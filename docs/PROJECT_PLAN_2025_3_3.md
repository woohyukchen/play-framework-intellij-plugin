# Play Plugin IntelliJ IDEA 251+ Migration Plan

## Conclusion

The existing plan in `docs/COMPATIBILITY_FIX_PLAN.md` is not sufficient as the execution plan.

It identifies one real structural issue: the plugin was compiled against IntelliJ IDEA Ultimate `2024.1` while claiming compatibility with newer platform lines. That mismatch had to be corrected first.

However, the current plan is still too speculative in three ways:

1. It presents a likely root cause as if it were already confirmed.
2. It relies too heavily on ad hoc manual installation checks and not enough on staged compatibility gates.
3. It does not separate platform migration work from plugin-behavior regression work.

Because of that, this document replaces the old one as the project plan to execute.

## Target

Primary goal:

- Make the Play Framework plugin load and function correctly in the validated IntelliJ IDEA Ultimate `251+` range.

Success criteria:

- The plugin installs without compatibility warnings caused by incorrect build targeting.
- The plugin loads without breaking Java indexing, navigation, or reference resolution.
- Core Play features still work:
  - Play template lexer
  - parser
  - formatter
  - routes navigation
  - template and controller references
- The project can be built, tested, and packaged against the validated `251+` platform line.

Non-goals for the first milestone:

- Backporting one artifact to every historical IntelliJ version.
- Large-scale refactoring unrelated to the `251+` compatibility migration.
- New Play Framework features beyond restoring compatibility.

## Current State

From the current module state:

- [`build.gradle.kts`](D:/work/source/LLM/Test/Test1/intellij-obsolete-plugins/play/build.gradle.kts) now targets `intellijIdeaUltimate("2025.1.4.1")`.
- The compatibility claim has been validated and lowered to the 251 platform line.
- [`plugin.xml`](D:/work/source/LLM/Test/Test1/intellij-obsolete-plugins/play/src/main/resources/META-INF/plugin.xml) registers several broad extension points that can affect non-Play files:
  - `lang.substitutor` for `HTML` and `JSP`
  - global `psi.referenceContributor`
  - `lang.elementManipulator` for `com.intellij.psi.PsiPlainText`
  - `codeInsight.lineMarkerProvider` for `JAVA`
  - `useScopeEnlarger`
- The migrated branch already compiles, passes `test`, passes `verifyPluginProjectConfiguration`, and packages with `buildPlugin` when run with the documented local Gradle workaround path.
- Targeted safety coverage is in place for plain-text and language-substitution boundaries, and manual IDE verification has completed successfully on the exercised scenarios.

## Planning Principles

1. Upgrade the platform baseline first.
2. Treat all suspected root causes as hypotheses until reproduced and isolated.
3. Reduce global extension-point blast radius before optimizing behavior.
4. Add regression coverage for failures that can affect the whole IDE, not only Play files.
5. Preserve scope discipline: compatibility first, cleanup later.

## Main Risks

### Risk 1: Build-target mismatch

Impact:

- The plugin may compile successfully yet fail at runtime on newer IDEs if it is built against older APIs.

Priority:

- Highest.

### Risk 2: Broad extension registrations

Impact:

- A faulty contributor, substitutor, or manipulator can interfere with unrelated files such as Java, HTML, JSP, or plain text.

Priority:

- Highest.

Relevant registrations in [`plugin.xml`](D:/work/source/LLM/Test/Test1/intellij-obsolete-plugins/play/src/main/resources/META-INF/plugin.xml):

- `lang.substitutor`
- `psi.referenceContributor`
- `lang.elementManipulator`
- `useScopeEnlarger`

### Risk 3: Groovy and mixed-language integration drift

Impact:

- This plugin depends heavily on Groovy PSI and Groovy plugin APIs. Platform or bundled-plugin changes in 2025.x may break parsing, completion, or references inside Play templates.

Priority:

- High.

### Risk 4: Insufficient regression coverage

Impact:

- A build may pass while the plugin still breaks editor behavior in the IDE.

Priority:

- High.

## Execution Plan

Execution priority has changed based on current evidence:

1. Keep Phase 3 narrow and evidence-driven.
2. Prioritize Phase 5 manual IDE verification once the highest-risk global extension points have basic guards or boundary tests.
3. Avoid broad cleanup or speculative plugin XML narrowing unless a reproduced runtime issue justifies it.
4. After Phase 5 passes, treat any remaining Phase 3/4 work as post-migration hardening rather than release-blocking migration work.

### Phase 0: Baseline and Reproducibility

Goal:

- Establish a reproducible starting point before changing compatibility code.

Tasks:

1. Record the current branch state and any local deltas affecting `play`.
2. Build the plugin as-is.
3. Attempt to load the current plugin in the intended target IDE line.
4. Capture the exact failure mode:
   - install blocked
   - plugin load failure
   - runtime exception
   - Java navigation/indexing breakage
   - Play-only feature regression
5. Save logs and stack traces for later comparison.

Exit criteria:

- We have one reproducible baseline symptom set for the current plugin on the target IDE line.

### Phase 1: Platform Baseline Upgrade

Goal:

- Align the build target with the intended runtime platform.

Tasks:

1. Update the IntelliJ target in [`build.gradle.kts`](D:/work/source/LLM/Test/Test1/intellij-obsolete-plugins/play/build.gradle.kts) from `2024.1` to a Java 21-era IDE line.
2. Re-check:
   - Java toolchain requirements
   - bundled plugin IDs
   - plugin verifier configuration
   - dependency resolution with the current Gradle IntelliJ Platform plugin
3. Keep the compatibility range honest:
   - if the code is migrated only for a newer line, narrow `sinceBuild`
   - if multi-version support is still required, plan that as a separate track
4. Run compile and packaging tasks.

Exit criteria:

- The plugin builds and packages against the chosen Java 21-era platform line.

Decision gate:

- If the plugin does not compile against the chosen target line, catalog each API break and continue with Phase 2.

### Phase 2: API Break Audit

Goal:

- Make the plugin source compatible with the chosen Java 21-era platform APIs.

Tasks:

1. Fix compile-time API changes surfaced after the platform upgrade.
2. Audit Groovy-related usages first, because this plugin depends on Groovy PSI extensively.
3. Audit file-view, parsing, and PSI creation code next:
   - `PlayLanguageSubstitutor`
   - `PlayFileViewProvider`
   - parser definition and custom element types
4. Audit reference and search integrations:
   - `PlayReferenceContributor`
   - routes references
   - controller action references
   - scope enlargers and usage handlers
5. Remove or replace APIs that are deprecated, relocated, or behaviorally changed in the 2025.x line.

Exit criteria:

- The project compiles cleanly against the validated SDK baseline.

### Phase 3: Extension-Point Scope Hardening

Goal:

- Ensure plugin integrations cannot destabilize unrelated IDE functionality.

Tasks:

1. Review each broad extension in [`plugin.xml`](D:/work/source/LLM/Test/Test1/intellij-obsolete-plugins/play/src/main/resources/META-INF/plugin.xml).
2. For each extension, answer:
   - What file types or PSI classes can it touch?
   - Can it run outside a Play project?
   - Does it fail closed or fail open?
3. Narrow registrations where possible.
4. Add defensive guards in implementations where registration narrowing is not enough.
5. Prioritize these classes:
   - `com.intellij.play.language.PlayLanguageSubstitutor`
   - `com.intellij.play.references.PlayReferenceContributor`
   - `com.intellij.play.references.PlainTextManipulator`
   - `com.intellij.play.utils.PlayScopeEnlarger`

Current execution rule:

- Only harden a broad extension point when there is either:
  - a reproduced runtime problem, or
  - a clear fail-open behavior that can be reduced safely with a targeted guard/test.

Exit criteria:

- No global extension remains broad without an explicit justification and defensive checks.

Status after current execution:

- No longer a release blocker for the validated `251+` migration once manual IDE verification passes.
- Remaining work in this phase should be treated as post-migration hardening unless a new reproduced IDE-wide regression appears.

### Phase 4: Regression Test Expansion

Goal:

- Cover the failure modes most likely to recur during migration.

Tasks:

1. Keep existing lexer/parser/formatter/routes tests green.
2. Add targeted tests for:
   - language substitution in Play view paths
   - references inside Play templates
   - references in routes files
   - negative cases proving non-Play Java/HTML/plain-text files are not affected
3. If an IntelliJ test fixture is needed for project-level behavior, add focused fixture-based tests rather than relying only on parser-style tests.
4. Add at least one regression test for the original newer-platform failure once reproduced.

Current execution rule:

- Prefer a small number of high-signal boundary tests over broad new test surface.
- The next most valuable tests are those that prove global extensions do not affect non-Play files.

Exit criteria:

- Automated tests cover both Play functionality and IDE-safety boundaries.

Status after current execution:

- No longer a release blocker for the validated `251+` migration once core automated checks and manual IDE verification pass.
- Remaining work in this phase should focus on incremental boundary coverage, not on delaying delivery of the migrated plugin.

### Phase 5: IDE-Level Verification

Goal:

- Validate that the migrated plugin works in the real target IDE.

Tasks:

1. Run module tests.
2. Run plugin verification tasks relevant to packaging and platform compatibility.
3. Install the built plugin into a validated target IDE.
4. Validate these scenarios manually:
   - open a normal Java project without Play and confirm navigation still works
   - open a Play 1.x project and confirm template parsing and references work
   - inspect logs for plugin exceptions during indexing and editor opening

Exit criteria:

- No installation failure, no startup exceptions that block plugin use, and no regression in Java navigation.

Current priority:

- This is now the highest-value remaining phase after minimal Phase 3 hardening.
- Do not postpone manual IDE verification waiting for a “perfect” extension-point audit.

Status after current execution:

- This phase is the migration-completion gate.
- This phase has passed for the exercised scenarios.
- Unresolved Phase 3/4 items have moved to a follow-up hardening track.

## Recommended Task Breakdown

Workstream A: Build and packaging

- Upgrade SDK target
- align build range
- restore compile/package/verifier green

Workstream B: Compatibility fixes

- fix API breaks
- adapt Groovy and PSI integration code

Workstream C: Safety hardening

- reduce extension-point blast radius
- add guards for non-Play contexts

Workstream D: Validation

- add regression tests
- run automated checks
- run manual IDE verification

## Suggested Milestones

Milestone 1: Buildable on 251+

- Java 21-era SDK target configured
- code compiles
- plugin packages successfully

Milestone 2: Safe to load

- plugin installs in the validated `251+` IDE range
- no IDE-wide breakage
- broad extension points audited

Milestone 3: Feature recovery

- core Play features work again
- regression tests cover reproduced failures

Milestone 4: Release candidate

- tests and verification complete
- compatibility range finalized
- changelog and release packaging ready

## Decision Log Template

Use this during execution:

- Decision: what changed
- Reason: why it was necessary
- Evidence: compile error, test failure, IDE log, or verifier result
- Impact: feature, compatibility, or risk reduction

## Open Questions

These do not block planning, but they must be answered during execution:

1. Does the current plugin fail immediately on install in the target IDE, or only after project indexing/editor activity?
2. Is the Java navigation failure caused by one global extension point, or by multiple overlapping registrations?
3. Does Groovy integration require additional plugin or API migration beyond simple recompilation?
4. Is backward compatibility with `2024.1` still required, or can this branch remain a `251+` compatibility branch?

## Recommended Final Direction

Recommended strategy:

1. Keep this branch on the validated `251+` compatibility range.
2. Treat any remaining extension-point tightening as post-migration hardening, not as a blocker for the completed migration.
3. Preserve the validated Java 21-era build target unless a separate backward-compatibility track is opened.
4. Expand support claims further only if an older platform line is built and verified explicitly.

This document is now retained as the execution record for the completed migration and the follow-up hardening track.
