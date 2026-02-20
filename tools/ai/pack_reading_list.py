#!/usr/bin/env python3
"""
pack_reading_list.py

Génère la liste complète des URLs RAW SHA
pour un pack donné.

Usage :
    python pack_reading_list.py <SHA> <PACK>
"""

import sys
import yaml

REPO = "DanielLEVY25021961/PatrimoineSSP"


def main() -> None:

    if len(sys.argv) != 3:
        print("Usage: python pack_reading_list.py <SHA> <PACK>")
        sys.exit(1)

    sha = sys.argv[1]
    pack_name = sys.argv[2]

    with open("docs/ai/perimetre.yaml", "r", encoding="utf-8") as f:
        data = yaml.safe_load(f)

    packs = data.get("packs", {})

    if pack_name not in packs:
        print(f"Pack inconnu : {pack_name}")
        sys.exit(2)

    for path in packs[pack_name].get("paths", []):
        url = f"https://raw.githubusercontent.com/{REPO}/{sha}/{path}"
        print(url)


if __name__ == "__main__":
    main()