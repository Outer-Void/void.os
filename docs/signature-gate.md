# Signature Gate

AXIOM uses a runtime product signature file as an additional protection layer. It is not a replacement for platform entitlement, Play Integrity, or package signature verification.

Runtime path: `filesDir/signatures/axiom.sig`.

The signature binds to product id, package name, signing certificate SHA-256, version code, entitlement state, issue/expiry timestamps, and install nonce. Missing, malformed, expired, or mismatched signatures lock protected functionality in release builds.

Signature files cannot be made uncopyable. The goal is that a copied file becomes useless outside the matching signed package and install context.

Private signing keys must remain server-side only. The APK may contain public verification material in a future implementation. Current release behavior fails closed until server signing is connected.
