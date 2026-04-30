# Repository Guidelines

## Project Structure & Module Organization

This repository is a single-module Android project named `void.os`. The app module lives in `app/`, with Kotlin source under `app/src/main/java/com/outervoid/axiom/`. UI is organized by responsibility: `ui/screens/` for Compose screens, `ui/components/` for reusable composables, `ui/theme/` for colors and typography, `vm/` for view models, `data/` for models, and `icon/` for icon providers. Android resources are in `app/src/main/res/`, and manifest configuration is in `app/src/main/AndroidManifest.xml`.

## Build, Test, and Development Commands

Use the Gradle wrapper from the repository root:

- `./gradlew assembleDebug` builds a debug APK.
- `./gradlew installDebug` installs the debug APK on a connected device or emulator.
- `./gradlew test` runs local JVM unit tests when present.
- `./gradlew connectedAndroidTest` runs instrumentation tests on a connected Android target.
- `./gradlew lint` runs Android lint checks.
- `./gradlew clean` removes generated build outputs.

## Coding Style & Naming Conventions

The project uses Kotlin, Android Gradle Plugin 8.4.2, Kotlin 1.9.24, Jetpack Compose, and Java 17. Follow idiomatic Kotlin with 4-space indentation, expression-style code where it stays readable, and concise composables. Name composable functions in `PascalCase` and keep screen-level composables in `ui/screens/`; reusable UI pieces belong in `ui/components/`. Keep theme constants centralized in `ui/theme/` and prefer existing `Axiom*` color/type names over inline styling.

## Testing Guidelines

No test directories are currently committed. Add local unit tests under `app/src/test/java/...` and instrumentation or Compose UI tests under `app/src/androidTest/java/...`. Name test classes after the subject, for example `LauncherViewModelTest` or `HomeScreenTest`. Run `./gradlew test` for JVM tests and `./gradlew connectedAndroidTest` for device-backed tests before submitting UI or launcher behavior changes.

## Commit & Pull Request Guidelines

Existing history uses short, imperative commit subjects such as `Build AXIOM Launcher A1 baseline`. Keep commits focused and describe the behavior changed, not just files touched. Pull requests should include a clear summary, test results, and screenshots or screen recordings for visible UI changes. Link related issues when available and call out any device, emulator, or Android version assumptions.

## Agent-Specific Instructions

Do not edit generated Gradle outputs or local build artifacts. Keep changes scoped to the app module unless build configuration changes are required. When adding dependencies, update `app/build.gradle.kts` only with libraries needed by the implemented feature and verify the wrapper build still resolves.
