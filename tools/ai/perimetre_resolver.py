#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
tools/ai/perimetre_resolver.py

Résolveur canonique du périmètre IA.

Responsabilités :
- Charger docs/ai/perimetre.yaml
- Résoudre un pack hybride :
  * paths explicites
  * roots récursifs filtrés par globs
- Dédupliquer et trier les paths
- Travailler au SHA via git quand possible
- Rebasculer localement si le SHA n'est pas disponible localement
- Résoudre le bootstrap minimal IA à partir de :
  * docs/ai/MANIFEST_IA.yaml
  * tools/ai/paths.txt

Ce module est la source unique de vérité pour la résolution des packs IA.
"""

from __future__ import annotations

import re
import subprocess
from dataclasses import dataclass
from pathlib import Path
from typing import Dict, Iterable, List, Optional, Sequence, Set, Tuple

import yaml


RAW_REFS_HEADS_PREFIX = re.compile(
    r"^https://raw\.githubusercontent\.com/"
    r"(?P<owner>[^/]+)/(?P<repo>[^/]+)/refs/heads/(?P<branch>[^/]+)/(?P<path>.+)$"
)

RAW_SHA_PREFIX = re.compile(
    r"^https://raw\.githubusercontent\.com/"
    r"(?P<owner>[^/]+)/(?P<repo>[^/]+)/(?P<sha>[0-9a-fA-F]{7,40})/(?P<path>.+)$"
)


@dataclass(frozen=True)
class RootSelector:
    """
    Décrit une racine à explorer.
    """

    path: str
    recursive: bool
    include_globs: Tuple[str, ...]
    exclude_globs: Tuple[str, ...]
    allow_missing: bool


@dataclass(frozen=True)
class PackResolution:
    """
    Résultat d'une résolution de pack.
    """

    pack_name: str
    files: Tuple[str, ...]
    explicit_paths: Tuple[str, ...]
    root_files: Tuple[str, ...]
    resolved_roots: Tuple[str, ...]
    missing_roots: Tuple[str, ...]


@dataclass(frozen=True)
class BootstrapResolution:
    """
    Résultat de résolution du bootstrap IA.
    """

    manifest_path: str
    paths_txt_path: str
    files: Tuple[str, ...]
    missing_files: Tuple[str, ...]
    
    @dataclass(frozen=True)
    
class LayerDefinition:
    """
    Définition canonique d'une couche.
    """

    layer_name: str
    status: str
    mandatory_pre_read: bool
    packs: Tuple[str, ...]
    pivot_paths: Tuple[str, ...]
    contract_paths: Tuple[str, ...]
    sublayers: Tuple[str, ...]


@dataclass(frozen=True)
class LayerResolution:
    """
    Résultat de résolution d'une couche canonique.
    """

    layer_name: str
    files: Tuple[str, ...]
    pack_names: Tuple[str, ...]
    pivot_paths: Tuple[str, ...]
    contract_paths: Tuple[str, ...]
    missing_roots: Tuple[str, ...]


def _read_text(path: Path) -> str:
    """
    Lit un fichier texte UTF-8.
    """
    return path.read_text(encoding="utf-8")


def _run_git(args: Sequence[str], cwd: Path) -> Tuple[int, bytes, bytes]:
    """
    Exécute git et retourne (returncode, stdout, stderr).
    """
    try:
        completed = subprocess.run(
            ["git", *args],
            cwd=str(cwd),
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            check=False,
        )
        return completed.returncode, completed.stdout, completed.stderr
    except FileNotFoundError:
        return 127, b"", b"git introuvable"


def _git_is_available(repo_root: Path) -> bool:
    """
    Indique si git est disponible.
    """
    code, _, _ = _run_git(["--version"], repo_root)
    return code == 0


def _git_has_object(repo_root: Path, sha: str) -> bool:
    """
    Indique si le SHA est résolvable localement.
    """
    code, _, _ = _run_git(["cat-file", "-e", f"{sha}^{{commit}}"], repo_root)
    return code == 0


def git_can_read_sha(repo_root: Path, sha: Optional[str]) -> bool:
    """
    Indique si git peut lire le SHA fourni.
    """
    return bool(sha) and _git_is_available(repo_root) and _git_has_object(repo_root, sha)


def _git_show_file(repo_root: Path, sha: str, rel_path: str) -> Optional[bytes]:
    """
    Retourne le contenu binaire de sha:path, ou None si échec.
    """
    code, out, _ = _run_git(["show", f"{sha}:{rel_path}"], repo_root)
    if code != 0:
        return None
    return out


def read_bytes_from_repo(
    *,
    repo_root: Path,
    rel_path: str,
    sha: Optional[str] = None,
    git_ok: Optional[bool] = None,
) -> Optional[bytes]:
    """
    Lit un fichier depuis git au SHA si possible, sinon localement.
    """
    effective_git_ok = git_can_read_sha(repo_root, sha) if git_ok is None else git_ok

    if effective_git_ok and sha:
        content = _git_show_file(repo_root, sha, rel_path)
        if content is not None:
            return content

    local_file = repo_root / rel_path
    if local_file.exists() and local_file.is_file():
        return local_file.read_bytes()

    return None


def read_text_from_repo(
    *,
    repo_root: Path,
    rel_path: str,
    sha: Optional[str] = None,
    git_ok: Optional[bool] = None,
) -> Optional[str]:
    """
    Lit un fichier texte UTF-8 depuis git au SHA si possible, sinon localement.
    """
    content_bytes = read_bytes_from_repo(
        repo_root=repo_root,
        rel_path=rel_path,
        sha=sha,
        git_ok=git_ok,
    )
    if content_bytes is None:
        return None
    return content_bytes.decode("utf-8")


def _safe_relpath(path_str: str) -> str:
    """
    Normalise un path de repo et interdit toute sortie du dépôt.
    """
    normalized = path_str.replace("\\", "/").strip()
    if not normalized:
        raise ValueError("Path vide interdit.")

    path_obj = Path(normalized)

    if path_obj.is_absolute():
        raise ValueError(f"Path absolu interdit : {path_str}")

    parts = [part for part in path_obj.parts if part not in ("", ".")]
    if any(part == ".." for part in parts):
        raise ValueError(f"Path parent '..' interdit : {path_str}")

    return str(Path(*parts)).replace("\\", "/")


def _load_yaml(path: Path) -> Dict:
    """
    Charge un fichier YAML.
    """
    raw = _read_text(path)
    data = yaml.safe_load(raw)

    if not isinstance(data, dict):
        raise ValueError(f"YAML invalide : {path}")

    return data


def load_perimetre(perimetre_path: Path) -> Dict:
    """
    Charge docs/ai/perimetre.yaml.
    """
    return _load_yaml(perimetre_path)


def get_project_owner_repo(perimetre: Dict) -> Tuple[str, str]:
    """
    Retourne (owner, repo) à partir de perimetre.yaml.
    """
    project = perimetre.get("project")
    if not isinstance(project, dict):
        raise ValueError("perimetre.yaml : section 'project' manquante ou invalide.")

    owner = str(project.get("owner", "")).strip()
    repo = str(project.get("repo", "")).strip()

    if not owner or not repo:
        raise ValueError("perimetre.yaml : owner/repo manquants.")

    return owner, repo


def make_raw_sha_url(owner: str, repo: str, sha: str, path: str) -> str:
    """
    Construit une URL Raw SHA canonique.
    """
    return f"https://raw.githubusercontent.com/{owner}/{repo}/{sha}/{path}"


def list_pack_names(perimetre: Dict) -> List[str]:
    """
    Liste les packs déclarés.
    """
    packs = perimetre.get("packs")
    if not isinstance(packs, dict):
        raise ValueError("perimetre.yaml : section 'packs' manquante ou invalide.")

    return list(packs.keys())


def _glob_to_regex(pattern: str) -> re.Pattern[str]:
    """
    Convertit un glob POSIX simplifié en regex.
    Supporte :
    - **
    - **/
    - *
    - ?
    """
    i = 0
    regex = "^"

    while i < len(pattern):
        if pattern[i:i + 3] == "**/":
            regex += "(?:.*/)?"
            i += 3
            continue

        if pattern[i:i + 2] == "**":
            regex += ".*"
            i += 2
            continue

        char = pattern[i]

        if char == "*":
            regex += "[^/]*"
        elif char == "?":
            regex += "[^/]"
        else:
            regex += re.escape(char)

        i += 1

    regex += "$"
    return re.compile(regex)


def _matches_globs(
    rel_path_from_root: str,
    include_globs: Sequence[str],
    exclude_globs: Sequence[str],
) -> bool:
    """
    Vérifie si un path relatif à une root doit être retenu.
    """
    include_patterns = list(include_globs) if include_globs else ["**/*"]

    included = any(
        _glob_to_regex(pattern).match(rel_path_from_root)
        for pattern in include_patterns
    )
    if not included:
        return False

    excluded = any(
        _glob_to_regex(pattern).match(rel_path_from_root)
        for pattern in exclude_globs
    )

    return not excluded


def _parse_root_selector(item: Dict) -> RootSelector:
    """
    Parse une entrée de roots.
    """
    if not isinstance(item, dict):
        raise ValueError("Root selector invalide (attendu : dict).")

    raw_path = item.get("path")
    if raw_path is None:
        raise ValueError("Root selector invalide : 'path' manquant.")

    include_globs = item.get("include_globs") or ["**/*"]
    exclude_globs = item.get("exclude_globs") or []

    if not isinstance(include_globs, list):
        raise ValueError("Root selector invalide : include_globs doit être une liste.")
    if not isinstance(exclude_globs, list):
        raise ValueError("Root selector invalide : exclude_globs doit être une liste.")

    return RootSelector(
        path=_safe_relpath(str(raw_path)),
        recursive=bool(item.get("recursive", True)),
        include_globs=tuple(str(x) for x in include_globs),
        exclude_globs=tuple(str(x) for x in exclude_globs),
        allow_missing=bool(item.get("allow_missing", False)),
    )


def _git_list_files_under(
    repo_root: Path,
    sha: str,
    rel_root: str,
    recursive: bool,
) -> List[str]:
    """
    Liste les fichiers présents sous une root au SHA demandé.
    """
    args: List[str] = ["ls-tree"]
    if recursive:
        args.append("-r")
    args.extend(["--name-only", sha, "--", rel_root])

    code, out, _ = _run_git(args, repo_root)
    if code != 0:
        return []

    result: List[str] = []

    for line in out.decode("utf-8").splitlines():
        stripped = line.strip()
        if not stripped:
            continue

        normalized = _safe_relpath(stripped)
        rel_from_root = Path(normalized).relative_to(Path(rel_root)).as_posix()

        if not recursive and "/" in rel_from_root:
            continue

        result.append(normalized)

    return sorted(set(result))


def _list_local_files_under(
    repo_root: Path,
    rel_root: str,
    recursive: bool,
) -> List[str]:
    """
    Liste les fichiers présents localement sous une root.
    """
    base_dir = repo_root / rel_root
    if not base_dir.exists() or not base_dir.is_dir():
        return []

    iterator: Iterable[Path]
    if recursive:
        iterator = base_dir.rglob("*")
    else:
        iterator = base_dir.iterdir()

    result: List[str] = []

    for file_path in iterator:
        if not file_path.is_file():
            continue

        rel_path = file_path.relative_to(repo_root).as_posix()
        result.append(_safe_relpath(rel_path))

    return sorted(set(result))


def _resolve_root_files(
    repo_root: Path,
    sha: Optional[str],
    git_ok: bool,
    selector: RootSelector,
) -> Tuple[List[str], bool]:
    """
    Résout les fichiers d'une root.

    Retourne :
    - la liste filtrée des fichiers
    - un booléen indiquant si la root a réellement été trouvée
    """
    candidates: List[str] = []

    if git_ok and sha:
        candidates = _git_list_files_under(
            repo_root=repo_root,
            sha=sha,
            rel_root=selector.path,
            recursive=selector.recursive,
        )
    else:
        candidates = _list_local_files_under(
            repo_root=repo_root,
            rel_root=selector.path,
            recursive=selector.recursive,
        )

    if not candidates:
        return [], False

    filtered: List[str] = []

    for candidate in candidates:
        rel_from_root = Path(candidate).relative_to(Path(selector.path)).as_posix()

        if _matches_globs(
            rel_path_from_root=rel_from_root,
            include_globs=selector.include_globs,
            exclude_globs=selector.exclude_globs,
        ):
            filtered.append(candidate)

    return sorted(set(filtered)), True


def resolve_pack(
    *,
    perimetre_path: Path,
    repo_root: Path,
    pack_name: str,
    sha: Optional[str] = None,
) -> PackResolution:
    """
    Résout un pack donné.

    `pack_name` peut être :
    - le nom exact d'un pack
    - "all"
    """
    perimetre = load_perimetre(perimetre_path)
    packs = perimetre.get("packs")

    if not isinstance(packs, dict):
        raise ValueError("perimetre.yaml : section 'packs' manquante ou invalide.")

    if pack_name == "all":
        selected_pack_names = list(packs.keys())
    else:
        if pack_name not in packs:
            raise ValueError(f"Pack introuvable : {pack_name}")
        selected_pack_names = [pack_name]

    git_ok = git_can_read_sha(repo_root, sha)

    explicit_paths: List[str] = []
    root_files: List[str] = []
    resolved_roots: List[str] = []
    missing_roots: List[str] = []

    for selected_name in selected_pack_names:
        pack = packs.get(selected_name)
        if not isinstance(pack, dict):
            raise ValueError(f"Pack invalide : {selected_name}")

        pack_paths = pack.get("paths", []) or []
        if not isinstance(pack_paths, list):
            raise ValueError(f"Pack '{selected_name}' : 'paths' invalide.")

        for item in pack_paths:
            explicit_paths.append(_safe_relpath(str(item)))

        pack_roots = pack.get("roots", []) or []
        if not isinstance(pack_roots, list):
            raise ValueError(f"Pack '{selected_name}' : 'roots' invalide.")

        for root_item in pack_roots:
            selector = _parse_root_selector(root_item)

            resolved_files, root_found = _resolve_root_files(
                repo_root=repo_root,
                sha=sha,
                git_ok=git_ok,
                selector=selector,
            )

            if root_found:
                resolved_roots.append(selector.path)

            if not resolved_files:
                if not selector.allow_missing:
                    missing_roots.append(selector.path)
                continue

            root_files.extend(resolved_files)

    final_files = sorted(set(explicit_paths + root_files))

    return PackResolution(
        pack_name=pack_name,
        files=tuple(final_files),
        explicit_paths=tuple(sorted(set(explicit_paths))),
        root_files=tuple(sorted(set(root_files))),
        resolved_roots=tuple(sorted(set(resolved_roots))),
        missing_roots=tuple(sorted(set(missing_roots))),
    )


def _extract_path_from_bootstrap_line(line: str) -> Optional[str]:
    """
    Extrait un path normalisé depuis une ligne de bootstrap.
    Supporte :
    - paths relatifs
    - URLs Raw refs/heads
    - URLs Raw SHA
    """
    stripped = line.strip()
    if not stripped or stripped.startswith("#"):
        return None

    match_ref = RAW_REFS_HEADS_PREFIX.match(stripped)
    if match_ref:
        return _safe_relpath(match_ref.group("path"))

    match_sha = RAW_SHA_PREFIX.match(stripped)
    if match_sha:
        return _safe_relpath(match_sha.group("path"))

    return _safe_relpath(stripped)


def collect_bootstrap_paths(
    *,
    repo_root: Path,
    sha: Optional[str] = None,
    manifest_rel_path: str = "docs/ai/MANIFEST_IA.yaml",
    paths_txt_rel_path: str = "tools/ai/paths.txt",
) -> BootstrapResolution:
    """
    Résout le bootstrap minimal IA à partir de MANIFEST_IA.yaml et paths.txt.
    """
    git_ok = git_can_read_sha(repo_root, sha)

    result: Set[str] = set()
    missing_files: Set[str] = set()

    manifest_rel_path = _safe_relpath(manifest_rel_path)
    paths_txt_rel_path = _safe_relpath(paths_txt_rel_path)

    result.add(manifest_rel_path)
    result.add(paths_txt_rel_path)

    manifest_text = read_text_from_repo(
        repo_root=repo_root,
        rel_path=manifest_rel_path,
        sha=sha,
        git_ok=git_ok,
    )
    if manifest_text is None:
        missing_files.add(manifest_rel_path)
    else:
        manifest_data = yaml.safe_load(manifest_text)
        if isinstance(manifest_data, dict):
            for section_name in ("ai", "tools"):
                section = manifest_data.get(section_name)
                if not isinstance(section, dict):
                    continue
                for value in section.values():
                    if isinstance(value, str) and value.strip():
                        result.add(_safe_relpath(value))

    paths_text = read_text_from_repo(
        repo_root=repo_root,
        rel_path=paths_txt_rel_path,
        sha=sha,
        git_ok=git_ok,
    )
    if paths_text is None:
        missing_files.add(paths_txt_rel_path)
    else:
        for line in paths_text.splitlines():
            extracted = _extract_path_from_bootstrap_line(line)
            if extracted:
                result.add(extracted)

    existing_missing: Set[str] = set()

    for rel_path in result:
        content = read_bytes_from_repo(
            repo_root=repo_root,
            rel_path=rel_path,
            sha=sha,
            git_ok=git_ok,
        )
        if content is None:
            existing_missing.add(rel_path)

    return BootstrapResolution(
        manifest_path=manifest_rel_path,
        paths_txt_path=paths_txt_rel_path,
        files=tuple(sorted(result)),
        missing_files=tuple(sorted(existing_missing | missing_files)),
    )
    
    def _load_layers_metadata(perimetre: Dict) -> Dict:
    """
    Charge et valide la section 'couches' du perimetre.
    """
    couches = perimetre.get("couches")
    if not isinstance(couches, dict):
        raise ValueError("perimetre.yaml : section 'couches' manquante ou invalide.")

    definitions = couches.get("definitions")
    if not isinstance(definitions, dict):
        raise ValueError("perimetre.yaml : section 'couches.definitions' manquante ou invalide.")

    return couches


def list_layer_names(perimetre: Dict) -> List[str]:
    """
    Liste les couches canoniques déclarées.
    Respecte l'ordre canonique s'il est fourni.
    """
    couches = _load_layers_metadata(perimetre)
    ordered_names: List[str] = []

    raw_order = couches.get("ordre_canonique") or []
    if isinstance(raw_order, list):
        for item in raw_order:
            layer_name = str(item).strip()
            if layer_name and layer_name not in ordered_names:
                ordered_names.append(layer_name)

    definitions = couches.get("definitions") or {}
    for layer_name in definitions.keys():
        normalized = str(layer_name).strip()
        if normalized and normalized not in ordered_names:
            ordered_names.append(normalized)

    return ordered_names


def get_layer_definition(perimetre: Dict, layer_name: str) -> LayerDefinition:
    """
    Retourne la définition canonique d'une couche.
    """
    couches = _load_layers_metadata(perimetre)
    definitions = couches.get("definitions") or {}

    raw_definition = definitions.get(layer_name)
    if not isinstance(raw_definition, dict):
        raise ValueError(f"Couche introuvable : {layer_name}")

    packs_raw = raw_definition.get("packs", []) or []
    pivot_paths_raw = raw_definition.get("fichiers_pivots", []) or []
    contract_paths_raw = raw_definition.get("contract_paths", []) or raw_definition.get("contracts", []) or []
    sublayers_raw = raw_definition.get("sous_couches", []) or []

    if not isinstance(packs_raw, list):
        raise ValueError(f"Couche '{layer_name}' : 'packs' invalide.")
    if not isinstance(pivot_paths_raw, list):
        raise ValueError(f"Couche '{layer_name}' : 'fichiers_pivots' invalide.")
    if not isinstance(contract_paths_raw, list):
        raise ValueError(f"Couche '{layer_name}' : 'contract_paths' invalide.")
    if not isinstance(sublayers_raw, list):
        raise ValueError(f"Couche '{layer_name}' : 'sous_couches' invalide.")

    return LayerDefinition(
        layer_name=layer_name,
        status=str(raw_definition.get("statut", "")).strip(),
        mandatory_pre_read=bool(raw_definition.get("mandatory_pre_read", False)),
        packs=tuple(str(item).strip() for item in packs_raw if str(item).strip()),
        pivot_paths=tuple(_safe_relpath(str(item)) for item in pivot_paths_raw if str(item).strip()),
        contract_paths=tuple(_safe_relpath(str(item)) for item in contract_paths_raw if str(item).strip()),
        sublayers=tuple(str(item).strip() for item in sublayers_raw if str(item).strip()),
    )


def resolve_layer(
    *,
    perimetre_path: Path,
    repo_root: Path,
    layer_name: str,
    sha: Optional[str] = None,
) -> LayerResolution:
    """
    Résout une couche canonique en agrégeant les packs qui la composent
    et les contrats locaux/pivots associés.
    """
    perimetre = load_perimetre(perimetre_path)
    layer_definition = get_layer_definition(perimetre, layer_name)

    files: Set[str] = set(layer_definition.pivot_paths)
    files.update(layer_definition.contract_paths)

    missing_roots: Set[str] = set()

    for pack_name in layer_definition.packs:
        pack_resolution = resolve_pack(
            perimetre_path=perimetre_path,
            repo_root=repo_root,
            pack_name=pack_name,
            sha=sha,
        )

        files.update(pack_resolution.files)
        missing_roots.update(pack_resolution.missing_roots)

    return LayerResolution(
        layer_name=layer_name,
        files=tuple(sorted(files)),
        pack_names=tuple(layer_definition.packs),
        pivot_paths=tuple(sorted(set(layer_definition.pivot_paths))),
        contract_paths=tuple(sorted(set(layer_definition.contract_paths))),
        missing_roots=tuple(sorted(missing_roots)),
    )