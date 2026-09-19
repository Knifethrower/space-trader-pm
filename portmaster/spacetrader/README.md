## Notes

Thanks to [Pieter Spronck](https://www.spronck.net/spacetrader/) for creating Space Trader, and to Russell Wolf for the open source Android version this port is built on. A compact trading game where every jump between 120 star systems is a gamble on prices, pirates and the police.

## Controls

| Key | Action |
|--|--|
| D-Pad / Left Stick | Move, change values |
| A | Select |
| B | Back |
| X | Alternate action (jettison cargo, take all, track a system) |
| Y | Average price list, on the charts and the warp list |
| Start | Select |
| Select | Back |

## Compile

Requires JDK 17, Python 3, curl and git.

```bash
git clone https://github.com/Knifethrower/space-trader-pm.git
cd space-trader-pm
```

Run everything below from that directory.

### 1. Download the libraries

```bash
mkdir -p libs && cd libs
M=https://repo1.maven.org/maven2
for f in \
  com/badlogicgames/gdx/gdx/1.12.1/gdx-1.12.1.jar \
  com/badlogicgames/gdx/gdx-backend-lwjgl3/1.12.1/gdx-backend-lwjgl3-1.12.1.jar \
  com/badlogicgames/gdx/gdx-platform/1.12.1/gdx-platform-1.12.1-natives-desktop.jar \
  com/badlogicgames/gdx/gdx-jnigen-loader/2.3.1/gdx-jnigen-loader-2.3.1.jar \
  com/badlogicgames/gdx/gdx-freetype/1.12.1/gdx-freetype-1.12.1.jar \
  com/badlogicgames/gdx/gdx-freetype-platform/1.12.1/gdx-freetype-platform-1.12.1-natives-desktop.jar; do
  curl -sSfLO "$M/$f"
done
for m in lwjgl lwjgl-glfw lwjgl-jemalloc lwjgl-opengl lwjgl-stb; do
  curl -sSfLO "$M/org/lwjgl/$m/3.3.3/$m-3.3.3.jar"
  curl -sSfLO "$M/org/lwjgl/$m/3.3.3/$m-3.3.3-natives-linux-arm64.jar"
done
cd ..
```

### 2. Build the port

```bash
./package_portmaster.sh
```
