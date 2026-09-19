# Space Trader for PortMaster

A port of Space Trader, the classic trading game, to handheld consoles through [PortMaster](https://portmaster.games/). Buy low, sell high and stay alive across 120 star systems. Trade goods between worlds, upgrade your ship, hire crew, take out loans and dodge pirates, police and space monsters on the way to buying your own moon.

The game is a Java port built with libGDX. The rules come from the open source Android version of Space Trader; the interface is new and made for a D-Pad and four face buttons. It runs at 640x480 and 1280x720, and other resolutions should scale.

Tested so far: dArkOS on an RG353V at 640x480.

## Install

Download `spacetrader.zip`, drop it in your PortMaster autoinstall folder and run PortMaster. The first launch needs an internet connection to download the Java and Westonpack runtimes (about 190 MB).

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

## Building

Requires JDK 17, Python 3, curl and git. The full steps are in [portmaster/spacetrader/README.md](portmaster/spacetrader/README.md); in short:

```bash
git clone https://github.com/Knifethrower/space-trader-pm.git
cd space-trader-pm
# download the libraries into libs/ (see the steps in the README above), then:
./package_portmaster.sh
```

This produces `build/spacetrader.zip`, ready to install, and `build/pr/spacetrader/`, the folder for a PortMaster pull request. The script also checks the package against the PortMaster packaging rules and fails if one is broken.

To try the game on a desktop:

```bash
./run_desktop.sh 1280 720
```

## Repository layout

| Folder | Contents |
|--|--|
| `core/` | Game rules and data, with no graphics. `GameState.java` is generated, so do not edit it by hand. |
| `desktop/` | The libGDX front end (`front/SpaceTraderApp.java`) and the launcher. |
| `portmaster/spacetrader/` | The PortMaster package files: launch script, `port.json`, `gameinfo.xml`, README, controller `.ini` and licences. |
| `tools/` | Generators, build helpers and the packaging checks. |
| `reference/` | `GameState.java.orig`, the input to the GameState generator. |

Developer notes, including how to regenerate `GameState.java`, are in [BUILDING.txt](BUILDING.txt).

## Credits

- [Pieter Spronck](https://www.spronck.net/spacetrader/) created Space Trader.
- Russell Wolf wrote the open source Android version that this port is built on.
- The port uses libGDX, LWJGL 3, GLFW, jemalloc, stb, FreeType and the DejaVu fonts. Their licences are in `portmaster/spacetrader/spacetrader/licenses/`.

## License

GNU General Public License v3.0 or later. See [LICENSE](LICENSE).
