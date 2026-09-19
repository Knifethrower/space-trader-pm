"""Assembles core/.../datatypes/GameState.java from reference/GameState.java.orig.

Copies UI-free members verbatim (by method start line) and strips the Android-only
fields. Re-run after adjusting BLOCKS; the output file is meant to be edited by hand
afterwards, so only run it once unless you're prepared to redo manual edits."""
import re, sys
SRC = "reference/GameState.java.orig"
OUT = "core/src/main/java/com/brucelet/spacetrader/datatypes/GameState.java"
L = open(SRC, encoding="utf-8", errors="replace").read().replace("\r", "").split("\n")

def block_end(start):
    d = 0; seen = False
    for i in range(start - 1, len(L)):
        d += L[i].count("{") - L[i].count("}")
        if "{" in L[i]: seen = True
        if seen and d == 0: return i + 1
    raise ValueError(start)

def with_comments(start):
    s = start
    while s > 1 and L[s - 2].strip().startswith(("//", "/*", "*", "@")): s -= 1
    return s

# Method start lines of UI-free blocks (see tools/methods.txt for the map).
BLOCKS = [401, 549, 823, 857, 917, 1133, 1143, 1155, 1450, 1466, 1482, 1502, 2050, 2083,
          5977, 5998, 6006, 6014, 6031, 6040, 6051, 6071, 6080, 6168, 6728, 6799, 6825, 6834,
          7282, 7440, 7450, 7470, 7478, 7490, 7932, 7944, 7989, 8003, 8015, 8122, 8495,
          8667, 9018, 10489, 10539,
          # travel / arrival / encounter (UI calls redirected to GameUI)
          8167, 9621, 9682, 10160, 10522, 3373, 3378, 3536, 3541, 3982, 4039, 4085,
          # new game, trading, misc state
          9078, 2600, 2651, 2152, 1950, 2541, 2569, 6587, 6617, 2735, 1587, 6902, 7501, 7679, 7685, 7692, 7704, 8028, 9030]
# small members copied by explicit ranges: (first, last)
RANGES = [(780, 822), (921, 957), (6021, 6029), (4059, 4084)]

out = []
# header: package + imports (drop Android and UI)
for l in L[:133]:
    if l.startswith("import android.") or l.startswith("import com.brucelet.spacetrader.") and not "datatypes" in l and not "enumtypes" in l:
        continue
    out.append(l)
out.append("import com.brucelet.spacetrader.platform.Resources;")
out.append("import com.brucelet.spacetrader.platform.SharedPreferences;")
out.append("import com.brucelet.spacetrader.R;")

# fields (lines 134-355), minus Android-only pieces
f = L[133:356]
skip = False; fields = []
i = 0
while i < len(f):
    l = f[i]
    if "@TargetApi" in l or "class ShipFlashUpdateListener" in l:
        # skip annotation + class body
        j = i
        while "class ShipFlashUpdateListener" not in f[j]: j += 1
        d = 0; seen = False
        while True:
            d += f[j].count("{") - f[j].count("}")
            if "{" in f[j]: seen = True
            if seen and d == 0: break
            j += 1
        i = j + 1; continue
    if re.search(r"mGameManager|Paint|ValueAnimator|ShipFlashUpdateListener|Canvas", l):
        i += 1; continue
    fields.append(l); i += 1
out += fields
out.append("")
out.append("\tprivate Resources getResources() { return Resources.get(); }")
out.append("")
for a, b in RANGES:
    out += L[a - 1:b] + [""]
for s in BLOCKS:
    out += L[with_comments(s) - 1:block_end(s)] + [""]
out.append("}")
txt = "\n".join(out) + "\n"
txt = txt.replace("import com.brucelet.spacetrader.enumtypes.ThemeType;\n", "")
txt = re.sub(r"\n[^\n]*\bLog\.d\([^\n]*\n", "\n", txt)
txt = txt.replace("Application.DEVELOPER_MODE", "DEVELOPER_MODE")
for a, b in [("mGameManager.showDialogFragment(", "ui.showDialog("),
             ("mGameManager.setCurrentScreenType(", "ui.setScreen("),
             ("mGameManager.getCurrentScreenType()", "ui.currentScreen()"),
             ("mGameManager.getResources()", "getResources()"),
             ("mGameManager.autosave()", "ui.autosave()"),
             ("mGameManager.clearBackStack()", "ui.clearBackStack()")]:
    txt = txt.replace(a, b)
txt = txt.replace("import java.util.Arrays;",
    "import com.brucelet.spacetrader.platform.AsyncTask;\n"
    "import com.brucelet.spacetrader.platform.Handler;\n"
    "import com.brucelet.spacetrader.ui.*;\n\n"
    "import java.util.Arrays;")
txt = txt.replace("\tprivate Resources getResources()",
    "\tstatic final boolean DEVELOPER_MODE = false;\n\n"
    "\tprivate GameUI ui = GameUI.NONE;\n\n"
    "\tpublic void setUI(GameUI ui) { this.ui = ui; }\n\n"
    "\t/** Stops any auto attack/flee. */\n"
    "\tpublic void clearButtonAction() {\n\t\tautoAttack = false;\n\t\tautoFlee = false;\n\t}\n\n"
    "\tprivate Resources getResources()")
open(OUT, "w", encoding="utf-8").write(txt)
print("wrote", OUT, len(out), "lines")

import subprocess, sys
subprocess.check_call([sys.executable, "tools/patch_ui.py"])
