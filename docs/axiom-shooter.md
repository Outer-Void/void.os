# Axiom Shooter Integration

Axiom Shooter is launched by long-pressing the AXIOM wordmark/name in AXIOM Launcher.

The launcher shows a short AXIOM-styled transition: amber pulse, dark field, `AXIOM SHOOTER`, and `SYSTEM ONLINE.` Then it starts the bundled internal `com.outervoid.voidgame.VoidGameActivity` packaged in the AXIOM APK.

Launch extras include source package, target package, issue/expiry timestamps, nonce, entitlement state, and a launch signature field. This is a protected launch contract foundation; full cryptographic verification is completed in the module/server layer later.

If the internal arcade module cannot be opened, AXIOM shows `AXIOM SHOOTER UNAVAILABLE`. Normal release UX must not ask users to install Axiom Shooter separately. Exiting the game returns control to the launcher/home environment.
