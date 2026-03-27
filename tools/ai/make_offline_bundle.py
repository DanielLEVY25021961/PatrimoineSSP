#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
tools/ai/make_offline_bundle.py

Construit un bundle OFFLINE à partir du dépôt au SHA demandé.

Règle forte :
- si --lot all, le bundle doit être IDENTIQUE au contenu tracké sur GitHub au SHA
  (donc tous les fichiers trackés du repo au SHA)
- si --lot != all, le bundle est construit à partir du périmètre hybride
  résolu par tools/ai/perimetre_resolver.py, complété par le bootstrap IA

Ce script s'appuie sur tools/ai/perimetre_resolver.py comme API canonique.
"""

from __future__ import annotations

import argparse
import datetime
import hashlib
import platform
import shutil
import subprocess
import sys
import tempfile
import zipfile
from pathlib import Path
from typing import Dict, List, Optional, Sequence, Tuple

import yaml

from perimetre_resolver import (
    BootstrapResolution,
    PackResolution,
    collect_bootstrap_paths,
    git_can_read_sha,
    list_pack_names,
    load_perimetre,
    read_bytes_from_repo,
    resolve_pack,
)


def _eprint(message: str) -> None:
    """
    Écrit un message sur stderr.
    """
    print(message, file=sys.stderr)


def _write_text(path: Path, content: str) -> None:
    """
    Écrit un fichier texte UTF-8.
    """
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content, encoding="utf-8")


def _sha256_file(path: Path) -> str:
    """
    Calcule le SHA-256 d'un fichier.
    """
    digest = hashlib.sha256()

    with path.open("rb") as handle:
        for chunk in iter(lambda: handle.read(1024 * 1024), b""):
            digest.update(chunk)

    return digest.hexdigest()


def _run_git(args: Sequence[str], cwd: Path) -> Tuple[int, bytes, bytes]:
    """
    Exécute git et retourne (returncode, stdout, stderr).
    """
    try:
        completed = subprocess.run(
            ["git", *args],
            cwd=str(cwd),
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            check=False,
        )
        return completed.returncode, completed.stdout, completed.stderr
    except FileNotFoundError:
        return 127, b"", b"git introuvable"


def _git_list_all_tracked_files_at_sha(repo_root: Path, sha: str) -> List[str]:
    """
    Retourne TOUS les fichiers trackés dans le commit SHA.

    Cette liste est la source de vérité pour construire un bundle `--lot all`
    strictement identique à GitHub au SHA.
    """
    code, out, err = _run_git(["ls-tree", "-r", "--name-only", sha], repo_root)
    if code != 0:
        raise RuntimeError(
            "Impossible de lister tous les fichiers trackés au SHA via git ls-tree : "
            + err.decode("utf-8", errors="replace").strip()
        )

    result: List[str] = []
    for line in out.decode("utf-8").splitlines():
        stripped = line.strip()
        if stripped:
            result.append(stripped.replace("\\", "/"))

    return sorted(set(result))


def _zip_dir(src_dir: Path, zip_path: Path) -> None:
    """
    Zippe un dossier complet.
    """
    zip_path.parent.mkdir(parents=True, exist_ok=True)
    with zipfile.ZipFile(str(zip_path), "w", compression=zipfile.ZIP_DEFLATED) as archive:
        for file_path in src_dir.rglob("*"):
            if file_path.is_file():
                archive.write(str(file_path), file_path.relative_to(src_dir).as_posix())


def _compute_ai_offline_dir(out_dir: Path, do_zip: bool) -> Path:
    """
    Détermine le dossier AI_OFFLINE à écrire.
    """
    if do_zip:
        tmp_root = Path(tempfile.mkdtemp(prefix="AI_OFFLINE_BUILD_"))
        return tmp_root / "AI_OFFLINE"

    if out_dir.name.lower() == "ai_offline":
        return out_dir
    return out_dir / "AI_OFFLINE"


def _current_utc_iso_z() -> str:
    """
    Retourne un horodatage UTC ISO-8601 suffixé par Z,
    sans utiliser datetime.utcnow().
    """
    now_utc = datetime.datetime.now(datetime.timezone.utc).replace(microsecond=0)
    return now_utc.isoformat().replace("+00:00", "Z")


def _build_provenance_content(
    *,
    sha: str,
    lot: str,
    repo_root: Path,
    perimetre_path: Path,
    git_ok: bool,
    bundle_mode: str,
    selected_packs: List[str],
    extracted: List[str],
    missing_files: List[str],
    pack_resolution: Optional[PackResolution],
    bootstrap_resolution: Optional[BootstrapResolution],
    tracked_repository_file_count: Optional[int],
) -> str:
    """
    Construit un PROVENANCE.yaml valide.
    """
    provenance: Dict[str, object] = {
        "sha": sha,
        "lot": lot,
        "generated_at_utc": _current_utc_iso_z(),
        "hostname": platform.node(),
        "system": platform.system(),
        "release": platform.release(),
        "python": platform.python_version(),
        "repo_root": repo_root.resolve().as_posix(),
        "perimetre_path": perimetre_path.as_posix(),
        "git_used": git_ok,
        "bundle_mode": bundle_mode,
        "selected_packs": selected_packs,
        "extracted_count": len(extracted),
        "missing_files_count": len(missing_files),
        "missing_files": missing_files,
    }

    if tracked_repository_file_count is not None:
        provenance["tracked_repository_file_count"] = tracked_repository_file_count

    if pack_resolution is not None:
        provenance["perimetre_mode"] = "hybrid"
        provenance["explicit_paths_count"] = len(pack_resolution.explicit_paths)
        provenance["root_files_count"] = len(pack_resolution.root_files)
        provenance["resolved_roots_count"] = len(pack_resolution.resolved_roots)
        provenance["missing_roots_count"] = len(pack_resolution.missing_roots)
        provenance["missing_roots"] = list(pack_resolution.missing_roots)
        provenance["missing_count"] = len(pack_resolution.missing_roots) + len(missing_files)
    else:
        provenance["perimetre_mode"] = "repository_full"
        provenance["missing_roots_count"] = 0
        provenance["missing_roots"] = []
        provenance["missing_count"] = len(missing_files)

    if bootstrap_resolution is not None:
        provenance["bootstrap_manifest_path"] = bootstrap_resolution.manifest_path
        provenance["bootstrap_paths_txt_path"] = bootstrap_resolution.paths_txt_path
        provenance["bootstrap_paths"] = list(bootstrap_resolution.files)
        provenance["bootstrap_missing_files"] = list(bootstrap_resolution.missing_files)
        provenance["bootstrap_paths_count"] = len(bootstrap_resolution.files)

    return yaml.safe_dump(
        provenance,
        allow_unicode=True,
        sort_keys=False,
        default_flow_style=False,
    )


def _resolve_bundle_paths(
    *,
    repo_root: Path,
    sha: str,
    lot: str,
    perimetre_path: Path,
) -> Tuple[
    List[str],
    str,
    List[str],
    Optional[PackResolution],
    Optional[BootstrapResolution],
    Optional[int],
]:
    """
    Résout la liste finale des fichiers à embarquer.

    Retourne :
    - all_paths
    - bundle_mode
    - selected_packs
    - pack_resolution
    - bootstrap_resolution
    - tracked_repository_file_count
    """
    perimetre = load_perimetre(perimetre_path)
    git_ok = git_can_read_sha(repo_root, sha)

    if lot == "all":
        if not git_ok:
            raise RuntimeError(
                "Impossible de construire un bundle identique à GitHub pour --lot all : "
                "le SHA n'est pas lisible via git."
            )

        tracked_files = _git_list_all_tracked_files_at_sha(repo_root, sha)
        selected_packs = list_pack_names(perimetre)

        return (
            tracked_files,
            "github_full_repository_at_sha",
            selected_packs,
            None,
            None,
            len(tracked_files),
        )

    pack_resolution = resolve_pack(
        perimetre_path=perimetre_path,
        repo_root=repo_root,
        pack_name=lot,
        sha=sha,
    )

    bootstrap_resolution = collect_bootstrap_paths(
        repo_root=repo_root,
        sha=sha,
    )

    all_paths = sorted(set(pack_resolution.files + bootstrap_resolution.files))

    return (
        all_paths,
        "hybrid_perimetre_plus_bootstrap",
        [lot],
        pack_resolution,
        bootstrap_resolution,
        None,
    )


def build_bundle(
    *,
    repo_root: Path,
    sha: str,
    lot: str,
    perimetre_path: Path,
    out_dir: Path,
    do_zip: bool,
) -> Path:
    """
    Construit AI_OFFLINE/ + CHECKSUMS + INDEX + PROVENANCE.
    Retourne le chemin du zip si do_zip, sinon le dossier AI_OFFLINE.
    """
    out_dir.mkdir(parents=True, exist_ok=True)

    ai_offline_dir = _compute_ai_offline_dir(out_dir=out_dir, do_zip=do_zip)
    files_dir = ai_offline_dir / "FILES"

    if ai_offline_dir.exists() and ai_offline_dir.is_dir():
        shutil.rmtree(ai_offline_dir)
    files_dir.mkdir(parents=True, exist_ok=True)

    git_ok = git_can_read_sha(repo_root, sha)

    (
        all_paths,
        bundle_mode,
        selected_packs,
        pack_resolution,
        bootstrap_resolution,
        tracked_repository_file_count,
    ) = _resolve_bundle_paths(
        repo_root=repo_root,
        sha=sha,
        lot=lot,
        perimetre_path=perimetre_path,
    )

    extracted: List[str] = []
    missing_files: List[str] = []

    for rel_path in all_paths:
        content_bytes: Optional[bytes] = read_bytes_from_repo(
            repo_root=repo_root,
            rel_path=rel_path,
            sha=sha,
            git_ok=git_ok,
        )

        if content_bytes is None:
            missing_files.append(rel_path)
            continue

        target = files_dir / rel_path
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_bytes(content_bytes)
        extracted.append(rel_path)

    extracted = sorted(set(extracted))
    missing_files = sorted(set(missing_files))

    _write_text(
        ai_offline_dir / "INDEX.txt",
        "\n".join(extracted) + ("\n" if extracted else ""),
    )

    checksums_lines = [
        f"{_sha256_file(files_dir / rel_path)}  FILES/{rel_path}"
        for rel_path in extracted
    ]
    _write_text(
        ai_offline_dir / "CHECKSUMS.sha256",
        "\n".join(checksums_lines) + ("\n" if checksums_lines else ""),
    )

    _write_text(
        ai_offline_dir / "PROVENANCE.yaml",
        _build_provenance_content(
            sha=sha,
            lot=lot,
            repo_root=repo_root,
            perimetre_path=perimetre_path,
            git_ok=git_ok,
            bundle_mode=bundle_mode,
            selected_packs=selected_packs,
            extracted=extracted,
            missing_files=missing_files,
            pack_resolution=pack_resolution,
            bootstrap_resolution=bootstrap_resolution,
            tracked_repository_file_count=tracked_repository_file_count,
        ),
    )

    missing_lines: List[str] = []

    if pack_resolution is not None and pack_resolution.missing_roots:
        missing_lines.append("# Roots obligatoires manquantes")
        missing_lines.extend(list(pack_resolution.missing_roots))
        missing_lines.append("")

    if missing_files:
        missing_lines.append("# Fichiers introuvables")
        missing_lines.extend(missing_files)

    if missing_lines:
        _write_text(
            ai_offline_dir / "MISSING.txt",
            "\n".join(missing_lines).rstrip() + "\n",
        )

    if do_zip:
        zip_name = f"AI_OFFLINE_{sha}_{lot}.zip"
        zip_path = out_dir / zip_name
        if zip_path.exists():
            zip_path.unlink()

        try:
            _zip_dir(ai_offline_dir, zip_path)
        finally:
            tmp_root = ai_offline_dir.parent
            if (
                tmp_root.exists()
                and tmp_root.is_dir()
                and tmp_root.name.startswith("AI_OFFLINE_BUILD_")
            ):
                shutil.rmtree(tmp_root, ignore_errors=True)

        return zip_path

    return ai_offline_dir


def main() -> int:
    """
    Point d'entrée CLI.
    """
    parser = argparse.ArgumentParser(
        prog="make_offline_bundle.py",
        formatter_class=argparse.RawTextHelpFormatter,
        description=(
            "Construit un bundle OFFLINE (AI_OFFLINE) au SHA donné.\n"
            "- --lot all : bundle identique aux fichiers trackés de GitHub au SHA\n"
            "- autre lot : bundle construit depuis le périmètre hybride + bootstrap IA"
        ),
    )
    parser.add_argument("--sha", required=True, help="SHA unique (commit).")
    parser.add_argument(
        "--lot",
        required=True,
        help="Pack à extraire depuis docs/ai/perimetre.yaml, ou 'all'.",
    )
    parser.add_argument(
        "--perimetre",
        default="docs/ai/perimetre.yaml",
        help="Chemin du perimetre.yaml (défaut : docs/ai/perimetre.yaml).",
    )
    parser.add_argument(
        "--out-dir",
        default=".",
        help=(
            "Répertoire de sortie.\n"
            "- Mode ZIP (défaut) : le zip est écrit dans ce répertoire.\n"
            "- Mode --no-zip : le dossier AI_OFFLINE est écrit dans ce répertoire.\n"
            "Astuce : passe --out-dir AI_OFFLINE pour obtenir AI_OFFLINE/AI_OFFLINE_<sha>_<lot>.zip "
            "sans créer AI_OFFLINE/AI_OFFLINE/."
        ),
    )
    parser.add_argument(
        "--no-zip",
        action="store_true",
        help="Ne pas zipper (garde le dossier AI_OFFLINE sur disque).",
    )

    args = parser.parse_args()

    repo_root = Path.cwd()
    perimetre_path = (repo_root / args.perimetre).resolve()

    if not perimetre_path.exists():
        _eprint(f"perimetre.yaml introuvable : {perimetre_path}")
        return 2

    out_dir = (repo_root / args.out_dir).resolve()
    if out_dir.exists() and not out_dir.is_dir():
        _eprint(f"--out-dir n'est pas un dossier : {out_dir}")
        return 2

    try:
        perimetre = load_perimetre(perimetre_path)
        valid_packs = list_pack_names(perimetre) + ["all"]

        if args.lot.strip() not in valid_packs:
            _eprint(
                f"Pack inconnu : {args.lot.strip()} "
                f"(packs valides : {', '.join(valid_packs)})"
            )
            return 2

        result_path = build_bundle(
            repo_root=repo_root,
            sha=args.sha.strip(),
            lot=args.lot.strip(),
            perimetre_path=perimetre_path,
            out_dir=out_dir,
            do_zip=(not args.no_zip),
        )

    except Exception as exc:
        _eprint("incident de lecture")
        _eprint(str(exc))
        return 3

    print(str(result_path))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())