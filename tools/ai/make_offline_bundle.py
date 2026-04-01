#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Construit un bundle OFFLINE à partir :
- d'un ou plusieurs packs du périmètre ;
- ou du dépôt tracké complet via --lot all.

Rétrocompatibilité CLI :
- --out-dir => alias de --output-dir
- --lot <nom> => alias historique d'un pack unique
- --lot all => bundle de tout le dépôt tracké, hors répertoire de sortie généré
"""

from __future__ import annotations

import argparse
import hashlib
import shutil
import subprocess
import sys
from pathlib import Path, PurePosixPath
from zipfile import ZIP_DEFLATED, ZipFile

import yaml

from perimetre_resolver import get_project_info, load_perimetre, resolve_pack_paths


def sha256_file(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Construit un bundle OFFLINE IA.",
    )

    parser.add_argument(
        "--sha",
        required=True,
        help="SHA Git de référence",
    )

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
        default=None,
        help="Nom(s) de pack à inclure",
    )

    parser.add_argument(
        "--lot",
        default="",
        help=(
            "Alias historique d'un pack unique. "
            "Utiliser 'all' pour empaqueter tout le dépôt tracké."
        ),
    )

    parser.add_argument(
        "--output-dir",
        "--out-dir",
        dest="output_dir",
        default="AI_OFFLINE",
        help="Répertoire de sortie du bundle",
    )

    parser.add_argument(
        "--zip-output",
        default="",
        help="Zip de sortie optionnel",
    )

    return parser.parse_args()


def _normalize_relative_path(value: str) -> str:
    """
    Normalise un chemin relatif sans casser les dotfiles.

    Important :
    - ne jamais utiliser lstrip("./"), car cela transformerait
      '.gitattributes' en 'gitattributes' ;
    - retirer uniquement un éventuel préfixe littéral './'.
    """
    normalized = PurePosixPath(value).as_posix()

    while normalized.startswith("./"):
        normalized = normalized[2:]

    return normalized


def _stable_unique(values: list[str]) -> list[str]:
    seen: set[str] = set()
    result: list[str] = []

    for value in values:
        normalized = _normalize_relative_path(value)
        if normalized in seen:
            continue
        seen.add(normalized)
        result.append(normalized)

    return result


def _path_inside_repo(repo_root: Path, candidate: Path) -> str | None:
    try:
        relative = candidate.resolve().relative_to(repo_root.resolve())
        return relative.as_posix()
    except ValueError:
        return None


def _filter_generated_targets(
    paths: list[str],
    repo_root: Path,
    output_dir: Path,
    zip_output: str,
) -> list[str]:
    output_rel = _path_inside_repo(repo_root, output_dir)
    zip_rel = None

    if zip_output:
        zip_rel = _path_inside_repo(repo_root, Path(zip_output).resolve())

    filtered: list[str] = []

    for raw_path in paths:
        normalized = _normalize_relative_path(raw_path)

        if output_rel and (
            normalized == output_rel
            or normalized.startswith(output_rel + "/")
        ):
            continue

        if zip_rel and normalized == zip_rel:
            continue

        filtered.append(normalized)

    return filtered


def list_git_tracked_files(repo_root: Path) -> list[str]:
    command = ["git", "-C", str(repo_root), "ls-files", "-z"]

    try:
        completed = subprocess.run(
            command,
            check=True,
            capture_output=True,
        )
    except FileNotFoundError as exc:
        raise RuntimeError("git introuvable dans le PATH.") from exc
    except subprocess.CalledProcessError as exc:
        stderr = exc.stderr.decode("utf-8", errors="replace").strip()
        raise RuntimeError(
            f"Échec de 'git ls-files' depuis {repo_root}: {stderr}",
        ) from exc

    stdout = completed.stdout.decode("utf-8", errors="replace")
    paths = [item for item in stdout.split("\0") if item]

    return _stable_unique(paths)


def resolve_selected_paths(
    repo_root: Path,
    perimetre_path: Path,
    output_dir: Path,
    lot: str,
    packs: list[str] | None,
    zip_output: str,
) -> tuple[list[str], str, dict[str, object]]:
    if packs and lot.strip():
        raise ValueError("Utiliser soit --packs, soit --lot, mais pas les deux.")

    if packs:
        resolved = resolve_pack_paths(
            repo_root=repo_root,
            perimetre_path=perimetre_path,
            pack_names=list(packs),
        )
        return (
            resolved,
            "resolved_packs_at_sha",
            {"selected_packs": list(packs)},
        )

    normalized_lot = lot.strip()

    if normalized_lot:
        if normalized_lot.lower() == "all":
            tracked_files = list_git_tracked_files(repo_root)
            filtered_files = _filter_generated_targets(
                paths=tracked_files,
                repo_root=repo_root,
                output_dir=output_dir,
                zip_output=zip_output,
            )
            return (
                filtered_files,
                "github_full_repository_at_sha",
                {"selected_lot": "all"},
            )

        resolved = resolve_pack_paths(
            repo_root=repo_root,
            perimetre_path=perimetre_path,
            pack_names=[normalized_lot],
        )
        return (
            resolved,
            "resolved_packs_at_sha",
            {"selected_lot": normalized_lot},
        )

    resolved = resolve_pack_paths(
        repo_root=repo_root,
        perimetre_path=perimetre_path,
        pack_names=["couche_ia"],
    )
    return (
        resolved,
        "resolved_packs_at_sha",
        {"selected_packs": ["couche_ia"]},
    )


def write_provenance(
    output_dir: Path,
    repo_owner: str,
    repo_name: str,
    sha: str,
    resolved_paths: list[str],
    bundle_mode: str,
    selection_metadata: dict[str, object],
) -> None:
    provenance: dict[str, object] = {
        "repo_owner": repo_owner,
        "repo_name": repo_name,
        "sha": sha,
        "bundle_mode": bundle_mode,
        "tracked_repository_file_count": len(resolved_paths),
        "extracted_count": len(resolved_paths),
        "missing_count": 0,
    }
    provenance.update(selection_metadata)

    content = yaml.safe_dump(
        provenance,
        sort_keys=False,
        allow_unicode=True,
    )
    (output_dir / "PROVENANCE.yaml").write_text(content, encoding="utf-8")


def write_index(output_dir: Path, resolved_paths: list[str]) -> None:
    content = "\n".join(resolved_paths) + ("\n" if resolved_paths else "")
    (output_dir / "INDEX.txt").write_text(content, encoding="utf-8")


def write_checksums(
    output_dir: Path,
    files_root: Path,
    resolved_paths: list[str],
) -> None:
    lines: list[str] = []

    for relative_path in resolved_paths:
        checksum = sha256_file(files_root / relative_path)
        lines.append(f"{checksum}  FILES/{relative_path}")

    content = "\n".join(lines) + ("\n" if lines else "")
    (output_dir / "CHECKSUMS.sha256").write_text(content, encoding="utf-8")


def copy_files(
    repo_root: Path,
    files_root: Path,
    resolved_paths: list[str],
) -> None:
    for relative_path in resolved_paths:
        source = repo_root / relative_path
        target = files_root / relative_path

        if not source.exists():
            raise FileNotFoundError(
                f"Fichier source introuvable lors du bundling : {relative_path}",
            )

        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(source, target)


def zip_bundle(output_dir: Path, zip_output: Path) -> None:
    zip_output.parent.mkdir(parents=True, exist_ok=True)

    with ZipFile(zip_output, "w", compression=ZIP_DEFLATED) as archive:
        for file_path in sorted(output_dir.rglob("*")):
            if file_path.is_file():
                archive.write(
                    file_path,
                    file_path.relative_to(output_dir.parent),
                )


def main() -> int:
    args = parse_args()

    repo_root = Path(args.repo_root).resolve()
    perimetre_path = Path(args.perimetre)
    output_dir = Path(args.output_dir).resolve()
    files_root = output_dir / "FILES"

    perimetre_data = load_perimetre(perimetre_path)
    project = get_project_info(perimetre_data)

    resolved_paths, bundle_mode, selection_metadata = resolve_selected_paths(
        repo_root=repo_root,
        perimetre_path=perimetre_path,
        output_dir=output_dir,
        lot=args.lot,
        packs=args.packs,
        zip_output=args.zip_output,
    )

    resolved_paths = _stable_unique(resolved_paths)

    if not resolved_paths:
        raise ValueError("Aucun fichier à empaqueter après résolution du périmètre.")

    if output_dir.exists():
        shutil.rmtree(output_dir)

    output_dir.mkdir(parents=True, exist_ok=True)
    files_root.mkdir(parents=True, exist_ok=True)

    copy_files(repo_root, files_root, resolved_paths)
    write_index(output_dir, resolved_paths)
    write_checksums(output_dir, files_root, resolved_paths)
    write_provenance(
        output_dir=output_dir,
        repo_owner=project.owner,
        repo_name=project.repo,
        sha=args.sha,
        resolved_paths=resolved_paths,
        bundle_mode=bundle_mode,
        selection_metadata=selection_metadata,
    )

    if args.zip_output:
        zip_bundle(output_dir, Path(args.zip_output).resolve())

    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except Exception as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        raise SystemExit(1)