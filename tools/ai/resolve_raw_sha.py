
```python
# tools/ai/resolve_raw_sha.py
# -*- coding: utf-8 -*-

"""
Resolve GitHub raw SHA URLs from:
- a SHA + paths
- or GitHub raw branch URLs (refs/heads/...) -> extract path -> raw SHA URL

Usage examples:

1) From SHA + paths:
   python tools/ai/resolve_raw_sha.py --owner DanielLEVY25021961 --repo PatrimoineSSP --sha 22faad15... \
     --path src/main/java/.../TypeProduitGatewayIService.java \
     --path src/main/java/.../TypeProduitGatewayJPAService.java

2) From a text file containing paths and/or raw URLs:
   python tools/ai/resolve_raw_sha.py --owner DanielLEVY25021961 --repo PatrimoineSSP --sha 22faad15... \
     --input tools/ai/paths.txt

3) Print only paths extracted from refs/heads URLs:
   python tools/ai/resolve_raw_sha.py --sha 22faad15... --input tools/ai/paths.txt --print-paths
"""

from __future__ import annotations

import argparse
import re
import sys
from dataclasses import dataclass
from typing import Iterable, List, Optional


RAW_REFS_HEADS_PREFIX = re.compile(
    r"^https://raw\.githubusercontent\.com/(?P<owner>[^/]+)/(?P<repo>[^/]+)/refs/heads/(?P<branch>[^/]+)/(?P<path>.+)$"
)
RAW_SHA_PREFIX = re.compile(
    r"^https://raw\.githubusercontent\.com/(?P<owner>[^/]+)/(?P<repo>[^/]+)/(?P<sha>[0-9a-f]{7,40})/(?P<path>.+)$",
    re.IGNORECASE,
)


@dataclass(frozen=True)
class InputItem:
    raw: str
    path: Optional[str]


def _normalize_line(line: str) -> str:
    return line.strip()


def _extract_path_from_line(line: str) -> InputItem:
    """
    Returns InputItem(raw=line, path=maybe_path).
    If line is:
      - a raw refs/heads URL => extract path
      - a raw sha URL       => extract path
      - a plain path        => keep as path
      - empty/comment       => path=None
    """
    if not line:
        return InputItem(raw=line, path=None)

    if line.startswith("#"):
        return InputItem(raw=line, path=None)

    m1 = RAW_REFS_HEADS_PREFIX.match(line)
    if m1:
        return InputItem(raw=line, path=m1.group("path"))

    m2 = RAW_SHA_PREFIX.match(line)
    if m2:
        return InputItem(raw=line, path=m2.group("path"))

    # Assume it's a repo-relative path
    return InputItem(raw=line, path=line)


def _read_input_file(fp: str) -> List[InputItem]:
    items: List[InputItem] = []
    with open(fp, "rb") as f:
        for bline in f.readlines():
            line = bline.decode("utf-8", errors="replace")
            norm = _normalize_line(line)
            items.append(_extract_path_from_line(norm))
    return items


def _make_raw_sha_url(owner: str, repo: str, sha: str, path: str) -> str:
    path2 = path.lstrip("/")
    return f"https://raw.githubusercontent.com/{owner}/{repo}/{sha}/{path2}"


def _iter_paths(args: argparse.Namespace) -> Iterable[str]:
    # From --path
    for p in args.path or []:
        p2 = _normalize_line(p)
        if p2:
            yield p2

    # From --input file
    if args.input:
        for item in _read_input_file(args.input):
            if item.path:
                yield item.path


def main(argv: Optional[List[str]] = None) -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--owner", default="", help="GitHub owner (required if building URLs)")
    parser.add_argument("--repo", default="", help="GitHub repo (required if building URLs)")
    parser.add_argument("--sha", required=True, help="Unique SHA (required)")
    parser.add_argument("--path", action="append", default=[], help="Repo-relative path (repeatable)")
    parser.add_argument("--input", default="", help="Text file containing paths and/or raw URLs")
    parser.add_argument("--print-paths", action="store_true", help="Print extracted paths only")
    args = parser.parse_args(argv)

    paths = list(dict.fromkeys(_iter_paths(args)))  # deduplicate, keep order

    if args.print_paths:
        for p in paths:
            sys.stdout.write(p + "\n")
        return 0

    if not args.owner or not args.repo:
        sys.stderr.write("ERROR: --owner and --repo are required to build raw SHA URLs.\n")
        return 2

    for p in paths:
        sys.stdout.write(_make_raw_sha_url(args.owner, args.repo, args.sha, p) + "\n")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
