# No Feather Trample

Players and mobs with Feather Falling on equipped boots (any level) or the Slow Falling status effect cannot trample farmland.
Crops and farmland moisture are preserved. Ordinary trampling, environmental farmland changes and vanilla fall damage still apply.

## Installation

Install `no_feather_trample-<version>.jar` in the server's `mods` directory. 
The mod also works in singleplayer when installed on the client. 
Multiplayer clients do not need the mod.

## Build and verification

Run `./gradlew build` (Windows: `.\gradlew.bat build`) with Java 25.
The installable jar is in `build/libs`; do not install the `-sources.jar`.
`build` includes configuration and text-rendering regression verification.

## License

AGPL-3.0; see [LICENSE](LICENSE). Copyright (c) 2026 SwordfishBE.