# Play Plugin 251+ Migration Progress

## Status

- Current phase: Phase 5 completed
- Overall status: Migrated branch now builds, tests, verifies, packages, and passes manual IDE verification across the validated IntelliJ IDEA Ultimate 251+ range; remaining Phase 3/4 items are now post-migration hardening, not release blockers
- Goal: make the plugin build, load, and work on the validated IntelliJ IDEA Ultimate 251+ range

## Timeline

### 2026-03-15

- Migration execution started from the plan in `docs/PROJECT_PLAN_2025_3_3.md`.
- Progress tracking document created.
- Baseline `buildPlugin` run started on the pre-migration codebase.
- Baseline compile reached Java compilation successfully.
- Baseline packaging failed in `:instrumentCode` because `build/instrumented/instrumentCode/com/intellij/play/references/PlayControllerMethodsReferenceProvider.class` was locked by another process.
- The first observed failure is therefore currently an environment/build-artifact locking issue, not yet a proven 2025.3 API incompatibility.
- Confirmed local Gradle 9.0.0 wrapper cache exists under `C:\\Users\\Tony\\.gradle\\wrapper\\dists`.
- Confirmed local IntelliJ 2025.3/2025.3.3 Gradle cache artifacts exist under `C:\\Users\\Tony\\.gradle\\caches`.
- Upgraded the build target from IntelliJ IDEA Ultimate `2024.1` to a Java 21-era IntelliJ line.
- Tightened declared compatibility from `241-253.*` to `253-253.*` so the branch does not claim support that has not been revalidated.
- First `2025.3.3` compile run failed in source compilation, not dependency resolution.
- Primary compile break discovered: `com.intellij.lang.pratt.PrattTokenType` is no longer available, which makes `PlayTokenType` incompatible and cascades into most lexer/parser token usages.
- Replaced `PlayTokenType` to extend `IElementType` instead of the removed `PrattTokenType`.
- Updated lexer tests to resolve test data from this module layout rather than the historical plugin test path.
- Added a focused routes navigation regression test covering controller and action resolution from a `routes` file.
- Added local build-cache directories to `.gitignore` to keep migration artifacts out of source control.
- Attempted targeted test runs with the Gradle wrapper on 2026-03-15, but execution did not reach project compilation.
- Wrapper invocation against the default user home failed on `C:\\Users\\Tony\\.gradle\\wrapper\\dists\\...\\gradle-9.0.0-bin.zip.lck` with access denied.
- Wrapper invocation against a module-local `GRADLE_USER_HOME` avoided the global lock but then failed earlier in Gradle 9 startup because a temporary probe file under `.gradle-user-build-phase0\\.tmp` could not be deleted on Windows.
- A later attempt aligned `JAVA_HOME` to the local JDK 21 and got Gradle farther into project configuration, which confirms the wrapper lock/probe issue is partly environmental.
- Established a repeatable local Gradle execution path for this machine by:
  - forcing `JAVA_HOME` to `C:\\Users\\Tony\\.jdks\\openjdk-21.0.1`
  - reusing the already extracted Gradle 9 distribution under `.gradle-user-build-phase0\\wrapper\\dists\\...\\gradle-9.0.0\\bin\\gradle.bat`
  - setting `GRADLE_USER_HOME` to `C:\\Users\\Tony\\.codex\\memories\\g10`
  - setting `java.io.tmpdir` via `GRADLE_OPTS` to `C:\\Users\\Tony\\.codex\\memories\\gtmp`
  - using `--project-cache-dir C:\\Users\\Tony\\.codex\\memories\\gpc`
  - moving IntelliJ Platform local cache/artifact generation out of the project with Gradle properties in `gradle.properties`
  - allowing an external build output directory via `-PplayBuildDir=C:\\Users\\Tony\\.codex\\memories\\play-build`
- Restored Java bytecode target to 21 after `verifyPluginProjectConfiguration` reported that the target IntelliJ Platform line requires Java 21.
- Confirmed the migrated branch compiles successfully with `testClasses`.
- Confirmed focused regression tests pass:
  - `com.intellij.frameworks.play.PlayLexerTest`
  - `com.intellij.frameworks.play.PlayScriptLexerTest`
  - `com.intellij.frameworks.play.PlayRoutesNavigationTest`
- Confirmed full module tests pass with `test`.
- Confirmed plugin configuration validation passes with `verifyPluginProjectConfiguration`.
- Confirmed plugin packaging passes with `buildPlugin`.
- Hardened `PlainTextManipulator` to fail closed outside recognized Play messages files instead of unconditionally handling every `PsiPlainText`.
- Added a focused negative safety test for the plain-text manipulator to confirm non-Play plain text files are rejected.
- Added a focused language-substitutor boundary test proving HTML files under Play `views/` resolve to `PlayLanguage` while ordinary HTML files outside `views/` do not.
- Re-ran full module tests after the boundary-test additions and the suite passed.
- Manual IDE verification showed a real runtime incompatibility in `PlayPersistencePackageProvider`: `ModuleBridgeImpl` can no longer be cast to `UserDataHolderEx` on IntelliJ IDEA `2025.3.3`.
- Fixed `PlayPersistencePackageProvider` to use `Module`'s standard `getUserData` / `putUserData` API instead of casting to `UserDataHolderEx`.
- Re-ran full module tests after the persistence fix and the suite passed.
- A follow-up `buildPlugin` run after the persistence fix hit another local `instrumentCode` file-deletion problem on Windows, but that failure matches the existing environment-specific file-lock pattern rather than a new source-level incompatibility.
- Produced a fresh post-fix plugin ZIP successfully under `C:\\Users\\Tony\\.codex\\memories\\play-build-5\\distributions` for manual verification.
- Manual re-test with the post-fix ZIP confirmed that the previous `PlayPersistencePackageProvider` `ClassCastException` no longer occurs.
- Manual verification also confirmed there are no new `com.intellij.play`-related exceptions in the IDE log for the exercised scenarios.
- Performed a follow-up compatibility check against the IntelliJ IDEA Ultimate `2025.1.4.1` line (`251` branch).
- The branch built successfully against `2025.1.4.1`, passed `test`, passed `verifyPluginProjectConfiguration`, and passed `buildPlugin`.
- Updated the declared minimum supported build from `253` to `251` based on that successful validation.
- Updated the plugin release version to `2025.3.3` while keeping the validated compatibility range at `251+`, and aligned Marketplace change notes with that release positioning.
- Added [`CHANGELOG.md`](D:/work/source/LLM/Test/Test1/intellij-obsolete-plugins/play/CHANGELOG.md) to record the released compatibility, runtime fixes, and regression coverage.
- `2024.1` compatibility remains unsupported because that platform line uses Java 17 while the validated migrated branch targets Java 21-era platform APIs and build requirements.
- During one validation run, IntelliJ Platform local artifact metadata under `play-intellij-platform-cache\\localPlatformArtifacts` was transiently corrupted; a subsequent clean rebuild succeeded, which indicates a cache artifact issue rather than a remaining source incompatibility.
- Manual verification is complete for the exercised non-Play and Play scenarios.

## Phase Checklist

- [x] Phase 0: baseline and reproducibility
- [x] Phase 1: platform baseline upgrade to a Java 21-era IntelliJ line
- [x] Phase 2: API break audit and source fixes
- Confirmed source migration work required for the current branch was limited to replacing removed `PrattTokenType` usage and updating test/build wiring.
- [ ] Phase 3: extension-point scope hardening
- Migration-complete, hardening-incomplete. `PlainTextManipulator` now fails closed for non-Play plain text, and `PlayLanguageSubstitutor` now has an explicit boundary test. The remaining broad extension points can be reviewed later as post-migration hardening unless new regressions appear.
- [ ] Phase 4: regression test expansion
- Migration-complete, coverage-expansion incomplete. Focused routes, plain-text-safety, and language-substitution boundary tests are now in place; any further negative-case coverage is a follow-up quality investment rather than a migration blocker.
- [x] Phase 5: IDE-level verification
- Completed for the exercised scenarios. The previously reproduced persistence-related runtime exception is fixed, core navigation scenarios work, and no new `com.intellij.play` log exceptions were observed.

## Current Findings

- The project configuration now targets IntelliJ IDEA Ultimate `2025.1.4.1`.
- The plugin release version is now `2025.3.3`.
- The project configuration has now been validated against both IntelliJ IDEA Ultimate `2025.1.4.1` and `2025.3.3`.
- The compatibility claim can now be lowered to the 251 platform line via `sinceBuild = "251"`.
- Several extension points are broad enough to affect non-Play files and must be audited during migration.
- A baseline packaging run can reach compilation under the current setup.
- The current baseline packaging result is blocked by a file lock during `instrumentCode`.
- The project can resolve IntelliJ IDEA target-platform dependencies from local cache.
- The first confirmed 2025.3 source incompatibility is the removed `PrattTokenType` API used by `PlayTokenType`.
- The initial source-level migration after the platform bump includes one concrete compatibility fix and targeted test updates.
- The current branch now compiles, tests, validates, and packages successfully against the validated IntelliJ IDEA Ultimate `251+` range when run with the documented local Gradle workaround path.
- Remaining compiler output contains deprecation warnings in `PlayConfigurable` and `PlayConsoleCompletionContributor`, but these warnings do not block build or packaging.
- The original broad extension-point risk assessment in `plugin.xml` is still relevant and has not yet been closed by code changes in this pass.
- One concrete extension-point hardening step is now in place: the global `PsiPlainText` manipulator no longer mutates arbitrary plain-text files outside recognized Play messages contexts.
- Another boundary is now covered by automation: `PlayLanguageSubstitutor` applies to Play view HTML but not to ordinary HTML outside Play views.
- The first confirmed manual-runtime incompatibility on IntelliJ IDEA `2025.3.3` was in the persistence integration layer, not in the lexer/parser/routes migration work.
- That persistence integration incompatibility is now fixed and manually verified in the IDE.
- There is still no evidence supporting IntelliJ IDEA `2024.1` compatibility for this branch.

## Open Items

- Post-migration: review and harden the remaining broad extension points in `plugin.xml` if future IDE-side regressions appear.
- Post-migration: decide whether `PlayReferenceContributor` or `PlayScopeEnlarger` need additional fail-closed guards.
- Post-migration: decide whether to keep the local Gradle execution workaround documented as developer guidance or to further normalize the project so default wrapper execution is reliable on this Windows environment.
- Post-migration: optionally follow up on the deprecation warnings surfaced during Java compilation.
