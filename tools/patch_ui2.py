"""Second batch of UI rewrites: system info, newspaper. Run by patch_ui.py before the final publicize step."""
import re, sys

F = "core/src/main/java/com/brucelet/spacetrader/datatypes/GameState.java"
t = open(F, encoding="utf-8").read()

# 1. system information: keep the news/visit bookkeeping, drop the view updates
t, n = re.subn(
    r"\t\tBaseScreen screen = mGameManager\.getCurrentScreen\(\);\n\t\tif \(screen == null \|\| screen\.getView\(\) == null \|\| screen\.getType\(\) != ScreenType\.INFO\) return;.*?screen\.setViewVisibilityById\(R\.id\.screen_info_merc, getForHire\(\) != null\);\n",
    "\t\tspecialAvailable = showSpecial;\n", t, flags=re.S)
assert n == 1, "system info tail"

# 2. newspaper: headlines collected into a list instead of views
t, n = re.subn(
    r"\tprivate void displayHeadline\(int stringId, Object\.\.\. args\) \{.*?\n\t\}\n+\tprivate void displayHeadline\(String string, Object\.\.\. args\) \{.*?\n\t\}\n",
    """\tprivate int headlineCount = 0;
\tprivate final java.util.List<String> headlines = new java.util.ArrayList<>();
\tprivate void displayHeadline(int stringId, Object... args) {
\t\tif (headlineCount > MAXSTORIES) return;
\t\theadlineCount++;
\t\theadlines.add(getResources().getString(stringId, args));
\t}
\tprivate void displayHeadline(String string, Object... args) {
\t\tif (headlineCount > MAXSTORIES) return;
\t\theadlineCount++;
\t\theadlines.add(String.format(string, args));
\t}
""", t, flags=re.S)
assert n == 1, "displayHeadline"

t = t.replace("\tpublic void drawNewspaperForm()\n\t{\n\t\theadlineCount = 0;",
              "\t/** Builds the headlines for the newspaper of the current system. */\n\tpublic java.util.List<String> newspaperHeadlines()\n\t{\n\t\theadlineCount = 0;\n\t\theadlines.clear();")
assert "newspaperHeadlines" in t
t, n = re.subn(r"(\t\t\t\t\tshown\[j\] = true;\n\t\t\t\t\}\n\t\t\t\}\n\t\t\}\n)",
               r"\1\t\treturn new java.util.ArrayList<>(headlines);\n", t)
assert n == 1, "return headlines"

open(F, "w", encoding="utf-8").write(t)
print("patched2")
