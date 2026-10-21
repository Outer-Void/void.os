# Axiom Shooter Integration

Axiom Shooter is an Easter egg reachable only from the launcher Home screen: long-press the AXIOM wordmark. A normal tap does nothing. The long-press action has an explicit accessibility label. Settings has no Shooter row or launch callback, and the Settings dock button has no long-press shortcut. The code gates the transition to Home, but swipe arbitration, TalkBack behavior, and the activation still require device checks in `docs/device-testing.md`.

The launcher shows a short AXIOM-styled transition: amber pulse, dark field, `AXIOM SHOOTER`, and `SYSTEM ONLINE.` Then it starts the bundled internal `com.outervoid.voidgame.VoidGameActivity` packaged in the AXIOM APK.

Launch extras include source package, target package, issue/expiry timestamps, nonce, entitlement state, and a launch signature field. These fields are scaffolding only; the app-side hash is not a cryptographic signature and the game activity does not verify those fields. Release access remains fail-closed until the security work in `axiom-product-completion-plan.md` is complete.

`ShooterAccessPolicyTest` checks that the hidden transition is rejected from every non-Home launcher state. It does not verify Compose gesture delivery or the visible Settings tree; those require UI/device verification.

If the internal arcade module cannot be opened, AXIOM shows `AXIOM SHOOTER UNAVAILABLE`. Normal release UX must not ask users to install Axiom Shooter separately. Exiting the game returns control to the launcher/home environment.
