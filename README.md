# DynamicHUD for Minecraft Fabric

DynamicHUD is a dev library for Minecraft mod creators who use Fabric mod loader for the latest minecraft version. With DynamicHUD, you can make your own HUD elements that players can change and interact with, making their game look and feel better.

# Examples
- [_IntegrationTest.java_](src/main/java/com/tanishisherewith/dynamichud/IntegrationTest.java)
## Demo

*May vary from version to version*

### Developed stage
<details>
	<summary>View Showcase video</summary>
	https://youtu.be/nQ-oiHyhrm8?si=vXLUPrj7plPRuPHv

</details>

### Mid stage (v1.2.0)
<details>
  <summary>View Demo video</summary>
	
  [!Mid stage demo video](https://github.com/V-Fast/DynamicHUD/assets/120117618/2abfcdf5-d786-4e58-acae-aefe51b77b4a)

</details>

### Super early stages
<details>
  <summary>View Demo video</summary>
  
  [!Early stage demo video](https://github.com/V-Fast/DynamicHUD/assets/120117618/04de9319-69cd-4456-a555-c026c7e053a2)
</details>

*_This mod / library is fabric only and has no future plans to be ported to forge._*


## Getting Started For Developers
> Visit our [wiki](https://tanishisherewith.gitbook.io/dynamic-hud) for more detailed information about the library!

To integrate DynamicHUD into your mod, add it as a dependency in your `build.gradle` file:

```groovy
allprojects {
	repositories {
		maven { url 'https://jitpack.io' }
	}
	maven {
		name 'Xander Maven'
		url 'https://maven.isxander.dev/releases'
	}
}

dependencies {
	// Dynamic HUD
	modImplementation 'com.github.V-Fast:DynamicHUD:<Version>'
}
```

## Contributing
Contributions are welcome! If you’re interested in improving DynamicHUD or adding new features, please review our contributing guidelines.


## Installation for Users

To ensure mods that depend on DynamicHUD work correctly, follow these steps to add DynamicHUD to your Minecraft installation:

0. Make sure you have [Fabric](https://fabricmc.net/) and [Fabric-API](https://modrinth.com/mod/fabric-api/) installed.
1. Download the `dynamichud-<version>.jar` file from the official [modrinth](https://modrinth.com/mod/dynamichud/versions) or [release](https://github.com/V-Fast/DynamicHUD/releases) page.
2. Navigate to your Minecraft directory. The default path is usually:
   - Windows: `%APPDATA%\.minecraft`
   - macOS: `~/Library/Application Support/minecraft`
   - Linux: `~/.minecraft`
3. Locate the `mods` folder within your Minecraft directory. If it doesn't exist, create it.
4. Place the downloaded DynamicHUD `.jar` file into the `mods` folder.
5. Run Minecraft with the Fabric loader. DynamicHUD will now be loaded, and any mods with DynamicHUD as a dependency can function properly.

Enjoy the enhanced HUD experience provided by mods utilizing DynamicHUD!

## License
DynamicHUD is released under the MIT License. Feel free to use and modify it in your mods, with proper attribution back to this repository.

## Support
Need assistance or have suggestions? Join our [Discord](https://discord.com/invite/Rqpn3C7yR5) community or submit an issue on our GitHub [repository](https://github.com/V-Fast/DynamicHUD).
