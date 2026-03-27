#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
tools/ai/make_offline_bundle.py

But :
- Construire un bundle OFFLINE reproductible au SHA donné.
- Le bundle contient tous les fichiers lisibles au SHA GitHub pour le lot demandé,
  selon le périmètre hybride défini dans docs/ai/perimetre.yaml.
- Le bundle ajoute aussi le bootstrap IA minimal dérivé de :
  * docs/ai/MANIFEST_IA.yaml
  * tools/ai/paths.txt
- Générer des métadonnées cohérentes et un PROVENANCE.yaml valide.

Entrées :
- --sha : SHA unique (obligatoire)
- --lot : pack (ou all)
- --perimetre : chemin du perimetre.yaml (optionnel)
- --out-dir : répertoire de sortie (zip ou dossier, selon --no-zip)

Sorties :
- Mode ZIP (défaut) :
  - <out-dir>/AI_OFFLINE_<sha>_<lot>.zip
- Mode NO-ZIP (--no-zip) :
  - <out-dir>/AI_OFFLINE/...

Stratégie d'extraction :
- Si git est disponible et que le SHA est résolvable localement :
  * résolution des roots via git ls-tree au SHA
  * extraction des fichiers via git show <sha>:<path>
  * aucun fallback local, afin de garantir la fidélité au SHA GitHub
- Sinon :
  * fallback local sur le working tree

Contrats :
- Génère AI_OFFLINE/INDEX.txt (liste triée des paths extraits)
- Génère AI_OFFLINE/CHECKSUMS.sha256 (sha256 pour chaque fichier extrait)
- Génère AI_OFFLINE/PROVENANCE.yaml valide (YAML)
- Génère AI_OFFLINE/MISSING.txt si certains fichiers/racines sont introuvables
"""

from __future__ import annotations

import argparse
import datetime
import hashlib
import platform
import re
import shutil
import subprocess
import sys
import tempfile
import zipfile
from pathlib import Path, PurePosixPath
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


def _eprint(message: str) -> None:
    """
    Ecrit un message sur stderr.
    """
    print(message, file=sys.stderr)


def _write_text(path: Path, content: str) -> None:
    """
    Ecrit un fichier texte UTF-8.
    """
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content, encoding="utf-8")


def _sha256_file(path: Path) -> str:
    """
    Calcule le SHA-256 d'un fichier.
    """
    digest = hashlib.sha256()

    with path.open("rb") as handle:
        for chunk in iter(lambda: handle.read(1024 * 1024), b""):
            digest.update(chunk)

    return digest.hexdigest()


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


def _git_show_file(repo_root: Path, sha: str, rel_path: str) -> Optional[bytes]:
    """
    Retourne le contenu binaire de sha:path, ou None si échec.
    """
    code, out, _ = _run_git(["show", f"{sha}:{rel_path}"], repo_root)
    if code != 0:
        return None
    return out


def _safe_relpath(path_str: str) -> str:
    """
    Normalise un path relatif au dépôt et interdit les paths absolus
    et les sorties du dépôt.
    """
    normalized = path_str.replace("\\", "/").strip()
    if not normalized:
        raise ValueError("Path vide interdit.")

    path_obj = PurePosixPath(normalized)

    if path_obj.is_absolute():
        raise ValueError(f"Path absolu interdit : {path_str}")

    parts = [part for part in path_obj.parts if part not in ("", ".")]
    if any(part == ".." for part in parts):
        raise ValueError(f"Path parent '..' interdit : {path_str}")

    return str(PurePosixPath(*parts))


def _load_yaml_file(path: Path) -> Dict:
    """
    Charge un fichier YAML depuis le disque.
    """
    data = yaml.safe_load(path.read_text(encoding="utf-8"))
    if not isinstance(data, dict):
        raise ValueError(f"YAML invalide : {path}")
    return data


def _get_pack_names(perimetre: Dict) -> List[str]:
    """
    Retourne la liste des packs déclarés.
    """
    packs = perimetre.get("packs")
    if not isinstance(packs, dict):
        raise ValueError("perimetre.yaml : section 'packs' manquante ou invalide.")
    return list(packs.keys())


def _get_selected_pack_names(perimetre: Dict, lot: str) -> List[str]:
    """
    Retourne la liste des packs à résoudre.
    """
    pack_names = _get_pack_names(perimetre)
    if lot == "all":
        return pack_names
    if lot not in pack_names:
        raise ValueError(f"Pack inconnu : {lot}")
    return [lot]


def _glob_to_regex(pattern: str) -> re.Pattern[str]:
    """
    Convertit un glob POSIX simplifié en regex.
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
    Indique si un path relatif à une root doit être retenu.
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
        rel_from_root = PurePosixPath(normalized).relative_to(PurePosixPath(rel_root)).as_posix()

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
    root_dir = repo_root / rel_root
    if not root_dir.exists() or not root_dir.is_dir():
        return []

    iterator: Iterable[Path]
    if recursive:
        iterator = root_dir.rglob("*")
    else:
        iterator = root_dir.glob("*")

    result: List[str] = []

    for file_path in iterator:
        if not file_path.is_file():
            continue
        rel_path = file_path.relative_to(repo_root).as_posix()
        result.append(_safe_relpath(rel_path))

    return sorted(set(result))


def _resolve_root_files(
    repo_root: Path,
    sha: str,
    git_ok: bool,
    rel_root: str,
    recursive: bool,
    include_globs: Sequence[str],
    exclude_globs: Sequence[str],
) -> Tuple[List[str], bool]:
    """
    Résout les fichiers d'une root.

    Si git_ok est vrai, la résolution est strictement basée sur Git au SHA.
    Sinon, fallback local.
    """
    candidates: List[str] = []

    if git_ok:
        candidates = _git_list_files_under(
            repo_root=repo_root,
            sha=sha,
            rel_root=rel_root,
            recursive=recursive,
        )
    else:
        candidates = _list_local_files_under(
            repo_root=repo_root,
            rel_root=rel_root,
            recursive=recursive,
        )

    if not candidates:
        return [], False

    retained: List[str] = []

    for candidate in candidates:
        rel_from_root = PurePosixPath(candidate).relative_to(PurePosixPath(rel_root)).as_posix()

        if _matches_globs(
            rel_path_from_root=rel_from_root,
            include_globs=include_globs,
            exclude_globs=exclude_globs,
        ):
            retained.append(candidate)

    return sorted(set(retained)), True


def _resolve_pack_paths(
    *,
    perimetre: Dict,
    repo_root: Path,
    sha: str,
    lot: str,
    git_ok: bool,
) -> Tuple[List[str], List[str], List[str], List[str], List[str]]:
    """
    Résout un lot hybride.

    Retourne :
    - paths finaux
    - explicit_paths
    - root_files
    - resolved_roots
    - missing_roots
    """
    packs = perimetre.get("packs")
    if not isinstance(packs, dict):
        raise ValueError("perimetre.yaml : section 'packs' manquante ou invalide.")

    selected_pack_names = _get_selected_pack_names(perimetre, lot)

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
            if not isinstance(root_item, dict):
                raise ValueError(f"Pack '{selected_name}' : root invalide.")

            rel_root = _safe_relpath(str(root_item.get("path", "")))
            recursive = bool(root_item.get("recursive", True))
            include_globs = root_item.get("include_globs") or ["**/*"]
            exclude_globs = root_item.get("exclude_globs") or []
            allow_missing = bool(root_item.get("allow_missing", False))

            if not isinstance(include_globs, list):
                raise ValueError(
                    f"Pack '{selected_name}' / root '{rel_root}' : include_globs invalide."
                )
            if not isinstance(exclude_globs, list):
                raise ValueError(
                    f"Pack '{selected_name}' / root '{rel_root}' : exclude_globs invalide."
                )

            resolved_files, root_found = _resolve_root_files(
                repo_root=repo_root,
                sha=sha,
                git_ok=git_ok,
                rel_root=rel_root,
                recursive=recursive,
                include_globs=[str(x) for x in include_globs],
                exclude_globs=[str(x) for x in exclude_globs],
            )

            if root_found:
                resolved_roots.append(rel_root)

            if not resolved_files:
                if not allow_missing:
                    missing_roots.append(rel_root)
                continue

            root_files.extend(resolved_files)

    final_paths = sorted(set(explicit_paths + root_files))

    return (
        final_paths,
        sorted(set(explicit_paths)),
        sorted(set(root_files)),
        sorted(set(resolved_roots)),
        sorted(set(missing_roots)),
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


def _read_text_from_git_or_local(
    repo_root: Path,
    sha: str,
    rel_path: str,
    git_ok: bool,
) -> Optional[str]:
    """
    Lit un fichier texte depuis git au SHA si possible, sinon localement.
    """
    content_bytes: Optional[bytes] = None

    if git_ok:
        content_bytes = _git_show_file(repo_root, sha, rel_path)
    else:
        local_file = repo_root / rel_path
        if local_file.exists() and local_file.is_file():
            content_bytes = local_file.read_bytes()

    if content_bytes is None:
        return None

    return content_bytes.decode("utf-8")


def _collect_manifest_bootstrap_paths(
    repo_root: Path,
    sha: str,
    git_ok: bool,
) -> List[str]:
    """
    Collecte les paths bootstrap dérivés du MANIFEST.
    """
    manifest_rel_path = "docs/ai/MANIFEST_IA.yaml"
    manifest_text = _read_text_from_git_or_local(
        repo_root=repo_root,
        sha=sha,
        rel_path=manifest_rel_path,
        git_ok=git_ok,
    )

    if manifest_text is None:
        return [manifest_rel_path]

    manifest_data = yaml.safe_load(manifest_text)
    if not isinstance(manifest_data, dict):
        return [manifest_rel_path]

    result: Set[str] = {manifest_rel_path}

    for section_name in ("ai", "tools"):
        section = manifest_data.get(section_name)
        if not isinstance(section, dict):
            continue
        for value in section.values():
            if isinstance(value, str) and value.strip():
                result.add(_safe_relpath(value))

    return sorted(result)


def _collect_paths_txt_bootstrap_paths(
    repo_root: Path,
    sha: str,
    git_ok: bool,
) -> List[str]:
    """
    Collecte les paths bootstrap à partir de tools/ai/paths.txt.
    """
    paths_rel_path = "tools/ai/paths.txt"
    paths_text = _read_text_from_git_or_local(
        repo_root=repo_root,
        sha=sha,
        rel_path=paths_rel_path,
        git_ok=git_ok,
    )

    if paths_text is None:
        return [paths_rel_path]

    result: Set[str] = {paths_rel_path}

    for line in paths_text.splitlines():
        extracted = _extract_path_from_bootstrap_line(line)
        if extracted:
            result.add(extracted)

    return sorted(result)


def _collect_bootstrap_paths(
    repo_root: Path,
    sha: str,
    git_ok: bool,
) -> List[str]:
    """
    Construit le bootstrap minimal du bundle.
    """
    result: Set[str] = set()
    result.update(_collect_manifest_bootstrap_paths(repo_root, sha, git_ok))
    result.update(_collect_paths_txt_bootstrap_paths(repo_root, sha, git_ok))
    return sorted(result)


def _zip_dir(src_dir: Path, zip_path: Path) -> None:
    """
    Zippe un dossier complet.
    """
    zip_path.parent.mkdir(parents=True, exist_ok=True)
    with zipfile.ZipFile(str(zip_path), "w", compression=zipfile.ZIP_DEFLATED) as archive:
        for file_path in src_dir.rglob("*"):
            if file_path.is_file():
                arcname = file_path.relative_to(src_dir).as_posix()
                archive.write(str(file_path), arcname)


def _compute_ai_offline_dir(out_dir: Path, do_zip: bool) -> Path:
    """
    Détermine le dossier AI_OFFLINE à écrire.
    """
    if do_zip:
        tmp_root = Path(tempfile.mkdtemp(prefix="AI_OFFLINE_BUILD_"))
        return tmp_root / "AI_OFFLINE"

    if out_dir.name.lower() == "ai_offline":
        return out_dir
    return out_dir / "AI_OFFLINE"


def _build_provenance_content(
    *,
    sha: str,
    lot: str,
    repo_root: Path,
    perimetre_path: Path,
    git_ok: bool,
    selected_packs: List[str],
    explicit_paths: List[str],
    root_files: List[str],
    resolved_roots: List[str],
    missing_roots: List[str],
    bootstrap_paths: List[str],
    extracted: List[str],
    missing_files: List[str],
) -> str:
    """
    Construit un PROVENANCE.yaml valide.
    """
    provenance = {
        "sha": sha,
        "lot": lot,
        "generated_at_utc": datetime.datetime.utcnow().replace(microsecond=0).isoformat() + "Z",
        "hostname": platform.node(),
        "system": platform.system(),
        "release": platform.release(),
        "python": platform.python_version(),
        "repo_root": repo_root.resolve().as_posix(),
        "perimetre_path": perimetre_path.as_posix(),
        "git_used": git_ok,
        "perimetre_mode": "hybrid",
        "selected_packs": selected_packs,
        "bootstrap_paths": bootstrap_paths,
        "explicit_paths_count": len(explicit_paths),
        "root_files_count": len(root_files),
        "resolved_roots_count": len(resolved_roots),
        "bootstrap_paths_count": len(bootstrap_paths),
        "extracted_count": len(extracted),
        "missing_roots_count": len(missing_roots),
        "missing_files_count": len(missing_files),
        "missing_count": len(missing_roots) + len(missing_files),
        "missing_roots": missing_roots,
        "missing_files": missing_files,
    }

    return yaml.safe_dump(
        provenance,
        allow_unicode=True,
        sort_keys=False,
        default_flow_style=False,
    )


def build_bundle(
    *,
    repo_root: Path,
    sha: str,
    lot: str,
    perimetre_path: Path,
    out_dir: Path,
    do_zip: bool,
) -> Path:
    """
    Construit AI_OFFLINE/ + CHECKSUMS + INDEX + PROVENANCE.
    Retourne le chemin du zip si do_zip, sinon le dossier AI_OFFLINE.
    """
    perimetre = _load_yaml_file(perimetre_path)
    selected_packs = _get_selected_pack_names(perimetre, lot)

    out_dir.mkdir(parents=True, exist_ok=True)

    ai_offline_dir = _compute_ai_offline_dir(out_dir=out_dir, do_zip=do_zip)
    files_dir = ai_offline_dir / "FILES"

    if ai_offline_dir.exists() and ai_offline_dir.is_dir():
        shutil.rmtree(ai_offline_dir)
    files_dir.mkdir(parents=True, exist_ok=True)

    git_ok = _git_is_available(repo_root) and _git_has_object(repo_root, sha)

    (
        resolved_paths,
        explicit_paths,
        root_files,
        resolved_roots,
        missing_roots,
    ) = _resolve_pack_paths(
        perimetre=perimetre,
        repo_root=repo_root,
        sha=sha,
        lot=lot,
        git_ok=git_ok,
    )

    bootstrap_paths = _collect_bootstrap_paths(
        repo_root=repo_root,
        sha=sha,
        git_ok=git_ok,
    )

    all_paths = sorted(set(resolved_paths + bootstrap_paths))

    extracted: List[str] = []
    missing_files: List[str] = []

    for rel_path in all_paths:
        content_bytes: Optional[bytes] = None

        if git_ok:
            content_bytes = _git_show_file(repo_root, sha, rel_path)
        else:
            local_file = repo_root / rel_path
            if local_file.exists() and local_file.is_file():
                content_bytes = local_file.read_bytes()

        if content_bytes is None:
            missing_files.append(rel_path)
            continue

        target = files_dir / rel_path
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_bytes(content_bytes)
        extracted.append(rel_path)

    extracted = sorted(set(extracted))
    missing_roots = sorted(set(missing_roots))
    missing_files = sorted(set(missing_files))

    index_content = "\n".join(extracted) + ("\n" if extracted else "")
    _write_text(ai_offline_dir / "INDEX.txt", index_content)

    checksums_lines: List[str] = []
    for rel_path in extracted:
        checksum = _sha256_file(files_dir / rel_path)
        checksums_lines.append(f"{checksum}  FILES/{rel_path}")

    checksums_content = "\n".join(checksums_lines) + ("\n" if checksums_lines else "")
    _write_text(ai_offline_dir / "CHECKSUMS.sha256", checksums_content)

    provenance_content = _build_provenance_content(
        sha=sha,
        lot=lot,
        repo_root=repo_root,
        perimetre_path=perimetre_path,
        git_ok=git_ok,
        selected_packs=selected_packs,
        explicit_paths=explicit_paths,
        root_files=root_files,
        resolved_roots=resolved_roots,
        missing_roots=missing_roots,
        bootstrap_paths=bootstrap_paths,
        extracted=extracted,
        missing_files=missing_files,
    )
    _write_text(ai_offline_dir / "PROVENANCE.yaml", provenance_content)

    if missing_roots or missing_files:
        missing_lines: List[str] = []

        if missing_roots:
            missing_lines.append("# Roots obligatoires manquantes")
            for rel_root in missing_roots:
                missing_lines.append(rel_root)
            missing_lines.append("")

        if missing_files:
            missing_lines.append("# Fichiers introuvables")
            for rel_path in missing_files:
                missing_lines.append(rel_path)

        missing_content = "\n".join(missing_lines).rstrip() + "\n"
        _write_text(ai_offline_dir / "MISSING.txt", missing_content)

    if do_zip:
        zip_name = f"AI_OFFLINE_{sha}_{lot}.zip"
        zip_path = out_dir / zip_name
        if zip_path.exists():
            zip_path.unlink()

        try:
            _zip_dir(ai_offline_dir, zip_path)
        finally:
            tmp_root = ai_offline_dir.parent
            if (
                tmp_root.exists()
                and tmp_root.is_dir()
                and tmp_root.name.startswith("AI_OFFLINE_BUILD_")
            ):
                shutil.rmtree(tmp_root, ignore_errors=True)

        return zip_path

    return ai_offline_dir


def main() -> int:
    """
    Point d'entrée CLI.
    """
    parser = argparse.ArgumentParser(
        prog="make_offline_bundle.py",
        formatter_class=argparse.RawTextHelpFormatter,
        description=(
            "Construit un bundle OFFLINE (AI_OFFLINE) au SHA donné, "
            "en incluant le périmètre hybride et le bootstrap IA minimal."
        ),
    )
    parser.add_argument("--sha", required=True, help="SHA unique (commit).")
    parser.add_argument(
        "--lot",
        required=True,
        help="Pack à extraire depuis docs/ai/perimetre.yaml, ou 'all'.",
    )
    parser.add_argument(
        "--perimetre",
        default="docs/ai/perimetre.yaml",
        help="Chemin du perimetre.yaml (par défaut : docs/ai/perimetre.yaml).",
    )
    parser.add_argument(
        "--out-dir",
        default=".",
        help=(
            "Répertoire de sortie.\n"
            "- Mode ZIP (défaut) : le zip est écrit dans ce répertoire.\n"
            "- Mode --no-zip : le dossier AI_OFFLINE est écrit dans ce répertoire.\n"
            "Astuce : passe --out-dir AI_OFFLINE pour obtenir AI_OFFLINE/AI_OFFLINE_<sha>_<lot>.zip "
            "sans créer AI_OFFLINE/AI_OFFLINE/."
        ),
    )
    parser.add_argument(
        "--no-zip",
        action="store_true",
        help="Ne pas zipper (garde le dossier AI_OFFLINE sur disque).",
    )

    args = parser.parse_args()

    repo_root = Path.cwd()
    perimetre_path = (repo_root / args.perimetre).resolve()

    if not perimetre_path.exists():
        _eprint(f"perimetre.yaml introuvable : {perimetre_path}")
        return 2

    out_dir = (repo_root / args.out_dir).resolve()
    if out_dir.exists() and not out_dir.is_dir():
        _eprint(f"--out-dir n'est pas un dossier : {out_dir}")
        return 2

    try:
        perimetre = _load_yaml_file(perimetre_path)
        valid_packs = _get_pack_names(perimetre) + ["all"]

        if args.lot.strip() not in valid_packs:
            _eprint(
                f"Pack inconnu : {args.lot.strip()} "
                f"(packs valides : {', '.join(valid_packs)})"
            )
            return 2

        result_path = build_bundle(
            repo_root=repo_root,
            sha=args.sha.strip(),
            lot=args.lot.strip(),
            perimetre_path=perimetre_path,
            out_dir=out_dir,
            do_zip=(not args.no_zip),
        )

    except Exception as exc:
        _eprint("incident de lecture")
        _eprint(str(exc))
        return 3

    print(str(result_path))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())