#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
tools/ai/make_offline_bundle.py

But :
- Construire un bundle OFFLINE reproductible au SHA donné, sans dépendre de GitHub
  dans le chat. Le bundle contient les fichiers du périmètre, + INDEX + CHECKSUMS.

Entrées :
- --sha : SHA unique (obligatoire)
- --lot : pack (ai|docs_contrats|metier|persistance|gateway|all)
- --perimetre : chemin du perimetre.yaml (optionnel)

Sorties (par défaut) :
- ./AI_OFFLINE/ ... (dossier)
- ./AI_OFFLINE_<sha>_<lot>.zip (zip)

Stratégie d’extraction :
- Priorité 1 : git show <sha>:<path> (si git dispo et sha résolvable)
- Priorité 2 : copie depuis working tree (si fichier existe localement)

Contrats :
- Génère AI_OFFLINE/INDEX.txt (liste triée des paths)
- Génère AI_OFFLINE/PROVENANCE.yaml (sha, lot, date, hostname, etc.)
- Génère AI_OFFLINE/CHECKSUMS.sha256 (sha256 pour chaque fichier)
"""

from __future__ import annotations

import argparse
import datetime
import hashlib
import os
import platform
import shutil
import subprocess
import sys
import tempfile
import textwrap
import zipfile
from pathlib import Path
from typing import Dict, List, Optional, Tuple


def _eprint(msg: str) -> None:
    print(msg, file=sys.stderr)


def _read_text(path: Path) -> str:
    return path.read_text(encoding="utf-8")


def _write_text(path: Path, content: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content, encoding="utf-8")


def _sha256_bytes(data: bytes) -> str:
    h = hashlib.sha256()
    h.update(data)
    return h.hexdigest()


def _sha256_file(path: Path) -> str:
    h = hashlib.sha256()
    with path.open("rb") as f:
        for chunk in iter(lambda: f.read(1024 * 1024), b""):
            h.update(chunk)
    return h.hexdigest()


def _run_git(args: List[str], cwd: Path) -> Tuple[int, bytes, bytes]:
    """
    Exécute git. Retourne (returncode, stdout, stderr).
    """
    try:
        p = subprocess.run(
            ["git"] + args,
            cwd=str(cwd),
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            check=False,
        )
        return p.returncode, p.stdout, p.stderr
    except FileNotFoundError:
        return 127, b"", b"git introuvable"


def _git_is_available(repo_root: Path) -> bool:
    code, _, _ = _run_git(["--version"], repo_root)
    return code == 0


def _git_has_object(repo_root: Path, sha: str) -> bool:
    code, _, _ = _run_git(["cat-file", "-e", f"{sha}^{{commit}}"], repo_root)
    return code == 0


def _git_show_file(repo_root: Path, sha: str, rel_path: str) -> Optional[bytes]:
    """
    Retourne le contenu binaire via git show sha:path, ou None si échec.
    """
    code, out, _ = _run_git(["show", f"{sha}:{rel_path}"], repo_root)
    if code != 0:
        return None
    return out


def _safe_relpath(path_str: str) -> str:
    """
    Normalise un path de repo.
    Refuse tout path qui tenterait de sortir du repo.
    """
    p = Path(path_str.replace("\\", "/"))
    if p.is_absolute():
        raise ValueError(f"Path absolu interdit : {path_str}")
    parts = [x for x in p.parts if x not in ("", ".")]
    if any(x == ".." for x in parts):
        raise ValueError(f"Path parent '..' interdit : {path_str}")
    return str(Path(*parts)).replace("\\", "/")


def _load_perimetre_yaml(perimetre_path: Path) -> Dict:
    """
    Charge perimetre.yaml via PyYAML si présent.
    """
    try:
        import yaml  # type: ignore
    except Exception as e:
        raise RuntimeError(
            "PyYAML manquant. Installe-le avec : pip install pyyaml\n"
            f"Détail : {e}"
        ) from e

    raw = _read_text(perimetre_path)
    data = yaml.safe_load(raw)
    if not isinstance(data, dict):
        raise ValueError("perimetre.yaml invalide (racine non-dict).")
    return data


def _get_pack_paths(perimetre: Dict, lot: str) -> List[str]:
    packs = perimetre.get("packs")
    if not isinstance(packs, dict):
        raise ValueError("perimetre.yaml : packs manquant ou invalide.")

    if lot == "all":
        selected = []
        for _, pack in packs.items():
            if isinstance(pack, dict):
                paths = pack.get("paths")
                if isinstance(paths, list):
                    selected.extend([str(p) for p in paths])
        return selected

    pack = packs.get(lot)
    if not isinstance(pack, dict):
        raise ValueError(f"Pack '{lot}' introuvable dans perimetre.yaml.")
    paths = pack.get("paths")
    if not isinstance(paths, list):
        raise ValueError(f"Pack '{lot}' : paths manquant ou invalide.")
    return [str(p) for p in paths]


def _zip_dir(src_dir: Path, zip_path: Path) -> None:
    zip_path.parent.mkdir(parents=True, exist_ok=True)
    with zipfile.ZipFile(str(zip_path), "w", compression=zipfile.ZIP_DEFLATED) as z:
        for file_path in src_dir.rglob("*"):
            if file_path.is_file():
                arcname = file_path.relative_to(src_dir).as_posix()
                z.write(str(file_path), arcname)


def build_bundle(
    repo_root: Path,
    sha: str,
    lot: str,
    perimetre_path: Path,
    out_dir: Path,
    do_zip: bool,
) -> Path:
    """
    Construit AI_OFFLINE/ + CHECKSUMS + INDEX + PROVENANCE.
    Retourne le chemin du zip si do_zip, sinon le dossier out_dir.
    """
    perimetre = _load_perimetre_yaml(perimetre_path)
    raw_paths = _get_pack_paths(perimetre, lot)

    normalized_paths: List[str] = []
    for p in raw_paths:
        normalized_paths.append(_safe_relpath(p))

    normalized_paths = sorted(set(normalized_paths))

    out_dir.mkdir(parents=True, exist_ok=True)

    bundle_root = out_dir
    ai_offline_dir = bundle_root / "AI_OFFLINE"
    files_dir = ai_offline_dir / "FILES"

    if ai_offline_dir.exists():
        shutil.rmtree(ai_offline_dir)
    files_dir.mkdir(parents=True, exist_ok=True)

    git_ok = _git_is_available(repo_root) and _git_has_object(repo_root, sha)

    missing: List[str] = []
    extracted: List[str] = []

    for rel_path in normalized_paths:
        data: Optional[bytes] = None

        if git_ok:
            data = _git_show_file(repo_root, sha, rel_path)

        if data is None:
            local_file = repo_root / rel_path
            if local_file.exists() and local_file.is_file():
                data = local_file.read_bytes()

        if data is None:
            missing.append(rel_path)
            continue

        target = files_dir / rel_path
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_bytes(data)
        extracted.append(rel_path)

    index_lines = extracted[:]
    index_content = "\n".join(index_lines) + ("\n" if index_lines else "")
    _write_text(ai_offline_dir / "INDEX.txt", index_content)

    provenance = textwrap.dedent(
        f"""\
        sha: "{sha}"
        lot: "{lot}"
        generated_at_utc: "{datetime.datetime.utcnow().replace(microsecond=0).isoformat()}Z"
        hostname: "{platform.node()}"
        system: "{platform.system()}"
        release: "{platform.release()}"
        python: "{platform.python_version()}"
        repo_root: "{repo_root.resolve().as_posix()}"
        perimetre_path: "{perimetre_path.as_posix()}"
        git_used: {str(git_ok).lower()}
        missing_count: {len(missing)}
        """
    )
    _write_text(ai_offline_dir / "PROVENANCE.yaml", provenance)

    checksums_lines: List[str] = []
    for rel_path in extracted:
        fpath = files_dir / rel_path
        checksum = _sha256_file(fpath)
        checksums_lines.append(f"{checksum}  FILES/{rel_path}")

    checksums_content = "\n".join(checksums_lines) + ("\n" if checksums_lines else "")
    _write_text(ai_offline_dir / "CHECKSUMS.sha256", checksums_content)

    if missing:
        missing_content = "\n".join(missing) + "\n"
        _write_text(ai_offline_dir / "MISSING.txt", missing_content)

    if do_zip:
        zip_name = f"AI_OFFLINE_{sha}_{lot}.zip"
        zip_path = bundle_root / zip_name
        if zip_path.exists():
            zip_path.unlink()
        _zip_dir(ai_offline_dir, zip_path)
        return zip_path

    return ai_offline_dir


def main() -> int:
    parser = argparse.ArgumentParser(
        prog="make_offline_bundle.py",
        formatter_class=argparse.RawTextHelpFormatter,
        description="Construit un bundle OFFLINE (AI_OFFLINE) au SHA donné.",
    )
    parser.add_argument("--sha", required=True, help="SHA unique (commit).")
    parser.add_argument(
        "--lot",
        required=True,
        choices=["ai", "docs_contrats", "metier", "persistance", "gateway", "all"],
        help="Pack à extraire depuis docs/ai/perimetre.yaml.",
    )
    parser.add_argument(
        "--perimetre",
        default="docs/ai/perimetre.yaml",
        help="Chemin du perimetre.yaml (par défaut: docs/ai/perimetre.yaml).",
    )
    parser.add_argument(
        "--out-dir",
        default=".",
        help="Répertoire de sortie (par défaut: .).",
    )
    parser.add_argument(
        "--no-zip",
        action="store_true",
        help="Ne pas zipper (garde seulement AI_OFFLINE/).",
    )

    args = parser.parse_args()

    repo_root = Path.cwd()
    perimetre_path = (repo_root / args.perimetre).resolve()
    if not perimetre_path.exists():
        _eprint(f"perimetre.yaml introuvable : {perimetre_path}")
        return 2

    out_dir = (repo_root / args.out_dir).resolve()

    try:
        result_path = build_bundle(
            repo_root=repo_root,
            sha=args.sha.strip(),
            lot=args.lot.strip(),
            perimetre_path=perimetre_path,
            out_dir=out_dir,
            do_zip=(not args.no_zip),
        )
    except Exception as e:
        _eprint("incident de lecture")
        _eprint(str(e))
        return 3

    print(str(result_path))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
