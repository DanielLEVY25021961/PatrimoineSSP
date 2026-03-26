#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
pack_reading_list.py

Génère la liste complète des URLs Raw SHA pour un pack donné
à partir d'un périmètre hybride :

- paths explicites
- roots récursifs filtrés par globs

Usage :
    python pack_reading_list.py <SHA> <PACK>
    python pack_reading_list.py <SHA> <PACK> --print-paths

Exemples :
    python pack_reading_list.py 9acdd4ba08e955f240fbfc05a2b00cc0f07dcd28 ai
    python pack_reading_list.py 9acdd4ba08e955f240fbfc05a2b00cc0f07dcd28 dto --print-paths
    python pack_reading_list.py 9acdd4ba08e955f240fbfc05a2b00cc0f07dcd28 all
"""

from __future__ import annotations

import argparse
import re
import subprocess
import sys
from pathlib import Path, PurePosixPath
from typing import Dict, Iterable, List, Sequence, Tuple

import yaml


REPO_FALLBACK = "DanielLEVY25021961/PatrimoineSSP"


def _eprint(message: str) -> None:
    """
    Ecrit un message sur stderr.
    """
    print(message, file=sys.stderr)


def _run_git(args: Sequence[str], cwd: Path) -> Tuple[int, bytes, bytes]:
    """
    Exécute git et retourne :
    (returncode, stdout, stderr).
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
    Indique si le commit SHA est résolvable localement.
    """
    code, _, _ = _run_git(["cat-file", "-e", f"{sha}^{{commit}}"], repo_root)
    return code == 0


def _safe_relpath(path_str: str) -> str:
    """
    Normalise un path relatif au dépôt et interdit
    tout path absolu ou parent.
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


def _load_perimetre(perimetre_path: Path) -> Dict:
    """
    Charge docs/ai/perimetre.yaml.
    """
    with perimetre_path.open("r", encoding="utf-8") as handle:
        data = yaml.safe_load(handle)

    if not isinstance(data, dict):
        raise ValueError("perimetre.yaml invalide : racine non-dict.")

    return data


def _get_repo_slug(perimetre: Dict) -> str:
    """
    Détermine le slug owner/repo depuis perimetre.yaml.
    """
    project = perimetre.get("project")
    if isinstance(project, dict):
        owner = str(project.get("owner", "")).strip()
        repo = str(project.get("repo", "")).strip()
        if owner and repo:
            return f"{owner}/{repo}"

    return REPO_FALLBACK


def _get_selected_pack_names(packs: Dict, pack_name: str) -> List[str]:
    """
    Retourne la liste des packs à résoudre.
    """
    if pack_name == "all":
        return list(packs.keys())

    if pack_name not in packs:
        raise ValueError(f"Pack inconnu : {pack_name}")

    return [pack_name]


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
    code, out, _ = _run_git(
        ["ls-tree", "-r", "--name-only", sha, "--", rel_root],
        repo_root,
    )

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

    Retourne :
    - les fichiers retenus après filtrage
    - un booléen indiquant si la root a été trouvée
    """
    candidates: List[str] = []

    if git_ok:
        candidates = _git_list_files_under(
            repo_root=repo_root,
            sha=sha,
            rel_root=rel_root,
            recursive=recursive,
        )

    if not candidates:
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
    pack_name: str,
) -> Tuple[List[str], List[str]]:
    """
    Résout un pack hybride.

    Retourne :
    - la liste finale des paths
    - la liste des roots obligatoires manquantes
    """
    packs = perimetre.get("packs")
    if not isinstance(packs, dict):
        raise ValueError("perimetre.yaml : section 'packs' manquante ou invalide.")

    selected_pack_names = _get_selected_pack_names(packs, pack_name)

    git_ok = _git_is_available(repo_root) and _git_has_object(repo_root, sha)

    explicit_paths: List[str] = []
    root_files: List[str] = []
    missing_roots: List[str] = []

    for selected_name in selected_pack_names:
        pack = packs.get(selected_name)
        if not isinstance(pack, dict):
            raise ValueError(f"Pack invalide : {selected_name}")

        pack_paths = pack.get("paths", [])
        if pack_paths is None:
            pack_paths = []

        if not isinstance(pack_paths, list):
            raise ValueError(f"Pack '{selected_name}' : 'paths' invalide.")

        for item in pack_paths:
            explicit_paths.append(_safe_relpath(str(item)))

        pack_roots = pack.get("roots", [])
        if pack_roots is None:
            pack_roots = []

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

            if not root_found and not allow_missing:
                missing_roots.append(rel_root)
                continue

            root_files.extend(resolved_files)

    final_paths = sorted(set(explicit_paths + root_files))
    return final_paths, sorted(set(missing_roots))


def main() -> None:
    """
    Point d'entrée CLI.
    """
    parser = argparse.ArgumentParser(
        prog="pack_reading_list.py",
        description=(
            "Génère la liste complète des URLs Raw SHA pour un pack "
            "défini dans docs/ai/perimetre.yaml, en mode hybride "
            "(paths + roots + globs)."
        ),
    )
    parser.add_argument("sha", help="SHA Git à utiliser.")
    parser.add_argument("pack", help="Nom du pack à résoudre, ou 'all'.")
    parser.add_argument(
        "--perimetre",
        default="docs/ai/perimetre.yaml",
        help="Chemin du perimetre.yaml (défaut : docs/ai/perimetre.yaml).",
    )
    parser.add_argument(
        "--print-paths",
        action="store_true",
        help="Imprime uniquement les paths résolus, sans reconstruire les URLs Raw SHA.",
    )

    args = parser.parse_args()

    repo_root = Path.cwd()
    perimetre_path = (repo_root / args.perimetre).resolve()

    if not perimetre_path.exists():
        _eprint(f"perimetre.yaml introuvable : {perimetre_path}")
        sys.exit(2)

    try:
        perimetre = _load_perimetre(perimetre_path)
        repo_slug = _get_repo_slug(perimetre)

        paths, missing_roots = _resolve_pack_paths(
            perimetre=perimetre,
            repo_root=repo_root,
            sha=args.sha.strip(),
            pack_name=args.pack.strip(),
        )

        if missing_roots:
            _eprint("incident de lecture")
            for missing_root in missing_roots:
                _eprint(f"Root obligatoire manquante : {missing_root}")
            sys.exit(3)

        if args.print_paths:
            for path in paths:
                print(path)
            return

        for path in paths:
            print(f"https://raw.githubusercontent.com/{repo_slug}/{args.sha.strip()}/{path}")

    except ValueError as exc:
        _eprint(str(exc))
        sys.exit(2)
    except Exception as exc:
        _eprint("incident de lecture")
        _eprint(str(exc))
        sys.exit(3)


if __name__ == "__main__":
    main()