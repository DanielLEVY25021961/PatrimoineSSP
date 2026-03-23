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
class ParsedInput:
    path: str
    owner: Optional[str] = None
    repo: Optional[str] = None
    branch: Optional[str] = None
    sha: Optional[str] = None


def _extract_path_from_ref_heads(url: str) -> Optional[ParsedInput]:
    m = RAW_REFS_HEADS_PREFIX.match(url.strip())
    if not m:
        return None
    return ParsedInput(
        owner=m.group("owner"),
        repo=m.group("repo"),
        branch=m.group("branch"),
        path=m.group("path"),
    )


def _extract_path_from_raw_sha(url: str) -> Optional[ParsedInput]:
    m = RAW_SHA_PREFIX.match(url.strip())
    if not m:
        return None
    return ParsedInput(
        owner=m.group("owner"),
        repo=m.group("repo"),
        sha=m.group("sha"),
        path=m.group("path"),
    )


def _normalize_path(s: str) -> str:
    s2 = s.strip()
    if not s2:
        return ""
    if s2.startswith("/"):
        s2 = s2[1:]
    return s2


def _iter_input_lines(path: str) -> Iterable[str]:
    with open(path, "r", encoding="utf-8") as f:
        for line in f:
            line2 = line.strip()
            if not line2:
                continue
            if line2.startswith("#"):
                continue
            yield line2


def _make_raw_sha_url(owner: str, repo: str, sha: str, path: str) -> str:
    return f"https://raw.githubusercontent.com/{owner}/{repo}/{sha}/{path}"


def _collect_paths(
    direct_paths: List[str],
    input_file: Optional[str],
) -> List[str]:

    out: List[str] = []

    for p in direct_paths:
        p2 = _normalize_path(p)
        if p2:
            out.append(p2)

    if input_file:
        for line in _iter_input_lines(input_file):
            parsed = _extract_path_from_ref_heads(line)
            if parsed is not None:
                p3 = _normalize_path(parsed.path)
                if p3:
                    out.append(p3)
                continue

            parsed2 = _extract_path_from_raw_sha(line)
            if parsed2 is not None:
                p4 = _normalize_path(parsed2.path)
                if p4:
                    out.append(p4)
                continue

            p5 = _normalize_path(line)
            if p5:
                out.append(p5)

    return out


def main() -> int:

    parser = argparse.ArgumentParser(description="Resolve GitHub raw SHA URLs from paths or refs/heads URLs.")
    parser.add_argument("--owner", default="DanielLEVY25021961", help="GitHub owner")
    parser.add_argument("--repo", default="PatrimoineSSP", help="GitHub repo")
    parser.add_argument("--sha", required=True, help="Git SHA (7-40 hex)")
    parser.add_argument("--path", action="append", default=[], help="Path (repeatable)")
    parser.add_argument("--input", help="Input file containing paths and/or raw URLs")
    parser.add_argument("--print-paths", action="store_true", help="Print only normalized paths (no URLs)")

    args = parser.parse_args()

    paths = _collect_paths(args.path, args.input)
    if not paths:
        sys.stderr.write("No paths provided.\n")
        return 2

    if args.print_paths:
        for p in paths:
            sys.stdout.write(p + "\n")
        return 0

    for p in paths:
        sys.stdout.write(_make_raw_sha_url(args.owner, args.repo, args.sha, p) + "\n")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())