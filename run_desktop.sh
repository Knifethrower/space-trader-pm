#!/bin/bash
# Dev runner (javac build, no Gradle). Usage: ./run_desktop.sh [width height] ; extra JVM flags via JAVA_OPTS
cd "$(dirname "$0")"
export JAVA_HOME="${JAVA_HOME:-C:/Program Files/Eclipse Adoptium/jdk-17.0.20.101-hotspot}"
CP=$(ls libs/*.jar | grep -v arm64 | tr '\n' ';')
mkdir -p build/classes build/desktop
"$JAVA_HOME/bin/javac" -encoding UTF-8 --release 11 -d build/classes $(find core/src/main/java -name "*.java") || exit 1
"$JAVA_HOME/bin/javac" -encoding UTF-8 --release 11 -cp "build/classes;$CP" -d build/desktop $(find desktop/src -name "*.java") || exit 1
exec "$JAVA_HOME/bin/java" $JAVA_OPTS -cp "build/classes;build/desktop;core/src/main/resources;$CP" com.brucelet.spacetrader.desktop.DesktopLauncher "$@"
