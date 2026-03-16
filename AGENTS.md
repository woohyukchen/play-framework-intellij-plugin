# AGENTS.md

## Scope

- This file applies to the `play` module at `D:\work\source\LLM\Test\Test1\intellij-obsolete-plugins\play`.
- The module is an IntelliJ Platform plugin for Play Framework 1.x support.
- It is part of the larger Git repository rooted at `D:\work\source\LLM\Test\Test1\intellij-obsolete-plugins`.
- When running Git commands, prefer paths scoped to this module to avoid touching sibling plugin directories.

## Repo Signals

- No module-local `AGENTS.md` existed before this file.
- No `.cursorrules` file was found in this module.
- No `.cursor/rules/` directory was found in this module.
- No `.github/copilot-instructions.md` file was found in this module.
- Treat this file as the primary agent guidance for this module.

## Tech Stack

- Language: Java.
- Build system: Gradle Kotlin DSL.
- IntelliJ Gradle plugin: `org.jetbrains.intellij.platform` `2.12.0`.
- Target IDE: IntelliJ IDEA Ultimate `2024.1`.
- Java toolchain: 21.
- Java bytecode target: 17.
- Tests use IntelliJ test framework base classes such as `LexerTestCase`, `ParsingTestCase`, and `FormatterTestCase`.

## Important Paths

- Build file: `build.gradle.kts`
- Settings: `settings.gradle.kts`
- Gradle properties: `gradle.properties`
- Plugin descriptor: `src/main/resources/META-INF/plugin.xml`
- Main code: `src/main/java/com/intellij/play`
- Generated sources dir: `src/main/gen`
- Tests: `src/test/java/com/intellij/frameworks/play`
- Test data: `src/test/testData`

## Environment Notes

- Gradle wrapper is configured via `gradle/wrapper/gradle-wrapper.properties`.
- The wrapper currently uses Gradle `9.0.0`.
- `gradle.properties` pins `org.gradle.java.home` to a local JDK path; do not casually rewrite it.
- IntelliJ plugin builds can download large IDE artifacts, so expect the first build to be slow.

## Build Commands

- Windows wrapper: `gradlew.bat`
- Bash wrapper: `./gradlew`
- Assemble plugin classes and resources: `./gradlew assemble`
- Full module build: `./gradlew build`
- Clean outputs: `./gradlew clean`
- Build distributable plugin ZIP: `./gradlew buildPlugin`
- Prepare sandbox for local IDE runs: `./gradlew prepareSandbox`
- Run IDE with plugin installed: `./gradlew runIde`

## Verification Commands

- Run all tests: `./gradlew test`
- Run all checks: `./gradlew check`
- Validate plugin project configuration: `./gradlew verifyPluginProjectConfiguration`
- Validate plugin structure: `./gradlew verifyPluginStructure`
- Run plugin verifier against configured IDE: `./gradlew verifyPlugin`

## Single-Test Commands

- Run one test class: `./gradlew test --tests "com.intellij.frameworks.play.PlayLexerTest"`
- Run one test method: `./gradlew test --tests "com.intellij.frameworks.play.PlayLexerTest.testExpressions"`
- Another example: `./gradlew test --tests "com.intellij.frameworks.play.PlayParserTest.testTags"`
- Formatter example: `./gradlew test --tests "com.intellij.frameworks.play.PlayFormatterTest.testSimple"`
- If method filtering behaves unexpectedly, fall back to the class-level `--tests` selector.

## No Dedicated Linter

- There is no separate lint task configured in this module.
- Use `test`, `check`, and the IntelliJ plugin verification tasks as the main quality gates.
- For refactors touching plugin metadata or compatibility, run at least `verifyPluginProjectConfiguration`.
- For changes affecting descriptors or packaging, prefer `verifyPluginStructure` as well.

## Typical Agent Workflow

- Read `build.gradle.kts` and any touched source files before editing.
- Keep changes narrowly scoped to this module.
- After non-trivial Java or plugin XML edits, run targeted tests first.
- After parser, lexer, formatter, or reference changes, run the most relevant single test class.
- Before finishing a substantial change, run `./gradlew test`.
- If you touch plugin metadata or compatibility settings, also run `./gradlew verifyPluginProjectConfiguration`.

## Code Style: General

- Follow the existing Java style already present in `src/main/java` and `src/test/java`.
- Prefer minimal, targeted edits over broad cleanup.
- Preserve JetBrains-style formatting and spacing rather than introducing a new style.
- Keep files ASCII unless the file already clearly uses other characters.
- Do not add comments unless the logic is genuinely non-obvious.

## Code Style: Formatting

- Use two-space indentation in Java files.
- Keep braces and wrapping consistent with surrounding code.
- Short methods and constructors usually stay compact.
- Multi-line chained expressions and argument lists align similarly to nearby code.
- Preserve existing blank-line rhythm; do not add excessive vertical spacing.

## Code Style: Imports

- Match the local file's import style.
- This codebase mostly uses explicit imports, but some older files use wildcard imports such as `com.intellij.psi.*`.
- Do not rewrite wildcard imports to explicit ones unless you are already modifying that import block for a real reason.
- Keep static imports grouped with other imports as currently done in the file.
- Remove obviously unused imports when editing a file.

## Code Style: Types and Nullability

- Use concrete PSI and platform types when they improve clarity.
- Follow the repository's heavy use of JetBrains annotations like `@NotNull`, `@Nullable`, `@Nls`, `@NonNls`, and `@PropertyKey`.
- Annotate new public APIs and important overrides consistently with neighboring code.
- Prefer `final` locals only when it matches the surrounding style; this codebase uses them selectively, not universally.
- Maintain existing generic type specificity, especially for collections and PSI-related APIs.

## Code Style: Naming

- Class names are descriptive and use PascalCase.
- Methods use camelCase.
- Test methods use `testXxx` naming because the test framework relies on that style.
- Constants use `UPPER_SNAKE_CASE` or, in older interfaces, `String` constant fields.
- Provider, processor, contributor, and utility class names should reflect IntelliJ extension point roles.

## Code Style: Error Handling

- Prefer early null checks and guard clauses over deep nesting.
- Avoid swallowing exceptions unless the surrounding API explicitly expects it.
- When an IntelliJ API returns `null`, handle it defensively instead of assuming success.
- Favor platform-safe behavior over throwing new unchecked exceptions in plugin code.
- In tests, let framework assertions and failures surface directly.

## Code Style: IntelliJ Plugin Conventions

- Register new extension points in `src/main/resources/META-INF/plugin.xml` only when required.
- Keep implementation class names aligned with their registered extension purpose.
- Reuse existing bundle infrastructure in `PlayBundle` for user-facing strings.
- Prefer PSI-based logic over text-based heuristics when IntelliJ APIs provide structure.
- Keep compatibility in mind with the declared IDE build range in `build.gradle.kts`.

## Code Style: Tests

- Add or update targeted tests for lexer, parser, formatter, navigation, or reference behavior changes.
- Test data belongs under `src/test/testData` in the matching subdirectory.
- Keep test class package `com.intellij.frameworks.play` unless there is a clear existing reason not to.
- Follow current test patterns: `doTest(...)`, `doTest(true)`, and file-backed test data.
- Prefer extending the nearest existing test class style instead of inventing a new harness.

## Files Agents Should Treat Carefully

- `gradle.properties` because it contains a machine-local JDK path.
- `build.gradle.kts` because it controls IDE target, Java target, and plugin verification tooling.
- `src/main/resources/META-INF/plugin.xml` because small mistakes can break plugin loading.
- Generated or cached directories such as `.gradle-user-build/`, `.gradle-user/`, `.gradle/`, and `.intellijPlatform/` should not be committed as source changes.

## Practical Finish Checklist

- Confirm only intended module files changed.
- Run the narrowest relevant test command first.
- Run `./gradlew test` for substantial changes.
- Run plugin verification tasks when build metadata or compatibility changed.
- Summarize what changed, what was verified, and any follow-up risk areas.
