# Grape Launcher

A lightweight and minimalist Minecraft: Java Edition launcher for Android.

Grape Launcher is an unofficial modified launcher based on Zalith Launcher 2, focused on a simple mobile experience and Minecraft Java Edition 1.12.2.

> **Grape Launcher is an unofficial modified version and is not affiliated with Mojang Studios, Microsoft, Zalith Launcher, or PojavLauncher.**

## Features

- Minimalist launcher interface
- Minecraft Java Edition 1.12.2 focused experience
- Existing account and authentication flow
- Account and profile management
- 3D Minecraft skin preview
- Built-in skin editor
- Import custom PNG skins
- Customizable touch controls
- Custom default control layout
- PojavLauncher-based Minecraft launching core
- Android-focused UI built with Jetpack Compose

## Minecraft Version

Grape Launcher currently focuses on:

**Minecraft Java Edition 1.12.2**

The launcher interface is intentionally simplified around this version instead of exposing the normal version-selection workflow.

## Skin System

Grape Launcher includes a simple skin workflow:

- **Choose Skin**
  - Make a Skin
  - Import Skin
- 3D player preview
- 64×64 skin editing
- Pixel-based skin painting
- Local skin storage
- Skin refresh after importing or saving

## Touch Controls

Grape Launcher includes a custom default touch-control layout.

The default layout contains:

- Virtual movement joystick
- Ctrl
- Alt
- Left mouse button
- Right mouse button
- Shift
- Space
- Esc
- Tab
- E
- T
- F3
- F5
- IME control
- Additional control layers

The bundled default layout is stored in:

```text
ZalithLauncher/src/main/assets/default_layout.json
```

## Project Structure

```text
Grape-launcher/
├── ZalithLauncher/
│   ├── src/
│   │   └── main/
│   │       ├── assets/
│   │       │   └── default_layout.json
│   │       └── java/
│   └── ...
├── LICENSE
├── README.md
└── ...
```

## Building

### Requirements

- Android Studio Bumblebee or newer
- Android SDK
- JDK 11
- Android SDK API 26 or newer

### Build

Clone the repository:

```bash
git clone https://github.com/Coolguyhehe2515/Grape-launcher.git
cd Grape-launcher
```

Open the project in Android Studio and build the Android application.

## Credits

Grape Launcher is based on the work of:

- Zalith Launcher 2
- PojavLauncher
- Contributors to the Android Minecraft launcher ecosystem

The upstream Zalith Launcher 2 project is licensed under GNU GPL-3.0 and includes additional terms for modified versions.

## License

Grape Launcher is licensed under the **GNU General Public License v3.0**.

See [LICENSE](LICENSE) for the complete license text.

Grape Launcher is a modified version of an upstream GPL-3.0 project. Applicable upstream copyright notices and license requirements must be preserved when redistributing modified versions.

## Disclaimer

Grape Launcher is an unofficial third-party project.

It is **not affiliated with, endorsed by, or sponsored by**:

- Mojang Studios
- Microsoft
- Zalith Launcher
- PojavLauncher

Minecraft is a trademark of Mojang Studios.

## Contributing

Contributions, bug reports, and improvements are welcome.

Please keep the project's goals in mind:

- Keep the launcher lightweight.
- Keep the interface simple.
- Preserve existing authentication and security functionality.
- Avoid unnecessary features.
- Keep Minecraft 1.12.2 as the primary supported version.
- Maintain compatibility with Android devices.
