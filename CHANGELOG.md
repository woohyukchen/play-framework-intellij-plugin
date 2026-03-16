# Changelog

## 2025.3.3

- Kept the release version aligned with the highest validated IntelliJ IDEA Ultimate line, `2025.3.3`.
- Lowered the minimum supported IntelliJ Platform build to `251` after validation on IntelliJ IDEA Ultimate `2025.1.4.1`.
- Validated plugin compatibility across IntelliJ IDEA Ultimate `2025.1.4.1` through `2025.3.3`.
- Replaced the removed `PrattTokenType` usage in `PlayTokenType` with `IElementType`.
- Fixed a `2025.3` runtime `ClassCastException` in `PlayPersistencePackageProvider`.
- Added regression coverage for routes navigation, plain-text safety boundaries, and Play-only language substitution.
- Completed automated validation with `test`, `verifyPluginProjectConfiguration`, and `buildPlugin`, plus manual IDE verification of core Play and non-Play scenarios.
