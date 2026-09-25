"""Builds build/spacetrader.jar: our classes + resources + libGDX/LWJGL and their natives (windows + linux arm64)."""
import glob, os, zipfile, sys
import sys
ARM_ONLY = "--arm" in sys.argv
ARM_AUDIO = "--arm-audio" in sys.argv  # dev-only: like --arm but keeps OpenAL, for testing st.testmusic on a device
if ARM_AUDIO:
    out = "build/spacetrader-arm-audio.jar"
elif ARM_ONLY:
    out = "build/spacetrader-arm.jar"
else:
    out = "build/spacetrader.jar"
SKIP = ("windows/", "macos/", "linux/x64/", "gdx.dll", "gdx64.dll", "libgdx64.so", "libgdxarm.so", "libgdx-freetype.dll", "gdx-freetype.dll", "gdx-freetype64.dll", "libgdx-freetype64.so", "libgdx-freetypearm.so", "libgdx.dylib", "libgdx64.dylib")
seen = set()
def add(z, name, data):
    if (ARM_ONLY or ARM_AUDIO) and name.startswith(SKIP): return
    if name in seen or name.startswith("META-INF/") and not name.startswith("META-INF/services/") or name.endswith("/"):
        return
    seen.add(name); z.writestr(name, data)
with zipfile.ZipFile(out, "w", zipfile.ZIP_DEFLATED) as z:
    z.writestr("META-INF/MANIFEST.MF", "Manifest-Version: 1.0\r\nMain-Class: com.brucelet.spacetrader.desktop.DesktopLauncher\r\n\r\n")
    seen.add("META-INF/MANIFEST.MF")
    for root in ("build/classes", "build/desktop", "core/src/main/resources"):
        for path in glob.glob(root + "/**/*", recursive=True):
            if os.path.isfile(path):
                add(z, os.path.relpath(path, root).replace(os.sep, "/"), open(path, "rb").read())
    for jar in sorted(glob.glob("libs/*.jar")):
        if ARM_ONLY and "openal" in os.path.basename(jar): continue  # audio is disabled; no need to ship OpenAL
        with zipfile.ZipFile(jar) as j:
            for n in j.namelist():
                if (ARM_ONLY or ARM_AUDIO) and n.endswith((".dylib", ".dll")): continue  # macOS/Windows natives are useless on the device
                add(z, n, j.read(n))
print(out, os.path.getsize(out) // 1024, "KB,", len(seen), "entries")
