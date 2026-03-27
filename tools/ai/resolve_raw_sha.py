#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
tools/ai/resolve_raw_sha.py

Résout des URLs Raw SHA à partir :
- d'un pack hybride défini dans docs/ai/perimetre.yaml
- et/ou de paths fournis directement
- et/ou d'un fichier d'entrée contenant des paths / URLs

La résolution des packs est entièrement déléguée à
`tools/ai/perimetre_resolver.py`, qui constitue l'API canonique.
"""

from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path, PurePosixPath
from typing import Iterable, List, Optional

from perimetre_resolver import (
    get_project_owner_repo,
    load_perimetre,
    make_raw_sha_url,
    resolve_pack,
)

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
    Écrit un message sur stderr.
    """
    print(message, file=sys.stderr)


def _safe_relpath(path_str: str) -> str:
    """
    Normalise un path relatif au dépôt et interdit
    les paths absolus ou parents.
    """
    normalized = path_str.replace("\\", "/").strip()

    if not normalized:
        return ""

    path_obj = PurePosixPath(normalized)

    if path_obj.is_absolute():
        raise ValueError(f"Path absolu interdit : {path_str}")

    parts = [part for part in path_obj.parts if part not in ("", ".")]
    if any(part == ".." for part in parts):
        raise ValueError(f"Path parent '..' interdit : {path_str}")

    return str(PurePosixPath(*parts))


def _normalize_path(path_str: str) -> str:
    """
    Normalise un path manuel ou extrait d'une URL.
    """
    normalized = path_str.strip()
    if not normalized:
        return ""

    if normalized.startswith("/"):
        normalized = normalized[1:]

    return _safe_relpath(normalized)


def _extract_path_from_ref_heads(url: str) -> Optional[str]:
    """
    Extrait le path d'une URL Raw refs/heads.
    """
    match = RAW_REFS_HEADS_PREFIX.match(url.strip())
    if not match:
        return None

    return _normalize_path(match.group("path"))


def _extract_path_from_raw_sha(url: str) -> Optional[str]:
    """
    Extrait le path d'une URL Raw SHA.
    """
    match = RAW_SHA_PREFIX.match(url.strip())
    if not match:
        return None

    return _normalize_path(match.group("path"))


def _iter_input_lines(path: Path) -> Iterable[str]:
    """
    Itère sur les lignes significatives d'un fichier d'entrée.
    """
    with path.open("r", encoding="utf-8") as handle:
        for line in handle:
            stripped = line.strip()
            if not stripped or stripped.startswith("#"):
                continue
            yield stripped


def _collect_manual_paths(
    direct_paths: List[str],
    input_file: Optional[Path],
) -> List[str]:
    """
    Collecte les paths fournis manuellement via --path et/ou --input.
    """
    result: List[str] = []

    for direct_path in direct_paths:
        normalized = _normalize_path(direct_path)
        if normalized:
            result.append(normalized)

    if input_file:
        for line in _iter_input_lines(input_file):
            from_ref = _extract_path_from_ref_heads(line)
            if from_ref:
                result.append(from_ref)
                continue

            from_sha = _extract_path_from_raw_sha(line)
            if from_sha:
                result.append(from_sha)
                continue

            normalized = _normalize_path(line)
            if normalized:
                result.append(normalized)

    return sorted(set(result))


def _resolve_repo_owner_repo(
    owner_arg: Optional[str],
    repo_arg: Optional[str],
    perimetre_data: Optional[dict],
) -> tuple[str, str]:
    """
    Détermine le couple (owner, repo) final.

    Priorité :
    1) --owner + --repo explicites
    2) owner/repo du perimetre.yaml
    """
    if owner_arg and repo_arg:
        return owner_arg.strip(), repo_arg.strip()

    if (owner_arg and not repo_arg) or (repo_arg and not owner_arg):
        raise ValueError("--owner et --repo doivent être fournis ensemble.")

    if perimetre_data is not None:
        return get_project_owner_repo(perimetre_data)

    raise ValueError(
        "owner/repo introuvables : fournir --owner et --repo, ou utiliser --pack."
    )


def main() -> int:
    """
    Point d'entrée CLI.
    """
    parser = argparse.ArgumentParser(
        prog="resolve_raw_sha.py",
        description="Résout des URLs Raw SHA à partir d'un pack ou de paths explicites.",
    )
    parser.add_argument("--owner", help="GitHub owner (optionnel).")
    parser.add_argument("--repo", help="GitHub repo (optionnel).")
    parser.add_argument("--sha", required=True, help="SHA Git (7 à 40 hex).")
    parser.add_argument("--pack", help="Nom d'un pack du perimetre.yaml, ou 'all'.")
    parser.add_argument(
        "--perimetre",
        default="docs/ai/perimetre.yaml",
        help="Chemin du perimetre.yaml (défaut : docs/ai/perimetre.yaml).",
    )
    parser.add_argument("--path", action="append", default=[], help="Path manuel (répétable).")
    parser.add_argument("--input", help="Fichier d'entrée contenant des paths et/ou URLs Raw.")
    parser.add_argument(
        "--print-paths",
        action="store_true",
        help="Imprime uniquement les paths résolus, sans générer les URLs.",
    )

    args = parser.parse_args()

    repo_root = Path.cwd()
    perimetre_path = (repo_root / args.perimetre).resolve()

    manual_paths = _collect_manual_paths(
        direct_paths=args.path,
        input_file=Path(args.input).resolve() if args.input else None,
    )

    try:
        perimetre_data: Optional[dict] = None
        pack_paths: List[str] = []

        if args.pack:
            if not perimetre_path.exists():
                _eprint(f"perimetre.yaml introuvable : {perimetre_path}")
                return 2

            perimetre_data = load_perimetre(perimetre_path)
            resolution = resolve_pack(
                perimetre_path=perimetre_path,
                repo_root=repo_root,
                pack_name=args.pack.strip(),
                sha=args.sha.strip(),
            )

            if resolution.missing_roots:
                _eprint("incident de lecture")
                for missing_root in resolution.missing_roots:
                    _eprint(f"Root obligatoire manquante : {missing_root}")
                return 3

            pack_paths = list(resolution.files)

        all_paths = sorted(set(pack_paths + manual_paths))

        if not all_paths:
            _eprint("Aucun path fourni.")
            return 2

        owner, repo = _resolve_repo_owner_repo(
            owner_arg=args.owner,
            repo_arg=args.repo,
            perimetre_data=perimetre_data,
        )

        if args.print_paths:
            for path in all_paths:
                print(path)
            return 0

        for path in all_paths:
            print(make_raw_sha_url(owner, repo, args.sha.strip(), path))

        return 0

    except ValueError as exc:
        _eprint(str(exc))
        return 2
    except Exception as exc:
        _eprint("incident de lecture")
        _eprint(str(exc))
        return 3


if __name__ == "__main__":
    raise SystemExit(main())