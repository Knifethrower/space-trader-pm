"""Makes package-private members of the top-level class public so the frontend (another package) can use them."""
import re, sys
skip = ("public", "private", "protected", "return", "if", "for", "while", "else", "case", "switch", "try", "new", "throw", "default", "break", "continue")
member = re.compile(r"^\t((?:final |static |volatile |abstract |synchronized )*)([A-Za-z][\w<>\[\],?. ]*?)\s+(\w+)\s*(\(|=|;)")
for f in sys.argv[1:]:
    out = []; n = 0
    for line in open(f, encoding="utf-8").read().split("\n"):
        if line.startswith("\t") and not line.startswith("\t\t") and not line.startswith("\t//") and not line.startswith("\t*") and not line.startswith("\t/*") and not line.startswith("\t@") and not line.startswith("\t}"):
            first = line.strip().split(" ")[0].split("(")[0]
            if first not in skip and member.match(line) and "class " not in line.split("(")[0] and "enum " not in line.split("(")[0] and "interface " not in line.split("(")[0]:
                line = "\tpublic " + line[1:]; n += 1
        out.append(line)
    open(f, "w", encoding="utf-8").write("\n".join(out))
    print(f, n)
