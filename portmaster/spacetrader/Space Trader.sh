#!/bin/bash

XDG_DATA_HOME=${XDG_DATA_HOME:-$HOME/.local/share}

if [ -d "/opt/system/Tools/PortMaster/" ]; then
  controlfolder="/opt/system/Tools/PortMaster"
elif [ -d "/opt/tools/PortMaster/" ]; then
  controlfolder="/opt/tools/PortMaster"
elif [ -d "$XDG_DATA_HOME/PortMaster/" ]; then
  controlfolder="$XDG_DATA_HOME/PortMaster"
else
  controlfolder="/roms/ports/PortMaster"
fi

source $controlfolder/control.txt
[ -f "${controlfolder}/mod_${CFW_NAME}.txt" ] && source "${controlfolder}/mod_${CFW_NAME}.txt"
get_controls

GAMEDIR=/$directory/ports/spacetrader
ini_filename="spacetrader.ini"
weston_runtime="weston_pkg_0.2"
java_runtime="zulu17.48.15-ca-jdk17.0.10-linux"

> "$GAMEDIR/log.txt" && exec > >(tee "$GAMEDIR/log.txt") 2>&1

# Westonpack: gives the Java game (LWJGL/GLFW, OpenGL 2 through GLX) an X11 + GL environment.
weston_dir=/tmp/weston
$ESUDO mkdir -p "${weston_dir}"
if [ ! -f "$controlfolder/libs/${weston_runtime}.squashfs" ]; then
  if [ ! -f "$controlfolder/harbourmaster" ]; then
    pm_message "This port requires the latest PortMaster to run, please go to https://portmaster.games/ for more info."
    sleep 5
    exit 1
  fi
  $ESUDO $controlfolder/harbourmaster --quiet --no-check runtime_check "${weston_runtime}.squashfs"
fi
if [[ "$PM_CAN_MOUNT" != "N" ]]; then
    $ESUDO umount "${weston_dir}"
fi
$ESUDO mount "$controlfolder/libs/${weston_runtime}.squashfs" "${weston_dir}"

# Java runtime (Zulu 17)
export JAVA_HOME="/tmp/javaruntime/"
$ESUDO mkdir -p "${JAVA_HOME}"
if [ ! -f "$controlfolder/libs/${java_runtime}.squashfs" ]; then
  if [ ! -f "$controlfolder/harbourmaster" ]; then
    pm_message "This port requires the latest PortMaster to run, please go to https://portmaster.games/ for more info."
    sleep 5
    exit 1
  fi
  $ESUDO $controlfolder/harbourmaster --quiet --no-check runtime_check "${java_runtime}.squashfs"
fi
if [[ "$PM_CAN_MOUNT" != "N" ]]; then
    $ESUDO umount "${JAVA_HOME}"
fi
$ESUDO mount "$controlfolder/libs/${java_runtime}.squashfs" "${JAVA_HOME}"
export PATH="$JAVA_HOME/bin:$PATH"

cd $GAMEDIR
mkdir -p "$GAMEDIR/savedata" "$GAMEDIR/tmp"

# Class-data archive: cuts JVM start-up time. Made on the first run, reused afterwards, rebuilt if the jar changes.
JSA="$GAMEDIR/spacetrader.jsa"
[ "$GAMEDIR/spacetrader.jar" -nt "$JSA" ] && rm -f "$JSA"
if [ -f "$JSA" ]; then
  CDS="-XX:SharedArchiveFile=$JSA"
else
  CDS="-XX:ArchiveClassesAtExit=$JSA"
fi

$GPTOKEYB2 "java" -c "$GAMEDIR/$ini_filename" &

# tmp/ is Java's temp directory (java.io.tmpdir): the libGDX and LWJGL native libraries are unpacked there at start-up.
# It is kept inside the game folder as a precaution; the default temp directory has not been tested.
$ESUDO env WESTON_HEADLESS_WIDTH=$DISPLAY_WIDTH WESTON_HEADLESS_HEIGHT=$DISPLAY_HEIGHT \
$weston_dir/westonwrap.sh headless noop kiosk crusty_glx_gl4es \
PATH=$PATH JAVA_HOME=$JAVA_HOME XDG_SESSION_TYPE=x11 WAYLAND_DISPLAY= XDG_DATA_HOME=$GAMEDIR \
java -Xms32m -Xmx128m -XX:ReservedCodeCacheSize=32m -XX:TieredStopAtLevel=1 -XX:+UseSerialGC $CDS \
-Djava.io.tmpdir=$GAMEDIR/tmp -Dst.hidecursor=1 -Dst.save=$GAMEDIR/savedata/save.properties \
-jar $GAMEDIR/spacetrader.jar $DISPLAY_WIDTH $DISPLAY_HEIGHT

$ESUDO $weston_dir/westonwrap.sh cleanup
if [[ "$PM_CAN_MOUNT" != "N" ]]; then
    $ESUDO umount "${weston_dir}"
    $ESUDO umount "${JAVA_HOME}"
fi
pm_finish
