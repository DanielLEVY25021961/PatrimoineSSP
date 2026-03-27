#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
tools/ai/pack_reading_list.py

Génère la liste complète des URLs Raw SHA ou des paths résolus
pour un pack défini dans docs/ai/perimetre.yaml.

La résolution du périmètre est entièrement déléguée à
`tools/ai/perimetre_resolver.py`, qui constitue l'API canonique.
"""

from __future__ import annotations

import argparse
import sys
from pathlib import Path

from perimetre_resolver import (
    get_project_owner_repo,
    load_perimetre,
    make_raw_sha_url,
    resolve_pack,
)


def _eprint(message: str) -> None:
    """
    Écrit un message sur stderr.
    """
    print(message, file=sys.stderr)


def main() -> int:
    """
    Point d'entrée CLI.
    """
    parser = argparse.ArgumentParser(
        prog="pack_reading_list.py",
        description=(
            "Résout un pack du perimetre.yaml puis imprime ses paths "
            "ou ses URLs Raw SHA."
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
        help="Imprime uniquement les paths résolus, sans générer les URLs Raw SHA.",
    )

    args = parser.parse_args()

    repo_root = Path.cwd()
    perimetre_path = (repo_root / args.perimetre).resolve()

    if not perimetre_path.exists():
        _eprint(f"perimetre.yaml introuvable : {perimetre_path}")
        return 2

    try:
        perimetre = load_perimetre(perimetre_path)
        owner, repo = get_project_owner_repo(perimetre)

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

        if args.print_paths:
            for path in resolution.files:
                print(path)
            return 0

        for path in resolution.files:
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