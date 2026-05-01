# Licensing Backend Plan

## Product Model

- **AXIOM Launcher**: proprietary paid Android launcher, planned price `$4.99`.
- **VOID.SYS**: proprietary icon pack, planned standalone price `$1.99`; included/intended as AXIOM's default icon system.
- **Axiom Shooter / VOID.GAME**: proprietary protected module available only through AXIOM Launcher.

## Protection Layers

AXIOM's release security model is layered:

1. Google Play paid-app purchase/licensing.
2. Play Integrity API.
3. Server-side verification.
4. Sealed product signature file.
5. Package signing certificate verification.
6. Signed AXIOM-to-VOID.GAME launch contract.
7. Fail-closed release behavior.

## Server Responsibilities

The backend is responsible for decisions that must not be trusted to local-only state:

- Verify Google Play purchase/license tokens.
- Verify Play Integrity results.
- Issue short-lived entitlement tokens.
- Sign product signature payloads.
- Sign game launch payloads if needed.
- Revoke suspicious entitlements.
- Rotate signing keys.

## App Responsibilities

### AXIOM

- Request entitlement from the backend.
- Validate local product signatures.
- Cache only short-lived entitlement state.
- Launch VOID.GAME only with a signed payload.

### VOID.GAME

- Reject direct launch in release builds.
- Validate the AXIOM launch payload.
- Validate its own module signature.

### VOID.SYS

- Validate its product signature for protected AXIOM integration.
- Expose `icon_map.json` and drawable resources only as allowed by the product model.

## Signature File Model

The signature file is copyable, but should be useless outside its original signed context. It binds to:

- `productId`
- `packageName`
- `signingCertSha256`
- `versionCode`
- `entitlementState`
- `issuedAt`
- `expiresAt`
- `installNonce`

## Future Implementation Phases

- **Phase L1**: Play Console product setup.
- **Phase L2**: Play Integrity client integration.
- **Phase L3**: Backend verification endpoint.
- **Phase L4**: Public-key signature verification in apps.
- **Phase L5**: Release-mode enforcement.
- **Phase L6**: Abuse and revocation handling.

## Warnings

- Nothing is hack-proof.
- Private keys must never be stored in APKs or this repository.
- Do not use long-lived entitlement tokens.
- Do not trust local-only booleans for release authorization.
- Debug bypass must never exist in release builds.
