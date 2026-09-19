#!/bin/bash
# Compiles the game and builds the PortMaster port: build/spacetrader.zip and build/pr/spacetrader/.
cd "$(dirname "$0")"
set -e

JAVAC="${JAVA_HOME:+$JAVA_HOME/bin/}javac"
for c in python3 python; do "$c" --version >/dev/null 2>&1 && PY=$c && break; done
case "$(uname -s)" in
  Linux*|Darwin*) SEP=":" ;;
  *) SEP=";" ;;
esac
CP=$(ls libs/*.jar | tr '\n' "$SEP")

rm -rf build/classes build/desktop
mkdir -p build/classes build/desktop
"$JAVAC" -encoding UTF-8 --release 11 -d build/classes $(find core/src/main/java -name "*.java")
"$JAVAC" -encoding UTF-8 --release 11 -cp "build/classes$SEP$CP" -d build/desktop $(find desktop/src -name "*.java")

"$PY" tools/package_port.py
