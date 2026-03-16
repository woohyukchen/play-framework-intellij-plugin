# GitHub Release Template

Use this template when publishing a new GitHub Release with the plugin ZIP attached.

## Versioning

Recommended convention:

- Tag: `v2025.3.3`
- Release title: `Play Framework IntelliJ Plugin 2025.3.3`
- ZIP asset: `play-framework-intellij-plugin-2025.3.3.zip`

If you need another build on the same compatibility line:

- Tag: `v2025.3.3-1`
- Release title: `Play Framework IntelliJ Plugin 2025.3.3-1`
- ZIP asset: `play-framework-intellij-plugin-2025.3.3-1.zip`

## Release Body Template

```markdown
Unofficial community-maintained fork of the obsolete JetBrains Play Framework plugin.

Compatibility

- Tested with IntelliJ IDEA Ultimate 2025.1.4.1 through 2025.3.3

Highlights

- Replaced removed platform APIs needed for newer IDEA builds
- Fixed Play persistence integration runtime issues on newer IDE versions
- Added regression coverage for routes navigation and non-Play file safety

Installation

1. Download the attached ZIP
2. In IntelliJ IDEA, open Settings/Preferences
3. Go to Plugins
4. Choose Install Plugin from Disk...
5. Select the ZIP and restart the IDE

Notes

- This is not an official JetBrains release
- Source repository: https://github.com/woohyukchen/play-framework-intellij-plugin
- Upstream origin: https://github.com/JetBrains/intellij-obsolete-plugins
```

## Release Checklist

- Update `CHANGELOG.md`
- Build the plugin ZIP with `./gradlew buildPlugin`
- Confirm the ZIP filename matches the release version
- Push source changes first
- Create the Git tag
- Create the GitHub Release
- Upload the ZIP as a release asset
- Keep older Releases so historical ZIP versions remain available
