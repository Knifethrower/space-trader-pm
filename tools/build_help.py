"""Converts the Android app's help page (assets/spacetrader.html) into core/src/main/resources/help.txt.

Output format (plain text, UTF-8):
    #S Section title          starts a section (an <h2> in the HTML)
    #T Sub-section title      starts a sub-section of the current section (an <h3>)
    other lines               paragraphs, one per line; list items start with a bullet
"""
import re
import sys
from html.parser import HTMLParser

SRC = "C:/Claude/Space Trader/com.brucelet.spacetrader_23_src/app/src/main/assets/spacetrader.html"
OUT = "core/src/main/resources/help.txt"


class Parser(HTMLParser):
    def __init__(self):
        super().__init__(convert_charrefs=True)
        self.out = []          # list of ("S"|"T"|"P", text)
        self.buf = []
        self.heading = None    # "S" or "T" while inside a heading
        self.started = False   # skip the title and index before the first <h2>

    def flush_paragraph(self):
        text = re.sub(r"\s+", " ", "".join(self.buf)).strip()
        self.buf = []
        if text and self.started:
            self.out.append(("P", text))

    def handle_starttag(self, tag, attrs):
        tag = tag.lower()
        if tag in ("h2", "h3"):
            self.flush_paragraph()
            self.heading = "S" if tag == "h2" else "T"
            if tag == "h2":
                self.started = True
        elif tag in ("p", "br", "ul"):
            self.flush_paragraph()
        elif tag == "li":
            self.flush_paragraph()
            self.buf.append("\u2022 ")

    def handle_endtag(self, tag):
        tag = tag.lower()
        if tag in ("h2", "h3"):
            text = re.sub(r"\s+", " ", "".join(self.buf)).strip()
            self.buf = []
            if self.started and text:
                self.out.append((self.heading, text))
            self.heading = None
        elif tag in ("li", "ul"):
            self.flush_paragraph()

    def handle_data(self, data):
        self.buf.append(data)


p = Parser()
p.feed(open(SRC, encoding="utf-8", errors="replace").read())
p.flush_paragraph()

# Passages that describe screens the handheld port does not have are reworded to match what it does have.
TEXT_FIXES = [
    ("And prices always fluctuate a bit.",
     "And prices always fluctuate a bit. On the Hard and Impossible levels the list only shows prices for systems you have already visited."),
]
for i, (kind, text) in enumerate(p.out):
    for old, new in TEXT_FIXES:
        if kind == "P" and old in text:
            p.out[i] = (kind, text.replace(old, new))

# Sections left out on purpose: "A Last Word" only holds the original Android port author's contact email.
SKIP_SECTIONS = {"A Last Word"}
kept, skipping = [], False
for kind, text in p.out:
    if kind == "S":
        skipping = text in SKIP_SECTIONS
    if not skipping:
        kept.append((kind, text))
p.out = kept

# Append the hand-written About section (credits and license notice).
for raw in open("tools/about_help.txt", encoding="utf-8").read().splitlines():
    if raw.startswith("#S "):
        p.out.append(("S", raw[3:]))
    elif raw.startswith("#T "):
        p.out.append(("T", raw[3:]))
    elif raw.strip():
        p.out.append(("P", raw.strip()))

lines = []
for kind, text in p.out:
    lines.append(("#S " if kind == "S" else "#T " if kind == "T" else "") + text)
open(OUT, "w", encoding="utf-8", newline="\n").write("\n".join(lines) + "\n")

sections = sum(1 for k, _ in p.out if k == "S")
subs = sum(1 for k, _ in p.out if k == "T")
paras = sum(1 for k, _ in p.out if k == "P")
print(f"{OUT}: {sections} sections, {subs} sub-sections, {paras} paragraphs, {sum(len(t) for _, t in p.out)} characters")
