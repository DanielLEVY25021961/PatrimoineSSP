#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
tools/ai/make_offline_bundle.py

Construit un bundle OFFLINE à partir du périmètre hybride défini dans
`docs/ai/perimetre.yaml`, en utilisant `tools/ai/perimetre_resolver.py`
comme API canonique de résolution.
"""

from __future__ import annotations

import argparse
import datetime
import hashlib
import platform
import re
import shutil
import subprocess
import sys
import tempfile
import zipfile
from pathlib import Path
from typing import Iterable, List, Optional, Sequence, Set, Tuple

import yaml

from perimetre_resolver import (
    list_pack_names,
    load_perimetre,
    resolve_pack,
)

RAW_REFS_HEADS_PREFIX = re.compile(
    r"^https://raw\.githubusercontent\.com/"
    r"(?P<owner>[^/]+)/(?P<repo>[^/]+)/refs/heads/(?P<branch>[^/]+)/(?P<path>.+)$"
)

RAW_SHA_PREFIX = re.compile(
    r"^https://raw\.githubusercontent\.com/"
    r"(?P<owner>[^/]+)/(?P<repo>[^/]+)/(?P<sha>[0-9a-fA-F]{7,40})/(?P<path>.+)$"
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


def _git_is_available(repo_root: Path) -> bool:
    """
    Indique si git est disponible.
    """
    code, _, _ = _run_git(["--version"], repo_root)
    return code == 0


def _git_has_object(repo_root: Path, sha: str) -> bool:
    """
    Indique si le SHA est résolvable localement.
    """
    code, _, _ = _run_git(["cat-file", "-e", f"{sha}^{{commit}}"], repo_root)
    return code == 0


def _git_show_file(repo_root: Path, sha: str, rel_path: str) -> Optional[bytes]:
    """
    Retourne le contenu binaire de sha:path, ou None si échec.
    """
    code, out, _ = _run_git(["show", f"{sha}:{rel_path}"], repo_root)
    if code != 0:
        return None
    return out


def _safe_relpath(path_str: str) -> str:
    """
    Normalise un path relatif au dépôt et interdit les paths absolus
    et les sorties du dépôt.
    """
    normalized = path_str.replace("\\", "/").strip()
    if not normalized:
        raise ValueError("Path vide interdit.")

    path_obj = Path(normalized)

    if path_obj.is_absolute():
        raise ValueError(f"Path absolu interdit : {path_str}")

    parts = [part for part in path_obj.parts if part not in ("", ".")]
    if any(part == ".." for part in parts):
        raise ValueError(f"Path parent '..' interdit : {path_str}")

    return str(Path(*parts)).replace("\\", "/")


def _extract_path_from_bootstrap_line(line: str) -> Optional[str]:
    """
    Extrait un path normalisé depuis une ligne de bootstrap.
    Supporte :
    - paths relatifs
    - URLs Raw refs/heads
    - URLs Raw SHA
    """
    stripped = line.strip()
    if not stripped or stripped.startswith("#"):
        return None

    match_ref = RAW_REFS_HEADS_PREFIX.match(stripped)
    if match_ref:
        return _safe_relpath(match_ref.group("path"))

    match_sha = RAW_SHA_PREFIX.match(stripped)
    if match_sha:
        return _safe_relpath(match_sha.group("path"))

    return _safe_relpath(stripped)


def _read_text_from_git_or_local(
    repo_root: Path,
    sha: str,
    rel_path: str,
    git_ok: bool,
) -> Optional[str]:
    """
    Lit un fichier texte depuis git au SHA si possible, sinon localement.
    """
    content_bytes: Optional[bytes] = None

    if git_ok:
        content_bytes = _git_show_file(repo_root, sha, rel_path)
    else:
        local_file = repo_root / rel_path
        if local_file.exists() and local_file.is_file():
            content_bytes = local_file.read_bytes()

    if content_bytes is None:
        return None

    return content_bytes.decode("utf-8")


def _collect_manifest_bootstrap_paths(
    repo_root: Path,
    sha: str,
    git_ok: bool,
) -> List[str]:
    """
    Collecte les paths bootstrap dérivés du MANIFEST.
    """
    manifest_rel_path = "docs/ai/MANIFEST_IA.yaml"
    manifest_text = _read_text_from_git_or_local(
        repo_root=repo_root,
        sha=sha,
        rel_path=manifest_rel_path,
        git_ok=git_ok,
    )

    if manifest_text is None:
        return [manifest_rel_path]

    manifest_data = yaml.safe_load(manifest_text)
    if not isinstance(manifest_data, dict):
        return [manifest_rel_path]

    result: Set[str] = {manifest_rel_path}

    for section_name in ("ai", "tools"):
        section = manifest_data.get(section_name)
        if not isinstance(section, dict):
            continue
        for value in section.values():
            if isinstance(value, str) and value.strip():
                result.add(_safe_relpath(value))

    return sorted(result)


def _collect_paths_txt_bootstrap_paths(
    repo_root: Path,
    sha: str,
    git_ok: bool,
) -> List[str]:
    """
    Collecte les paths bootstrap à partir de tools/ai/paths.txt.
    """
    paths_rel_path = "tools/ai/paths.txt"
    paths_text = _read_text_from_git_or_local(
        repo_root=repo_root,
        sha=sha,
        rel_path=paths_rel_path,
        git_ok=git_ok,
    )

    if paths_text is None:
        return [paths_rel_path]

    result: Set[str] = {paths_rel_path}

    for line in paths_text.splitlines():
        extracted = _extract_path_from_bootstrap_line(line)
        if extracted:
            result.add(extracted)

    return sorted(result)


def _collect_bootstrap_paths(
    repo_root: Path,
    sha: str,
    git_ok: bool,
) -> List[str]:
    """
    Construit le bootstrap minimal du bundle.
    """
    result: Set[str] = set()
    result.update(_collect_manifest_bootstrap_paths(repo_root, sha, git_ok))
    result.update(_collect_paths_txt_bootstrap_paths(repo_root, sha, git_ok))
    return sorted(result)


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


def _build_provenance_content(
    *,
    sha: str,
    lot: str,
    repo_root: Path,
    perimetre_path: Path,
    git_ok: bool,
    selected_packs: List[str],
    resolution,
    bootstrap_paths: List[str],
    extracted: List[str],
    missing_files: List[str],
) -> str:
    """
    Construit un PROVENANCE.yaml valide.
    """
    provenance = {
        "sha": sha,
        "lot": lot,
        "generated_at_utc": datetime.datetime.utcnow().replace(microsecond=0).isoformat() + "Z",
        "hostname": platform.node(),
        "system": platform.system(),
        "release": platform.release(),
        "python": platform.python_version(),
        "repo_root": repo_root.resolve().as_posix(),
        "perimetre_path": perimetre_path.as_posix(),
        "git_used": git_ok,
        "perimetre_mode": "hybrid",
        "selected_packs": selected_packs,
        "bootstrap_paths": bootstrap_paths,
        "explicit_paths_count": len(resolution.explicit_paths),
        "root_files_count": len(resolution.root_files),
        "resolved_roots_count": len(resolution.resolved_roots),
        "bootstrap_paths_count": len(bootstrap_paths),
        "extracted_count": len(extracted),
        "missing_roots_count": len(resolution.missing_roots),
        "missing_files_count": len(missing_files),
        "missing_count": len(resolution.missing_roots) + len(missing_files),
        "missing_roots": list(resolution.missing_roots),
        "missing_files": missing_files,
    }

    return yaml.safe_dump(
        provenance,
        allow_unicode=True,
        sort_keys=False,
        default_flow_style=False,
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
    perimetre = load_perimetre(perimetre_path)
    selected_packs = list_pack_names(perimetre) if lot == "all" else [lot]

    out_dir.mkdir(parents=True, exist_ok=True)

    ai_offline_dir = _compute_ai_offline_dir(out_dir=out_dir, do_zip=do_zip)
    files_dir = ai_offline_dir / "FILES"

    if ai_offline_dir.exists() and ai_offline_dir.is_dir():
        shutil.rmtree(ai_offline_dir)
    files_dir.mkdir(parents=True, exist_ok=True)

    git_ok = _git_is_available(repo_root) and _git_has_object(repo_root, sha)

    resolution = resolve_pack(
        perimetre_path=perimetre_path,
        repo_root=repo_root,
        pack_name=lot,
        sha=sha,
    )

    bootstrap_paths = _collect_bootstrap_paths(
        repo_root=repo_root,
        sha=sha,
        git_ok=git_ok,
    )

    all_paths = sorted(set(resolution.files + tuple(bootstrap_paths)))

    extracted: List[str] = []
    missing_files: List[str] = []

    for rel_path in all_paths:
        content_bytes: Optional[bytes] = None

        if git_ok:
            content_bytes = _git_show_file(repo_root, sha, rel_path)
        else:
            local_file = repo_root / rel_path
            if local_file.exists() and local_file.is_file():
                content_bytes = local_file.read_bytes()

        if content_bytes is None:
            missing_files.append(rel_path)
            continue

        target = files_dir / rel_path
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_bytes(content_bytes)
        extracted.append(rel_path)

    extracted = sorted(set(extracted))
    missing_files = sorted(set(missing_files))

    _write_text(ai_offline_dir / "INDEX.txt", "\n".join(extracted) + ("\n" if extracted else ""))

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
            selected_packs=selected_packs,
            resolution=resolution,
            bootstrap_paths=bootstrap_paths,
            extracted=extracted,
            missing_files=missing_files,
        ),
    )

    if resolution.missing_roots or missing_files:
        missing_lines: List[str] = []

        if resolution.missing_roots:
            missing_lines.append("# Roots obligatoires manquantes")
            missing_lines.extend(list(resolution.missing_roots))
            missing_lines.append("")

        if missing_files:
            missing_lines.append("# Fichiers introuvables")
            missing_lines.extend(missing_files)

        _write_text(ai_offline_dir / "MISSING.txt", "\n".join(missing_lines).rstrip() + "\n")

    if do_zip:
        zip_name = f"AI_OFFLINE_{sha}_{lot}.zip"
        zip_path = out_dir / zip_name
        if zip_path.exists():
            zip_path.unlink()

        try:
            _zip_dir(ai_offline_dir, zip_path)
        finally:
            tmp_root = ai_offline_dir.parent
            if tmp_root.exists() and tmp_root.is_dir() and tmp_root.name.startswith("AI_OFFLINE_BUILD_"):
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
            "Construit un bundle OFFLINE (AI_OFFLINE) au SHA donné, "
            "en incluant le périmètre hybride et le bootstrap IA minimal."
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