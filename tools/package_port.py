"""Assembles the PortMaster port and checks it against the packaging rules.

Inputs : portmaster/spacetrader/  (the PR folder: port.json, README.md, gameinfo.xml, testing_thread.txt,
                                    screenshot.png, "Space Trader.sh", spacetrader/ with the ini and licenses)
         build/spacetrader-arm.jar (built by tools/build_jar.py --arm)
Outputs: build/pr/spacetrader/     the folder to submit
         build/spacetrader.zip     the installable port (what port.json's "name" refers to)
"""
import json
import os
import shutil
import struct
import subprocess
import sys
import zipfile

SRC = "portmaster/spacetrader"
JAR = "build/spacetrader-arm.jar"
PR = "build/pr/spacetrader"
ZIP = "build/spacetrader.zip"
GAME_DIR = "spacetrader"
SH = "Space Trader.sh"
TEXT_EXT = (".sh", ".ini", ".json", ".xml", ".md", ".txt")
EM_DASH = "—"
errors = []


def fail(msg):
    errors.append(msg)


# ---- 1. build the jar --------------------------------------------------------------------------------
subprocess.check_call([sys.executable, "tools/build_jar.py", "--arm"])

# ---- 2. assemble the PR folder (LF line endings for every text file) ----------------------------------
shutil.rmtree(PR, ignore_errors=True)
for dp, dn, fn in os.walk(SRC):
    rel = os.path.relpath(dp, SRC)
    os.makedirs(os.path.join(PR, rel), exist_ok=True)
    for f in fn:
        data = open(os.path.join(dp, f), "rb").read()
        if f.endswith(TEXT_EXT):
            data = data.replace(b"\r\n", b"\n")
        open(os.path.join(PR, rel, f), "wb").write(data)
shutil.copy(JAR, os.path.join(PR, GAME_DIR, "spacetrader.jar"))


def read(name):
    return open(os.path.join(PR, name), encoding="utf-8").read()


# ---- 3. rule checks ----------------------------------------------------------------------------------
for name in ("port.json", "README.md", "gameinfo.xml", "testing_thread.txt", SH, GAME_DIR + "/spacetrader.ini"):
    if not os.path.isfile(os.path.join(PR, name)):
        fail("missing file: " + name)
for img in ("screenshot.png", "cover.png"):
    if not os.path.isfile(os.path.join(PR, img)):
        fail("missing " + img)

if not errors:
    # em dashes are banned in all generated port content
    for name in ("port.json", "README.md", "gameinfo.xml", "testing_thread.txt", SH, GAME_DIR + "/spacetrader.ini"):
        if EM_DASH in read(name):
            fail("em dash in " + name)

    # port.json schema (v4)
    pj = json.loads(read("port.json"))
    attr = pj.get("attr", {})
    if pj.get("version") != 4:
        fail("port.json version must be 4")
    if pj.get("name") != os.path.basename(ZIP):
        fail("port.json name must be " + os.path.basename(ZIP))
    for item in pj.get("items", []):
        if not os.path.exists(os.path.join(PR, item)):
            fail("port.json item not found: " + item)
    for key in ("title", "porter", "desc", "desc_md", "inst", "inst_md", "genres", "image", "rtr", "exp",
                "runtime", "store", "availability", "reqs", "arch", "min_glibc"):
        if key not in attr:
            fail("port.json attr missing: " + key)
    if not attr.get("porter") or any(not p or p.lower() in ("yourhandle", "todo", "tbd") for p in attr["porter"]):
        fail("port.json porter is empty or a placeholder")
    if not isinstance(attr.get("runtime"), list) or not isinstance(attr.get("store"), list):
        fail("port.json runtime and store must be lists")
    for r in attr.get("runtime", []):
        if r.endswith(".squashfs") or ".aarch64" in r:
            fail("port.json runtime must be the bare catalog key, got " + r)
    if attr.get("exp") is not False:
        fail("port.json exp must be false")
    for key in ("desc", "inst"):
        if "\\n" in attr.get(key, "") or "\n" in attr.get(key, ""):
            fail("port.json " + key + " must be plain single-paragraph text")

    # launch script
    sh = read(SH)
    if not sh.startswith("#!/bin/bash\n\nXDG_DATA_HOME=${XDG_DATA_HOME:-$HOME/.local/share}\n"):
        fail("launch script must start with the standard boilerplate header")
    if "\r" in sh:
        fail("launch script has CRLF line endings")
    for banned in ("SDL_VIDEODRIVER", "SDL_AUDIODRIVER", "export LD_PRELOAD", "GPTOKEYB \""):
        if banned in sh:
            fail("launch script contains banned pattern: " + banned)
    if "$GPTOKEYB2" not in sh:
        fail("launch script must use $GPTOKEYB2")

    # naming and layout
    if not (GAME_DIR.islower() and GAME_DIR.isalnum()):
        fail("game folder must be lowercase and squashed")
    if any(f.endswith(".gptk") for _, _, fs in os.walk(PR) for f in fs):
        fail("classic .gptk file present; use the gptokeyb2 .ini")
    lic = os.path.join(PR, GAME_DIR, "licenses")
    if not os.path.isdir(lic) or not [f for f in os.listdir(lic) if os.path.getsize(os.path.join(lic, f)) > 0]:
        fail("licenses/ folder missing or empty")

    # README
    readme = read("README.md")
    if not readme.startswith("## Notes\n"):
        fail("README must start directly at '## Notes'")
    if "Unused" in readme or "Known Limitations" in readme:
        fail("README has a banned row or section")
    for section in ("## Controls", "## Compile"):
        if section not in readme:
            fail("README missing " + section)

    # gameinfo.xml
    gi = read("gameinfo.xml")
    if "<path>./" + SH + "</path>" not in gi:
        fail("gameinfo.xml path must match the launch script name")
    if "<image>./" + GAME_DIR + "/cover.png</image>" not in gi:
        fail("gameinfo.xml image must be the cover, with the game folder prefix")
    if "<desc>" not in gi or "<releasedate>" not in gi:
        fail("gameinfo.xml missing desc or releasedate")

    # screenshot and cover: PNG, at least 640x480, 4:3
    for img in ("screenshot.png", "cover.png"):
        head = open(os.path.join(PR, img), "rb").read(24)
        if head[:8] != b"\x89PNG\r\n\x1a\n":
            fail(img + " is not a PNG")
        else:
            w, h = struct.unpack(">II", head[16:24])
            if w < 640 or h < 480 or abs(w / h - 4 / 3) > 0.01:
                fail(img + " must be 4:3 and at least 640x480 (got %dx%d)" % (w, h))

if errors:
    print("PACKAGING RULE VIOLATIONS:")
    for e in errors:
        print("  - " + e)
    sys.exit(1)

# ---- 4. the installable zip: the port.json items plus the metadata files, no wrapping folder ----------
if os.path.exists(ZIP):
    os.remove(ZIP)
top = list(pj["items"]) + ["port.json", "README.md", "gameinfo.xml", "screenshot.png", "cover.png"]
with zipfile.ZipFile(ZIP, "w", zipfile.ZIP_DEFLATED) as z:
    for entry in top:
        path = os.path.join(PR, entry)
        walk = [(path, entry)] if os.path.isfile(path) else [
            (os.path.join(dp, f), os.path.relpath(os.path.join(dp, f), PR).replace(os.sep, "/"))
            for dp, _, fs in os.walk(path) for f in fs]
        for full, arc in walk:
            info = zipfile.ZipInfo.from_file(full, arc)
            info.compress_type = zipfile.ZIP_DEFLATED
            info.external_attr = (0o755 if arc.endswith(".sh") else 0o644) << 16
            z.writestr(info, open(full, "rb").read())

print("PR folder : " + PR)
print("Zip       : %s (%d KB)" % (ZIP, os.path.getsize(ZIP) // 1024))
print("All packaging rule checks passed.")
