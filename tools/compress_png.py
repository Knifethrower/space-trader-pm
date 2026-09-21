"""Shrinks PNG files with the tools in tools/pngtools/ (pngquant.exe and oxipng.exe).

Usage:
  python tools/compress_png.py lossless FILE...   oxipng only: pixels stay identical (use for screenshots)
  python tools/compress_png.py lossy FILE...      pngquant (quality 80-95) then oxipng: much smaller, colours shift slightly
                                                  (used for cover.png, which is photo-like artwork)

The tool binaries are not committed to the repository. Download them into tools/pngtools/:
  oxipng   https://github.com/oxipng/oxipng/releases      (oxipng-<version>-x86_64-pc-windows-msvc.zip)
  pngquant https://pngquant.org                            (pngquant-windows.zip)
On Linux or macOS, install the same tools from your package manager; the script also finds them on PATH.
"""
import os
import shutil
import subprocess
import sys
import tempfile

HERE = os.path.dirname(os.path.abspath(__file__))


def find(name):
    for cand in (os.path.join(HERE, "pngtools", name + ".exe"), os.path.join(HERE, "pngtools", name)):
        if os.path.isfile(cand):
            return cand
    return shutil.which(name)


def main():
    if len(sys.argv) < 3 or sys.argv[1] not in ("lossless", "lossy"):
        sys.exit(__doc__)
    mode, files = sys.argv[1], sys.argv[2:]
    oxipng = find("oxipng")
    pngquant = find("pngquant") if mode == "lossy" else None
    if not oxipng or (mode == "lossy" and not pngquant):
        sys.exit("missing tool: put oxipng and pngquant in tools/pngtools/ (see the top of this file)")
    for f in files:
        before = os.path.getsize(f)
        work = tempfile.mkdtemp()
        try:
            src = os.path.join(work, "in.png")
            shutil.copyfile(f, src)
            if mode == "lossy":
                out = os.path.join(work, "q.png")
                subprocess.run([pngquant, "--quality=80-95", "--speed", "1", "--force", "--output", out, src], check=True)
                src = out
            subprocess.run([oxipng, "-o", "4", "--zopfli", "--strip", "all", "-q", src], check=True)
            shutil.copyfile(src, f)
        finally:
            shutil.rmtree(work, ignore_errors=True)
        print("%s: %d KB -> %d KB (%s)" % (f, before // 1024, os.path.getsize(f) // 1024, mode))


main()
