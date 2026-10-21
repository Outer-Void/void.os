# AXIOM Release Hardening and Visual Refinement Review

## Toolchain and package discovery

The release toolchain now targets API 36 with AGP 8.10.0 and Gradle 8.11.1. The application keeps `minSdk = 26`, Java 17, Kotlin 1.9.24, and the existing Compose and AndroidX versions. The native Termux AAPT2 executable is used through the command-line override because the SDK's bundled AAPT2 is not executable on this ARM64 environment.

The broad `QUERY_ALL_PACKAGES` permission was replaced with a launcher intent query and package-specific queries for the two integrations that use package-name lookups. The app drawer depends on discovery of installed launcher activities. Validate full inventory on device and complete the Play Console declaration; policy acceptance is not established by this code review. The camera permission is used by the flashlight action and the camera feature is optional, so camera-less devices remain eligible for installation.

## Implementation notes

- The widget picker loads provider labels and sorting on IO, refreshes on package changes, and cancels its receiver and query work when stopped.
- Mapped VOID.SYS assets that still render directly are explicitly kept for resource shrinking. Duplicate bundled art falls back to deterministic category glyphs and monograms.
- The shooter retains its libGDX architecture, pools existing projectiles and particles, caps large frame deltas, and releases screen render resources when screens change. Gameplay now includes tactical ship variants, layered background detail, weapon feedback, missile targeting, and a limited bomb action.
- Home content applies safe-drawing insets under edge-to-edge rendering. The shooter registers and unregisters an Android 13+ Back callback with its activity lifecycle.

## Release readiness findings

- Entitlement management, Play Integrity, and product-signature verification remain incomplete and fail closed in release builds. Do not weaken these gates to produce a release artifact.
- No release signing configuration is defined in the app module. A production keystore and release signing setup are still required.
- Launcher and game activities remain portrait-locked for the current design. Review large screens and foldables; Android 16 may ignore orientation restrictions on large displays.
- The launcher queries installed app activities as a core product feature. Review Play policy and data-disclosure requirements before distribution.
- This report does not establish Play approval, entitlement readiness, signing readiness, or visual verification on hardware.

## Device verification checklist

- Confirm complete app-drawer discovery and icons with the chosen icon pack, including package install/update/remove.
- Review home, drawer, search, widget picker, dock, settings, and system metrics at 320dp width, large font scale, cutout, gesture navigation, and three-button navigation.
- Exercise widget bind/configure/cancel flows and confirm abandoned widget IDs are reclaimed.
- Exercise shooter campaign, survival, training, pause/back, controls, missiles, bomb, impacts, game over, return to launcher, and background/resume behavior.
- Compare screenshots on a physical phone at typical and large display sizes. No device screenshot review was available during this change.
- Validate API 26, API 33, API 34, and API 36 behavior, including status/navigation bars and Android 16 Back behavior.

## Validation performed in this environment

- `:app:assembleDebug`: passed with the previously working API 34 / AGP 8.4.2 / Gradle 8.6 toolchain and native Termux AAPT2 override.
- `:app:testDebugUnitTest`: passed; 26 tests, zero failures or errors.
- `:app:lintDebug`: passed; zero errors and 573 warnings. The warnings are mainly 485 dynamically resolved or unused bundled drawables, 71 duplicate icon assets, dependency update suggestions, and portrait-orientation recommendations. The icon resources referenced dynamically by the bundled map have explicit shrinker keep rules.
- The API 36 / AGP 8.10.0 / Gradle 8.11.1 build was attempted but Gradle stopped because the API 36 platform license is not accepted in this local SDK. API 36 has not been validated by this run.
- No Android device or usable `adb` was available, so instrumentation and visual checks were not run.
