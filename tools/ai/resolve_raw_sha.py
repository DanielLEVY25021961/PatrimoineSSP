# tools/ai/resolve_raw_sha.py
# -*- coding: utf-8 -*-

"""
resolve_raw_sha.py

But :
- Résoudre des URLs Raw SHA à partir :
  1) d'un pack hybride défini dans docs/ai/perimetre.yaml
  2) et/ou de paths fournis directement
  3) et/ou d'un fichier d'entrée contenant des paths / URLs

Le script reste compatible avec l'ancien usage manuel,
tout en supportant désormais la résolution hybride via pack.

Exemples :

1) Depuis un pack hybride :
   python tools/ai/resolve_raw_sha.py --sha <SHA> --pack ai

2) Depuis un pack hybride en n'imprimant que les paths :
   python tools/ai/resolve_raw_sha.py --sha <SHA> --pack dto --print-paths

3) Depuis des paths manuels :
   python tools/ai/resolve_raw_sha.py --sha <SHA> \
       --path docs/ai/CONTRAT_IA.md \
       --path docs/ai/MANIFEST_IA.yaml

4) Depuis un fichier d'entrée contenant des paths et/ou URLs Raw :
   python tools/ai/resolve_raw_sha.py --sha <SHA> --input tools/ai/paths.txt

5) Depuis un pack hybride + des paths manuels :
   python tools/ai/resolve_raw_sha.py --sha <SHA> --pack ai \
       --path src/main/java/.../MaClasse.java
"""

from __future__ import annotations

import argparse
import re
import sys
from dataclasses import dataclass
from pathlib import PurePosixPath
from typing import Iterable, List, Optional

from pack_reading_list import _get_repo_slug, _load_perimetre, _resolve_pack_paths


RAW_REFS_HEADS_PREFIX = re.compile(
    r"^https://raw\.githubusercontent\.com/"
    r"(?P<owner>[^/]+)/(?P<repo>[^/]+)/refs/heads/(?P<branch>[^/]+)/(?P<path>.+)$"
)

RAW_SHA_PREFIX = re.compile(
    r"^https://raw\.githubusercontent\.com/"
    r"(?P<owner>[^/]+)/(?P<repo>[^/]+)/(?P<sha>[0-9a-f]{7,40})/(?P<path>.+)$",
    re.IGNORECASE,
)

REPO_FALLBACK = "DanielLEVY25021961/PatrimoineSSP"


@dataclass(frozen=True)
class ParsedInput:
    """
    Représente une ligne d'entrée déjà interprétée.
    """

    path: str
    owner: Optional[str] = None
    repo: Optional[str] = None
    branch: Optional[str] = None
    sha: Optional[str] = None


def _eprint(message: str) -> None:
    """
    Ecrit un message sur stderr.
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


def _extract_path_from_ref_heads(url: str) -> Optional[ParsedInput]:
    """
    Extrait le path d'une URL Raw refs/heads.
    """
    match = RAW_REFS_HEADS_PREFIX.match(url.strip())
    if not match:
        return None

    return ParsedInput(
        owner=match.group("owner"),
        repo=match.group("repo"),
        branch=match.group("branch"),
        path=match.group("path"),
    )


def _extract_path_from_raw_sha(url: str) -> Optional[ParsedInput]:
    """
    Extrait le path d'une URL Raw SHA.
    """
    match = RAW_SHA_PREFIX.match(url.strip())
    if not match:
        return None

    return ParsedInput(
        owner=match.group("owner"),
        repo=match.group("repo"),
        sha=match.group("sha"),
        path=match.group("path"),
    )


def _iter_input_lines(path: str) -> Iterable[str]:
    """
    Itère sur les lignes significatives d'un fichier d'entrée.
    """
    with open(path, "r", encoding="utf-8") as handle:
        for line in handle:
            stripped = line.strip()
            if not stripped:
                continue
            if stripped.startswith("#"):
                continue
            yield stripped


def _make_raw_sha_url(owner: str, repo: str, sha: str, path: str) -> str:
    """
    Construit une URL Raw SHA canonique.
    """
    return f"https://raw.githubusercontent.com/{owner}/{repo}/{sha}/{path}"


def _collect_manual_paths(
    direct_paths: List[str],
    input_file: Optional[str],
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
            parsed_ref_heads = _extract_path_from_ref_heads(line)
            if parsed_ref_heads is not None:
                normalized_ref_heads = _normalize_path(parsed_ref_heads.path)
                if normalized_ref_heads:
                    result.append(normalized_ref_heads)
                continue

            parsed_raw_sha = _extract_path_from_raw_sha(line)
            if parsed_raw_sha is not None:
                normalized_raw_sha = _normalize_path(parsed_raw_sha.path)
                if normalized_raw_sha:
                    result.append(normalized_raw_sha)
                continue

            normalized_line = _normalize_path(line)
            if normalized_line:
                result.append(normalized_line)

    return sorted(set(result))


def _resolve_repo_slug(
    owner: Optional[str],
    repo: Optional[str],
    perimetre_data: Optional[dict],
) -> str:
    """
    Détermine le slug owner/repo final.

    Priorité :
    1) --owner + --repo explicites
    2) project.owner/project.repo de perimetre.yaml
    3) fallback constant
    """
    if owner and repo:
        return f"{owner}/{repo}"

    if perimetre_data is not None:
        return _get_repo_slug(perimetre_data)

    return REPO_FALLBACK


def main() -> int:
    """
    Point d'entrée CLI.
    """
    parser = argparse.ArgumentParser(
        description=(
            "Résout des URLs Raw SHA à partir d'un pack hybride et/ou de "
            "paths explicites."
        )
    )
    parser.add_argument("--owner", help="GitHub owner (optionnel)")
    parser.add_argument("--repo", help="GitHub repo (optionnel)")
    parser.add_argument("--sha", required=True, help="Git SHA (7-40 hex)")
    parser.add_argument("--pack", help="Nom d'un pack du perimetre.yaml, ou 'all'")
    parser.add_argument(
        "--perimetre",
        default="docs/ai/perimetre.yaml",
        help="Chemin du perimetre.yaml (défaut : docs/ai/perimetre.yaml)",
    )
    parser.add_argument("--path", action="append", default=[], help="Path manuel (répétable)")
    parser.add_argument("--input", help="Fichier d'entrée contenant des paths et/ou URLs Raw")
    parser.add_argument(
        "--print-paths",
        action="store_true",
        help="Imprime uniquement les paths résolus (sans générer les URLs)",
    )

    args = parser.parse_args()

    manual_paths = _collect_manual_paths(
        direct_paths=args.path,
        input_file=args.input,
    )

    perimetre_data: Optional[dict] = None
    pack_paths: List[str] = []

    try:
        if args.pack:
            from pathlib import Path

            repo_root = Path.cwd()
            perimetre_path = (repo_root / args.perimetre).resolve()

            if not perimetre_path.exists():
                _eprint(f"perimetre.yaml introuvable : {perimetre_path}")
                return 2

            perimetre_data = _load_perimetre(perimetre_path)

            pack_paths, missing_roots = _resolve_pack_paths(
                perimetre=perimetre_data,
                repo_root=repo_root,
                sha=args.sha.strip(),
                pack_name=args.pack.strip(),
            )

            if missing_roots:
                _eprint("incident de lecture")
                for missing_root in missing_roots:
                    _eprint(f"Root obligatoire manquante : {missing_root}")
                return 3

        all_paths = sorted(set(pack_paths + manual_paths))

        if not all_paths:
            _eprint("No paths provided.")
            return 2

        repo_slug = _resolve_repo_slug(
            owner=args.owner.strip() if args.owner else None,
            repo=args.repo.strip() if args.repo else None,
            perimetre_data=perimetre_data,
        )

        owner, repo = repo_slug.split("/", 1)

        if args.print_paths:
            for path in all_paths:
                sys.stdout.write(path + "\n")
            return 0

        for path in all_paths:
            sys.stdout.write(_make_raw_sha_url(owner, repo, args.sha.strip(), path) + "\n")

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