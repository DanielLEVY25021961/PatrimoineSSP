#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Reconstruit des URLs Raw GitHub au SHA à partir :
- d'un fichier de paths ;
- ou d'un ou plusieurs packs du perimetre.
"""

from __future__ import annotations

import argparse
from pathlib import Path

from perimetre_resolver import (
    build_raw_sha_urls,
    read_paths_txt,
    resolve_pack_paths,
)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Résout les URLs Raw au SHA.")
    parser.add_argument("--sha", required=True, help="SHA Git unique")
    parser.add_argument(
        "--repo-root",
        default=".",
        help="Racine du dépôt local (défaut: .)",
    )
    parser.add_argument(
        "--perimetre",
        default="docs/ai/perimetre.yaml",
        help="Chemin vers docs/ai/perimetre.yaml",
    )
    parser.add_argument(
        "--input",
        default="",
        help="Fichier de paths (ex: tools/ai/paths.txt)",
    )
    parser.add_argument(
        "--packs",
        nargs="*",
        default=[],
        help="Nom(s) de pack à résoudre via perimetre.yaml",
    )
    parser.add_argument(
        "--output",
        default="",
        help="Fichier de sortie optionnel",
    )
    return parser.parse_args()


def main() -> int:
    args = parse_args()

    if args.input:
        paths = read_paths_txt(Path(args.input))
    else:
        packs = list(args.packs) if args.packs else ["couche_ia"]
        paths = resolve_pack_paths(
            repo_root=Path(args.repo_root),
            perimetre_path=Path(args.perimetre),
            pack_names=packs,
        )

    urls = build_raw_sha_urls(
        perimetre_path=Path(args.perimetre),
        sha=args.sha,
        paths=paths,
    )

    content = "\n".join(urls) + ("\n" if urls else "")

    if args.output:
        Path(args.output).write_text(content, encoding="utf-8")
    else:
        print(content, end="")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())