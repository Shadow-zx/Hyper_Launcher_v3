<H1 align="center">Hyper Launcher v3</H1>

<p align="center"><i>A fork of <a href="https://github.com/MojoLauncher/MojoLauncher">MojoLauncher</a></i></p>

* Hyper Launcher is a launcher, forked from [MojoLauncher](https://github.com/MojoLauncher/MojoLauncher) (which itself is based on [PojavLauncher](https://github.com/PojavLauncherTeam/PojavLauncher)), that lets you play Minecraft: Java Edition on your Android device — with a heavier focus on **customizability, mod management based on Material 3 UI**.

* Like its upstream, it can run almost every version of Minecraft, letting you use .jar-only installers for modloaders like [Forge](https://files.minecraftforge.net/) and [Fabric](http://fabricmc.net/), and mods like [OptiFine](https://optifine.net).

## Navigation
- [Introduction](#introduction)
- [What's Different from MojoLauncher](#whats-different-from-mojolauncher)
- [Getting Hyper Launcher](#getting-hyper-launcher)
- [Building](#building)
- [Current roadmap](#current-roadmap)
- [License](#license)
- [Contributing](#contributing)
- [Credits & Third party components and their licenses](#credits--third-party-components-and-their-licenses-if-available)

## Introduction
* Hyper Launcher is a Minecraft: Java Edition launcher for Android, built on top of MojoLauncher's codebase.
* It can launch almost all available Minecraft versions, ranging from early rd- builds through modern snapshots (including Combat Test versions).
* Modding via Forge and Fabric is supported out of the box.

## What's Different from MojoLauncher
Hyper Launcher keeps MojoLauncher as its base but shifts focus toward three areas:

- **Customizability** — expanded theming, layout, and per-instance configuration options beyond what upstream exposes.
- **Mod Management** — a more built-in workflow for installing, organizing, updating, and removing mods, aiming to reduce reliance on manually dropping jars into folders.
- **New Renderers** — additional/experimental rendering backends alongside the existing GL4ES-based pipeline, giving more flexibility on compatibility vs. performance.


## Getting Hyper Launcher

You can get Hyper Launcher via:

1. Prebuilt builds from the [releases section](https://github.com/hollowlauncher/Hyper_Launcher_v3/releases).
2. Early/dev builds from [GitHub Actions](https://github.com/hollowlauncher/Hyper_Launcher_v3/actions).
3. [Building](#building) from source.

## Building
* Build the launcher (it will automatically download all required components)
```
./gradlew :app_pojavlauncher:assembleDebug
```
(Replace `./gradlew` with `.\gradlew.bat` if you are building on Windows).

## Current roadmap
- [ ] Renderer selection UI (switch between backends per-instance)
- [ ] In-launcher mod manager (install/update/remove without leaving the app)
- [ ] Expanded theming/customization options
- [ ] mrpack/CurseForge zip import (inherited from upstream)
- [ ] MMC-compatible instance import

## Known Issues
- Some physical mice may have very slow mouse speed
- On Holy GL4ES, large texture atlases may be distorted (stretched/blocky textures in modpacks)
- Fork-specific issues will be tracked separately as features land

## License
- Hyper Launcher is licensed under [GNU LGPLv3](https://github.com/hollowlauncher/Hyper_Launcher_v3/blob/v3_openjdk/LICENSE), consistent with its MojoLauncher/PojavLauncher upstream.

## Contributing
Contributions are welcome! This fork especially welcomes help with the customization, mod-management — but general contributions, bug reports, and translation help are all appreciated too.

Any code change to this repository should be submitted as a pull request. The description should explain what the code does and give steps to execute it.

## Credits & Third party components and their licenses (when applicable)
Hyper Launcher builds on top of MojoLauncher and PojavLauncher, and inherits their third-party components:

- [MojoLauncher](https://github.com/MojoLauncher/MojoLauncher): [GNU LGPLv3 License](https://github.com/MojoLauncher/MojoLauncher/blob/v3_openjdk/LICENSE)
- [PojavLauncher](https://github.com/PojavLauncherTeam/PojavLauncher): [GNU LGPLv3 License](https://github.com/PojavLauncherTeam/PojavLauncher/blob/v3_openjdk/LICENSE)
- [Boardwalk](https://github.com/zhuowei/Boardwalk) (JVM Launcher): Unknown License/[Apache License 2.0](https://github.com/zhuowei/Boardwalk/blob/master/LICENSE) or GNU GPLv2.
- Android Support Libraries: [Apache License 2.0](https://android.googlesource.com/platform/prebuilts/maven_repo/android/+/master/NOTICE.txt).
- [Holy GL4ES](https://github.com/artdeell/gl4es_extra_extra/): [MIT License](https://github.com/ptitSeb/gl4es/blob/master/LICENSE).
- [OpenJDK](https://github.com/PojavLauncherTeam/openjdk-multiarch-jdk8u): [GNU GPLv2 License](https://openjdk.java.net/legal/gplv2+ce.html).
- [GLFW](https://github.com/MojoLauncher/glfw): [zlib license](https://github.com/MojoLauncher/glfw/blob/glfw34/LICENSE.md)
- [LWJGL2-GLFW](https://github.com/MojoLauncher/lwjgl2-glfw): 3-Clause BSD license
- [LWJGL3](https://github.com/LWJGL/lwjgl3): [BSD-3 License](https://github.com/LWJGL/lwjgl3/blob/master/LICENSE.md).
- [Mesa 3D Graphics Library](https://gitlab.freedesktop.org/mesa/mesa): [MIT License](https://docs.mesa3d.org/license.html).
- [pro-grade](https://github.com/pro-grade/pro-grade) (Java sandboxing security manager): [Apache License 2.0](https://github.com/pro-grade/pro-grade/blob/master/LICENSE.txt).
- [bhook](https://github.com/bytedance/bhook) (exit code trapping): [MIT license](https://github.com/bytedance/bhook/blob/main/LICENSE).
- [Authlib-Injector](https://github.com/yushijinhun/authlib-injector) (authorisation via ely.by): [AGPL-3.0](https://github.com/yushijinhun/authlib-injector/blob/develop/LICENSE).
- [alsoft](https://github.com/kcat/openal-soft/) (audio output library): [GNU LIBRARY GENERAL PUBLIC LICENSE](https://github.com/kcat/openal-soft/blob/master/COPYING) and [modified PFFFT](https://github.com/kcat/openal-soft/blob/master/LICENSE-pffft).
- [oboe](https://github.com/google/oboe): [Apache License 2.0](https://github.com/google/oboe/blob/main/LICENSE).
- Thanks to [Mineskin](https://mineskin.eu/) for providing Minecraft avatars.
