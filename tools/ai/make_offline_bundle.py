#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Construit un bundle OFFLINE à partir d'un ou plusieurs packs du périmètre IA.

Le script :
- résout le périmètre ;
- copie les fichiers dans AI_OFFLINE/FILES ;
- génère INDEX.txt ;
- génère CHECKSUMS.sha256 ;
- génère PROVENANCE.yaml ;
- peut produire en option un zip du bundle.
"""

from __future__ import annotations

import argparse
import hashlib
import shutil
from pathlib import Path
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
    parser = argparse.ArgumentParser(description="Construit un bundle OFFLINE IA.")
    parser.add_argument("--sha", required=True, help="SHA Git de référence")
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
        help="Nom(s) de pack à inclure",
    )
    parser.add_argument(
        "--output-dir",
        default="AI_OFFLINE",
        help="Répertoire de sortie du bundle",
    )
    parser.add_argument(
        "--zip-output",
        default="",
        help="Zip de sortie optionnel",
    )
    return parser.parse_args()


def write_provenance(
    output_dir: Path,
    repo_owner: str,
    repo_name: str,
    sha: str,
    resolved_paths: list[str],
) -> None:
    provenance = {
        "repo_owner": repo_owner,
        "repo_name": repo_name,
        "sha": sha,
        "bundle_mode": "resolved_packs_at_sha",
        "tracked_repository_file_count": len(resolved_paths),
        "extracted_count": len(resolved_paths),
        "missing_count": 0,
    }
    content = yaml.safe_dump(
        provenance,
        sort_keys=False,
        allow_unicode=True,
    )
    (output_dir / "PROVENANCE.yaml").write_text(content, encoding="utf-8")


def write_index(output_dir: Path, resolved_paths: list[str]) -> None:
    content = "\n".join(resolved_paths) + ("\n" if resolved_paths else "")
    (output_dir / "INDEX.txt").write_text(content, encoding="utf-8")


def write_checksums(output_dir: Path, files_root: Path, resolved_paths: list[str]) -> None:
    lines: list[str] = []
    for relative_path in resolved_paths:
        checksum = sha256_file(files_root / relative_path)
        lines.append(f"{checksum}  FILES/{relative_path}")
    content = "\n".join(lines) + ("\n" if lines else "")
    (output_dir / "CHECKSUMS.sha256").write_text(content, encoding="utf-8")


def copy_files(repo_root: Path, files_root: Path, resolved_paths: list[str]) -> None:
    for relative_path in resolved_paths:
        source = repo_root / relative_path
        target = files_root / relative_path
        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(source, target)


def zip_bundle(output_dir: Path, zip_output: Path) -> None:
    with ZipFile(zip_output, "w", compression=ZIP_DEFLATED) as archive:
        for file_path in sorted(output_dir.rglob("*")):
            if file_path.is_file():
                archive.write(file_path, file_path.relative_to(output_dir.parent))


def main() -> int:
    args = parse_args()

    repo_root = Path(args.repo_root).resolve()
    output_dir = Path(args.output_dir).resolve()
    files_root = output_dir / "FILES"

    if output_dir.exists():
        shutil.rmtree(output_dir)

    output_dir.mkdir(parents=True, exist_ok=True)
    files_root.mkdir(parents=True, exist_ok=True)

    perimetre_data = load_perimetre(Path(args.perimetre))
    project = get_project_info(perimetre_data)

    resolved_paths = resolve_pack_paths(
        repo_root=repo_root,
        perimetre_path=Path(args.perimetre),
        pack_names=list(args.packs),
    )

    copy_files(repo_root, files_root, resolved_paths)
    write_index(output_dir, resolved_paths)
    write_checksums(output_dir, files_root, resolved_paths)
    write_provenance(
        output_dir=output_dir,
        repo_owner=project.owner,
        repo_name=project.repo,
        sha=args.sha,
        resolved_paths=resolved_paths,
    )

    if args.zip_output:
        zip_bundle(output_dir, Path(args.zip_output).resolve())

    return 0


if __name__ == "__main__":
    raise SystemExit(main())