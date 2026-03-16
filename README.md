# Play Framework IntelliJ Plugin

Unofficial community-maintained fork of the obsolete JetBrains Play Framework plugin for IntelliJ IDEA.

This repository keeps the Play 1.x plugin usable on newer IntelliJ IDEA Ultimate builds.

## Status

- This is not an official JetBrains release.
- Upstream source originated from JetBrains `intellij-obsolete-plugins`.
- This fork currently targets IntelliJ IDEA Ultimate `2025.1.4.1` through `2025.3.3`.

## Downloads

- Latest downloadable plugin ZIP: see GitHub Releases for this repository.
- Historical ZIP builds: also kept in GitHub Releases.

Recommended release naming:

- Tag: `v2025.3.3`
- Release title: `Play Framework IntelliJ Plugin 2025.3.3`
- ZIP asset: `play-framework-intellij-plugin-2025.3.3.zip`

ZIP files are intentionally not committed into the Git repository. Source code stays in Git, while installable artifacts are published through GitHub Releases.

## Installation

1. Download the ZIP asset from a GitHub Release.
2. In IntelliJ IDEA, open `Settings/Preferences`.
3. Go to `Plugins`.
4. Open the gear menu and choose `Install Plugin from Disk...`.
5. Select the downloaded ZIP file.
6. Restart IntelliJ IDEA.

## Compatibility

Current validated compatibility in this fork:

- IntelliJ IDEA Ultimate `2025.1.4.1`
- IntelliJ IDEA Ultimate `2025.3.3`

See [CHANGELOG.md](/D:/work/source/LLM/Test/Test1/intellij-obsolete-plugins/play/CHANGELOG.md) for the currently recorded compatibility and fixes.

## Development

Common commands:

- Build plugin ZIP: `./gradlew buildPlugin`
- Run tests: `./gradlew test`
- Verify plugin project configuration: `./gradlew verifyPluginProjectConfiguration`

Build outputs should remain untracked. The repository ignores generated ZIP files and local build caches.

## Upstream

Original upstream repository:

- `https://github.com/JetBrains/intellij-obsolete-plugins`

This fork should not be presented as an official JetBrains-maintained plugin release.
