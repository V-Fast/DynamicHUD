# DynamicHUD for Minecraft Fabric

DynamicHUD is a special tool for Minecraft mod creators who use Fabric. It works with Minecraft 1.19.1 and newer versions. With DynamicHUD, you can make your own HUD parts that players can change and interact with, making their game look and feel better.
> _It may be ported back upto 1.16 fabric on demand._

# Examples
- [_DynamicHUDtest.java_](src/main/java/com/tanishisherewith/dynamichud/DynamicHUDtest.java)
- 
## Demo

*May vary from version to version*

### Developed stage
> In progress
---

### Mid stage (1.2.0)
<details>
  <summary>View Demo video</summary>
  
  [!Mid stage demo video](https://github.com/V-Fast/DynamicHUD/assets/120117618/2abfcdf5-d786-4e58-acae-aefe51b77b4a)
</details>

### Early stages
<details>
  <summary>View Demo video</summary>
  
  [!Early stage demo video](https://github.com/V-Fast/DynamicHUD/assets/120117618/04de9319-69cd-4456-a555-c026c7e053a2)
</details>

## Features

- **Automatic Loading & Saving**: Widgets automatically save their state and reload upon game restart, providing a seamless experience for players.
- **Dynamic Values**: Widgets can display real-time information, adapting to in-game events and data changes.
- **Inbuilt Widgets**: Start with `TextWidget` for easy text display and expand with more specialized widgets.
- **Utility Classes**: Utilize `DrawHelper`, `ColorHelper`, and `TextureHelper` for efficient and streamlined development.
- **ContextMenu**: A versatile context menu featuring boolean, slider, runnable, color options, and list/enum selections for comprehensive widget customization.
- **Screen Border Control**: Widgets are confined within the screen boundaries to prevent off-screen drift.
- **Snapping**: Widgets automatically snap to a grid corner in a imaginary grid pattern on the screen when the shift key is pressed.
- **Multi-Mod Support**: Designed for compatibility and easy integration across various mods.
- **Easy Integration**: Simple setup for quick implementation into your projects.
- **Comprehensive Wiki**: A detailed guide and reference for all features of DynamicHUD, available at https://tanishisherewith.gitbook.io/dynamic-hud.

## Disclaimer
DynamicHUD is a library for Minecraft Fabric designed to provide developers with the tools to create customizable HUD elements. It does not add widgets or any functionality on its own. Interaction with DynamicHUD's internals and utilization of its features can only be done through mods that implement this library. Users looking for in-game HUD elements should refer to mods that use DynamicHUD.

*_This mod / library is fabric only and has no future plans to be ported to forge._*

## Installation for Users

To ensure mods that depend on DynamicHUD work correctly, follow these steps to add DynamicHUD to your Minecraft installation:

0. Make sure you have [Fabric](https://fabricmc.net/) and [Fabric-API](https://modrinth.com/mod/fabric-api/) installed.
1. Download the `dynamichud-<version>.jar` file from the official [release](https://modrinth.com/mod/dynamichud/versions) page.
2. Navigate to your Minecraft directory. The default path is usually:
   - Windows: `%APPDATA%\.minecraft`
   - macOS: `~/Library/Application Support/minecraft`
   - Linux: `~/.minecraft`
3. Locate the `mods` folder within your Minecraft directory. If it doesn't exist, create it.
4. Place the downloaded DynamicHUD `.jar` file into the `mods` folder.
5. Run Minecraft with the Fabric loader. DynamicHUD will now be loaded, and any mods with DynamicHUD as a dependency can function properly.

Enjoy the enhanced HUD experience provided by mods utilizing DynamicHUD!

## Getting Started For Developers
> Visit our [wiki](https://tanishisherewith.gitbook.io/dynamic-hud) for more detailed information about the library!

To integrate DynamicHUD into your mod, add it as a dependency in your `build.gradle` file:

```groovy
allprojects {
	repositories {
		maven { url 'https://jitpack.io' }
	}
}

dependencies {
	// Dynamic HUD
	modImplementation 'com.github.V-Fast:DynamicHUD:<Version>'
}

```

## Contributing
Contributions are welcome! If you’re interested in improving DynamicHUD or adding new features, please review our contributing guidelines.

## License
DynamicHUD is released under the MIT License. Feel free to use and modify it in your mods, with proper attribution back to this repository.

## Support
Need assistance or have suggestions? Join our [Discord](https://discord.com/invite/Rqpn3C7yR5) community or submit an issue on our GitHub [repository](https://github.com/V-Fast/DynamicHUD).
