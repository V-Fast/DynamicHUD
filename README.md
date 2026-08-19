<div align="center">

<img src="https://cdn.modrinth.com/data/hQbzUScT/5748eb2d8941f930df1e23d8e89d555dc0a5bb6e_96.webp" alt="DynamicHUD Logo" width="128" />

**An in-game HUD engine for Fabric mod creators**

[![Fabric](https://img.shields.io/badge/Platform-Fabric-brightgreen?style=for-the-badge&logo=fabric)](https://fabricmc.net/)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](https://github.com/V-Fast/DynamicHUD/blob/master/LICENSE)
[![Discord](https://img.shields.io/badge/Discord-Join-5865F2?style=for-the-badge&logo=discord&logoColor=white)](https://discord.com/invite/Rqpn3C7yR5)
[![Docs](https://img.shields.io/badge/Docs-GitBook-informational?style=for-the-badge&logo=gitbook)](https://tanishisherewith.gitbook.io/dynamic-hud)

<br/>

> *Made for Fabric only — there are no plans for Forge ports.*

DynamicHUD is a dev library for the latest minecraft version. With DynamicHUD, you can make your own HUD elements that players can move, change and interact with, making their game look and feel better.
</div>

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
Contributions are welcome! If you’re interested to do any help, please review our [contributing guidelines](https://github.com/V-Fast/DynamicHUD/blob/master/CONTRIBUTING.md).

## Quick Setup (For Players)

1. Ensure you have **[Fabric Loader](https://fabricmc.net/)**, **[Fabric API](https://modrinth.com/mod/fabric-api/)**. (**Note**: *You may also need **[YACL](https://modrinth.com/mod/yacl)** installed for older versions of DynamicHUD*).
3. Download the latest `.jar` from the **[Releases Tab](https://modrinth.com/mod/dynamichud/versions)**.
4. Drop the file into your `.minecraft/mods` directory.
5. Launch the game.

## License
DynamicHUD is released under the MIT License. Feel free to use and modify it in your mods, with proper attribution back to this repository.

## Support
Need assistance or have suggestions? Join our [Discord](https://discord.com/invite/Rqpn3C7yR5) community or submit an issue on our GitHub [repository](https://github.com/V-Fast/DynamicHUD).
