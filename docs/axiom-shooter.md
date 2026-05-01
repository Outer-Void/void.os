# Axiom Shooter Integration

Axiom Shooter is launched by long-pressing the AXIOM wordmark/name in AXIOM Launcher.

The launcher shows a short AXIOM-styled transition: amber pulse, dark field, `AXIOM SHOOTER`, and `SYSTEM ONLINE.` Then it starts `com.outervoid.voidgame.VoidGameActivity` explicitly.

Launch extras include source package, target package, issue/expiry timestamps, nonce, entitlement state, and a launch signature field. This is a protected launch contract foundation; full cryptographic verification is completed in the module/server layer later.

If VOID.GAME is not installed, AXIOM shows an AXIOM-styled dialog instead of silently failing. Exiting the game returns control to the launcher/home environment.
