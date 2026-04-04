#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Résolveur canonique du périmètre IA.

API canonique partagée par :
- tools/ai/pack_reading_list.py
- tools/ai/resolve_raw_sha.py
- tools/ai/make_offline_bundle.py

Le résolveur sait :
- charger docs/ai/perimetre.yaml ;
- résoudre un ou plusieurs packs ;
- fusionner paths explicites + roots/globs ;
- dédupliquer et trier de façon stable ;
- tolérer des roots optionnelles via allow_missing.
"""

from __future__ import annotations

from dataclasses import dataclass
from fnmatch import fnmatch
from pathlib import Path
from typing import Any, Iterable

import yaml


@dataclass(frozen=True)
class RootSpec:
    path: str
    recursive: bool
    include_globs: list[str]
    exclude_globs: list[str]
    allow_missing: bool


@dataclass(frozen=True)
class ProjectInfo:
    owner: str
    repo: str


def load_yaml_file(path: Path) -> dict[str, Any]:
    with path.open("r", encoding="utf-8") as stream:
        data = yaml.safe_load(stream) or {}
    if not isinstance(data, dict):
        raise ValueError(f"YAML racine invalide : {path}")
    return data


def load_perimetre(perimetre_path: str | Path) -> dict[str, Any]:
    return load_yaml_file(Path(perimetre_path))


def get_project_info(perimetre_data: dict[str, Any]) -> ProjectInfo:
    project = perimetre_data.get("project", {})
    owner = str(project.get("owner", "")).strip()
    repo = str(project.get("repo", "")).strip()
    if not owner or not repo:
        raise ValueError("project.owner / project.repo manquants dans perimetre.yaml")
    return ProjectInfo(owner=owner, repo=repo)


def list_pack_names(perimetre_data: dict[str, Any]) -> list[str]:
    packs = perimetre_data.get("packs", {})
    if not isinstance(packs, dict):
        raise ValueError("Section packs invalide dans perimetre.yaml")
    return list(packs.keys())


def read_paths_txt(paths_txt: str | Path) -> list[str]:
    result: list[str] = []
    for raw_line in Path(paths_txt).read_text(encoding="utf-8").splitlines():
        line = raw_line.strip()
        if not line or line.startswith("#"):
            continue
        result.append(line)
    return result


def _normalize_path(value: str) -> str:
    return Path(value).as_posix()


def _iter_root_candidates(root_dir: Path, recursive: bool) -> Iterable[Path]:
    if recursive:
        yield from root_dir.rglob("*")
    else:
        yield from root_dir.iterdir()


def _matches_any_glob(candidate: str, patterns: list[str]) -> bool:
    if not patterns:
        return True
    return any(fnmatch(candidate, pattern) for pattern in patterns)


def _resolve_root(repo_root: Path, spec: RootSpec) -> list[str]:
    root_dir = repo_root / spec.path
    if not root_dir.exists():
        if spec.allow_missing:
            return []
        raise FileNotFoundError(f"Root introuvable : {spec.path}")

    if not root_dir.is_dir():
        raise NotADirectoryError(f"Root non directory : {spec.path}")

    resolved: list[str] = []
    for candidate in _iter_root_candidates(root_dir, spec.recursive):
        if not candidate.is_file():
            continue

        relative_to_root = candidate.relative_to(root_dir).as_posix()

        if not _matches_any_glob(relative_to_root, spec.include_globs):
            continue

        if _matches_any_glob(relative_to_root, spec.exclude_globs):
            continue

        resolved.append(candidate.relative_to(repo_root).as_posix())

    return resolved


def _parse_root_specs(pack_data: dict[str, Any]) -> list[RootSpec]:
    specs: list[RootSpec] = []
    for raw_root in pack_data.get("roots", []) or []:
        specs.append(
            RootSpec(
                path=str(raw_root.get("path", "")).strip(),
                recursive=bool(raw_root.get("recursive", False)),
                include_globs=list(raw_root.get("include_globs", []) or []),
                exclude_globs=list(raw_root.get("exclude_globs", []) or []),
                allow_missing=bool(raw_root.get("allow_missing", False)),
            )
        )
    return specs


def _stable_unique(values: Iterable[str]) -> list[str]:
    seen: set[str] = set()
    result: list[str] = []
    for value in values:
        normalized = _normalize_path(value)
        if normalized in seen:
            continue
        seen.add(normalized)
        result.append(normalized)
    return result


def resolve_pack_paths(
    repo_root: str | Path,
    perimetre_path: str | Path,
    pack_names: list[str],
) -> list[str]:
    repo_root_path = Path(repo_root).resolve()
    perimetre_data = load_perimetre(perimetre_path)
    packs = perimetre_data.get("packs", {})

    if not isinstance(packs, dict):
        raise ValueError("Section packs invalide dans perimetre.yaml")

    resolved: list[str] = []

    for pack_name in pack_names:
        if pack_name not in packs:
            raise KeyError(f"Pack inconnu : {pack_name}")

        pack_data = packs[pack_name]
        explicit_paths = list(pack_data.get("paths", []) or [])
        resolved.extend(explicit_paths)

        for root_spec in _parse_root_specs(pack_data):
            resolved.extend(_resolve_root(repo_root_path, root_spec))

    return _stable_unique(resolved)


def resolve_bootstrap_paths(
    repo_root: str | Path,
    paths_txt: str | Path,
) -> list[str]:
    _ = Path(repo_root).resolve()
    return _stable_unique(read_paths_txt(paths_txt))


def build_raw_sha_url(owner: str, repo: str, sha: str, path: str) -> str:
    normalized = _normalize_path(path)
    return f"https://raw.githubusercontent.com/{owner}/{repo}/{sha}/{normalized}"


def build_raw_sha_urls(
    perimetre_path: str | Path,
    sha: str,
    paths: list[str],
) -> list[str]:
    perimetre_data = load_perimetre(perimetre_path)
    project = get_project_info(perimetre_data)
    return [build_raw_sha_url(project.owner, project.repo, sha, path) for path in paths]