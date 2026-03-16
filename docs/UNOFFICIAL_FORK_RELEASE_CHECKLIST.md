# Unofficial Fork Release Checklist

This document records what should be reviewed before publishing this Play plugin fork from a personal GitHub account or JetBrains Marketplace profile.

It is intentionally a checklist only. It does not change current build or plugin metadata yet.

## Goal

Publish a clearly unofficial fork of the obsolete JetBrains Play plugin, with clear attribution and without implying JetBrains is the current maintainer.

## Current State Observed

Observed from the current module configuration:

- `build.gradle.kts`
  - `group = "com.intellij"`
  - `version = "2025.3.3"`
  - target IDE is `IntelliJ IDEA Ultimate 2025.1.4.1`
  - minimum supported build is `251`
- `src/main/resources/META-INF/plugin.xml`
  - plugin id is `com.intellij.play`
  - plugin name is `Play Framework`
  - vendor is `JetBrains`
  - description does not state that this is an unofficial fork
- `CHANGELOG.md`
  - contains compatibility work already done for newer IDE builds

## Release Decision

Choose one release path before editing metadata:

- Path A: source-only fork on GitHub
  - lowest risk
  - you can publish source and optionally attach built ZIPs in GitHub Releases
- Path B: standalone plugin release for end users
  - requires clearer identity changes
  - recommended if you want others to install your maintained build
- Path C: submit a pull request back to JetBrains
  - optional
  - does not replace Path A or B
  - acceptance and official release are not guaranteed

## Required Before Public GitHub Release

- Keep the upstream license text with the published source tree.
  - The upstream repository is Apache 2.0 licensed.
  - If the root `LICENSE` file is not present in your local export, copy it into the published repository before release.
- Keep attribution to the upstream JetBrains repository.
- State clearly in the repository README that this is an unofficial fork.
- State which IntelliJ IDEA versions you tested.
- State what functional changes you made versus upstream.
- Do not commit build caches or local IDE directories.

## Required Before Public Binary Distribution

- Review plugin identity so users are not misled about who publishes it.
- Decide whether to keep the current plugin id.
  - Keeping `com.intellij.play` helps existing settings and may act like an update path in manual installs.
  - Changing the id is safer from a branding and ownership perspective, but users may need to remove the old plugin first.
- Change vendor away from `JetBrains`.
- Adjust plugin name to make unofficial status obvious.
  - Example pattern: `Play Framework (Unofficial)` or `Play Framework 1.x Support (Unofficial)`
- Add an unofficial-maintainer note in the plugin description.
- Add your own release notes for each published build.

## Required Before JetBrains Marketplace Publication

- Use your own Marketplace vendor profile.
- Do not publish with `vendor` set to `JetBrains`.
- Strongly consider using a distinct plugin id instead of `com.intellij.play`.
- Use a plugin name that does not imply official JetBrains maintenance.
- Review any logo, screenshots, and release text so they do not imply JetBrains endorsement.
- Verify the plugin package passes Marketplace validation with the updated metadata.

## Recommended Metadata Changes

These are the likely edits to make after you review this checklist.

### `src/main/resources/META-INF/plugin.xml`

- Review `<id>com.intellij.play</id>`
  - decide whether to keep or rename
- Change `<vendor>JetBrains</vendor>`
  - replace with your own name or handle
- Review `<name>Play Framework</name>`
  - consider adding an unofficial marker
- Update `<description>`
  - mention this is a community-maintained fork for modern IntelliJ IDEA builds

### `build.gradle.kts`

- Review `group = "com.intellij"`
  - consider replacing with your own reverse-domain namespace if you want a clean fork identity
- Keep `version` aligned to your actual release strategy
  - current version mirrors validated IDE line rather than upstream historical plugin versions
- Review change notes text
  - it currently describes compatibility work, which is fine
  - consider adding one line that the release is maintained independently from JetBrains

### Repository Files

- Add or update `README.md`
  - explain unofficial fork status
  - explain install steps
  - explain tested IDE versions
  - link to upstream repository
- Ensure `LICENSE` is present in the published repository
- Optionally add `NOTICE` or a short attribution section in README

## Open Decisions For Review

- Keep plugin id `com.intellij.play`, or rename it?
- Keep plugin name `Play Framework`, or append an unofficial marker?
- Keep release version format as `2025.3.3`, or move to your own scheme such as `1.0.0`, `2026.1`, or `2025.3.3-fork.1`?
- Publish only on GitHub, or also on JetBrains Marketplace?
- Publish only source, or also publish built ZIP artifacts?

## Suggested Safe Default

If your main goal is to help other users without creating branding ambiguity:

- Publish source on your own GitHub
- Include upstream license and attribution
- Add a README that clearly says unofficial fork
- Publish ZIP artifacts in GitHub Releases
- Change vendor to your own name
- Change plugin name to include `(Unofficial)`
- Decide on plugin id after checking whether you want compatibility with manual replacement installs

## Files To Review Later

- `build.gradle.kts`
- `src/main/resources/META-INF/plugin.xml`
- `CHANGELOG.md`
- `README.md` if added
- `LICENSE` if copied into the published repository
