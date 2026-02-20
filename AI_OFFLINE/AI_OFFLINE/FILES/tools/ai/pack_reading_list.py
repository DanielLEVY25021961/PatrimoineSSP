# tools/ai/pack_reading_list.py
# -*- coding: utf-8 -*-

"""
Generate a GitHub raw SHA reading list from docs/ai/perimetre.yaml.

Goal:
- Read docs/ai/perimetre.yaml
- Select a "lot" (gateway / metier / persistance / dao / config / test_config)
- Output raw SHA URLs ready to paste in chat.

Output format (one URL per line):
https://raw.githubusercontent.com/{owner}/{repo}/{sha}/{path}

Usage:
  python tools/ai/pack_reading_list.py --sha 22faad15d41a61589a09ab8b4a5f4b5c683a7c40 --lot gateway
  python tools/ai/pack_reading_list.py --sha 22faad15... --lot metier
  python tools/ai/pack_reading_list.py --sha 22faad15... --lot persistance

Advanced:
  python tools/ai/pack_reading_list.py --sha 22faad15... --lot gateway --sub ports
  python tools/ai/pack_reading_list.py --sha 22faad15... --lot gateway --sub tests
  python tools/ai/pack_reading_list.py --sha 22faad15... --lot gateway --sub ports --sub adapters

  python tools/ai/pack_reading_list.py --sha 22faad15... --lot gateway --as-paths
  python tools/ai/pack_reading_list.py --sha 22faad15... --lot gateway --dedup

Notes:
- Requires PyYAML (pip install pyyaml) OR falls back to minimal YAML parsing for simple structures.
- Keeps the ordering from YAML (plus sub-keys ordering).
"""

from __future__ import annotations

import argparse
import sys
from dataclasses import dataclass
from pathlib import Path
from typing import Any, Dict, Iterable, List, Optional, Set, Tuple


DEFAULT_PERIMETRE = Path("docs/ai/perimetre.yaml")


def _eprint(msg: str) -> None:
    sys.stderr.write(msg + "\n")


def _make_raw_sha_url(owner: str, repo: str, sha: str, path: str) -> str:
    p = path.lstrip("/")
    return f"https://raw.githubusercontent.com/{owner}/{repo}/{sha}/{p}"


def _try_load_yaml_pyyaml(yaml_path: Path) -> Optional[Dict[str, Any]]:
    try:
        import yaml  # type: ignore
    except Exception:
        return None

    try:
        with yaml_path.open("rb") as f:
            data = yaml.safe_load(f)
        if isinstance(data, dict):
            return data
        return None
    except Exception:
        return None


def _minimal_yaml_load(yaml_path: Path) -> Dict[str, Any]:
    """
    Minimal YAML loader for the specific structure used in docs/ai/perimetre.yaml:
    - top-level scalars: owner, repo
    - nested dicts/lists for layers
    - lists of strings

    This is NOT a general YAML parser.
    If parsing fails, install PyYAML.
    """
    text = yaml_path.read_text(encoding="utf-8", errors="replace").splitlines()

    # Very small indentation-based loader for:
    # key:
    #   subkey:
    #     - "value"
    #     - "value"
    #   - "value"
    root: Dict[str, Any] = {}
    stack: List[Tuple[int, Any, Optional[str]]] = [(0, root, None)]

    def current_container() -> Any:
        return stack[-1][1]

    def push(indent: int, container: Any, key: Optional[str]) -> None:
        stack.append((indent, container, key))

    def pop_to(indent: int) -> None:
        while len(stack) > 1 and stack[-1][0] >= indent:
            stack.pop()

    for raw in text:
        line = raw.rstrip()
        if not line.strip() or line.strip().startswith("#"):
            continue

        indent = len(line) - len(line.lstrip(" "))
        line_stripped = line.lstrip(" ")

        # List item
        if line_stripped.startswith("- "):
            item = line_stripped[2:].strip()
            # remove surrounding quotes if any
            if (item.startswith('"') and item.endswith('"')) or (item.startswith("'") and item.endswith("'")):
                item = item[1:-1]
            cont = current_container()
            if not isinstance(cont, list):
                # create a list for the last key
                parent = stack[-2][1]
                last_key = stack[-1][2]
                if isinstance(parent, dict) and last_key:
                    parent[last_key] = []
                    stack[-1] = (stack[-1][0], parent[last_key], last_key)  # type: ignore
                    cont = current_container()
                else:
                    raise ValueError("Minimal YAML loader: unexpected list item position.")
            cont.append(item)
            continue

        # Key with value or key with nested block
        if ":" in line_stripped:
            key, rest = line_stripped.split(":", 1)
            key = key.strip()
            rest = rest.strip()

            pop_to(indent + 1)
            cont = current_container()
            if not isinstance(cont, dict):
                raise ValueError("Minimal YAML loader: expected dict container.")

            if rest == "":
                # nested
                # decide if next block likely dict; we default to dict
                cont[key] = {}
                push(indent + 1, cont[key], key)
            else:
                # scalar
                val = rest
                if (val.startswith('"') and val.endswith('"')) or (val.startswith("'") and val.endswith("'")):
                    val = val[1:-1]
                cont[key] = val
            continue

        raise ValueError(f"Minimal YAML loader: cannot parse line: {raw}")

    return root


def _load_perimetre(yaml_path: Path) -> Dict[str, Any]:
    data = _try_load_yaml_pyyaml(yaml_path)
    if data is not None:
        return data

    # Fallback minimal loader
    return _minimal_yaml_load(yaml_path)


def _ensure_list_of_strings(obj: Any) -> List[str]:
    if obj is None:
        return []
    if isinstance(obj, list):
        out: List[str] = []
        for x in obj:
            if isinstance(x, str):
                out.append(x)
        return out
    return []


def _collect_paths_for_lot(perimetre: Dict[str, Any], lot: str, subs: Optional[List[str]]) -> List[str]:
    layers = perimetre.get("layers", {})
    if not isinstance(layers, dict):
        raise ValueError("Invalid perimetre.yaml: missing 'layers' mapping.")

    if lot not in layers:
        available = ", ".join(sorted(layers.keys()))
        raise ValueError(f"Unknown lot '{lot}'. Available lots: {available}")

    lot_obj = layers[lot]

    # lot is a list directly
    if isinstance(lot_obj, list):
        return _ensure_list_of_strings(lot_obj)

    # lot is a mapping (e.g., gateway: {ports: [...], adapters: [...]})
    if isinstance(lot_obj, dict):
        if not subs:
            # all subkeys in YAML order (dict order preserved in Py3.7+)
            paths: List[str] = []
            for _, v in lot_obj.items():
                paths.extend(_ensure_list_of_strings(v))
            return paths

        # selected subkeys only (in the order they are provided)
        paths2: List[str] = []
        for sub in subs:
            if sub not in lot_obj:
                available_subs = ", ".join(lot_obj.keys())
                raise ValueError(f"Unknown sub '{sub}' for lot '{lot}'. Available subs: {available_subs}")
            paths2.extend(_ensure_list_of_strings(lot_obj[sub]))
        return paths2

    raise ValueError(f"Invalid perimetre.yaml: lot '{lot}' must be list or mapping.")


def _dedup_keep_order(items: Iterable[str]) -> List[str]:
    seen: Set[str] = set()
    out: List[str] = []
    for it in items:
        if it not in seen:
            seen.add(it)
            out.append(it)
    return out


def main(argv: Optional[List[str]] = None) -> int:
    parser = argparse.ArgumentParser(description="Pack a raw SHA reading list from docs/ai/perimetre.yaml")
    parser.add_argument("--sha", required=True, help="Unique Git SHA to build raw URLs")
    parser.add_argument("--lot", required=True, help="Lot name (gateway/metier/persistance/dao/config/test_config/...)")
    parser.add_argument("--sub", action="append", default=[], help="Sub-key for lots that are mappings (repeatable)")
    parser.add_argument("--perimetre", default=str(DEFAULT_PERIMETRE), help="Path to docs/ai/perimetre.yaml")
    parser.add_argument("--as-paths", action="store_true", help="Output repo-relative paths (not URLs)")
    parser.add_argument("--dedup", action="store_true", help="Deduplicate paths/URLs (keep first occurrence)")
    args = parser.parse_args(argv)

    perimetre_path = Path(args.perimetre)
    if not perimetre_path.exists():
        _eprint(f"ERROR: perimetre file not found: {perimetre_path}")
        return 2

    try:
        perimetre = _load_perimetre(perimetre_path)
    except Exception as e:
        _eprint("ERROR: Failed to parse perimetre.yaml.")
        _eprint(f"Reason: {e}")
        _eprint("Tip: install PyYAML: pip install pyyaml")
        return 3

    owner = str(perimetre.get("owner", "")).strip()
    repo = str(perimetre.get("repo", "")).strip()
    if not owner or not repo:
        _eprint("ERROR: perimetre.yaml must define top-level 'owner' and 'repo'.")
        return 4

    subs = args.sub if args.sub else None

    try:
        paths = _collect_paths_for_lot(perimetre, args.lot, subs)
    except Exception as e:
        _eprint(f"ERROR: {e}")
        return 5

    if args.dedup:
        paths = _dedup_keep_order(paths)

    if args.as_paths:
        for p in paths:
            sys.stdout.write(p + "\n")
        return 0

    for p in paths:
        sys.stdout.write(_make_raw_sha_url(owner, repo, args.sha, p) + "\n")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
