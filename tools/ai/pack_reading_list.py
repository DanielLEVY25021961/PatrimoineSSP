#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Produit une liste de lecture à partir d'un ou plusieurs packs de perimetre.yaml.
"""

from __future__ import annotations

import argparse
from pathlib import Path

from perimetre_resolver import resolve_pack_paths


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Résout une liste de lecture IA.")
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
        "--packs",
        nargs="+",
        default=["couche_ia"],
        help="Nom(s) de pack à résoudre",
    )
    parser.add_argument(
        "--output",
        default="",
        help="Fichier de sortie optionnel",
    )
    return parser.parse_args()


def main() -> int:
    args = parse_args()

    resolved = resolve_pack_paths(
        repo_root=Path(args.repo_root),
        perimetre_path=Path(args.perimetre),
        pack_names=list(args.packs),
    )

    content = "\n".join(resolved) + ("\n" if resolved else "")

    if args.output:
        Path(args.output).write_text(content, encoding="utf-8")
    else:
        print(content, end="")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())