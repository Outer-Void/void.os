# Security Model

Nothing is hack-proof. AXIOM uses layered protection to raise the cost of unauthorized use while preserving a usable development workflow.

Layers:

1. Proprietary license
2. Paid entitlement foundation
3. Play Integrity scaffold
4. Product signature file gate
5. Package signature verification
6. Signed AXIOM to VOID.GAME launch contract
7. R8/ProGuard release hardening
8. Release fail-closed behavior

Debug builds may bypass entitlement and signature gates only through explicit `BuildConfig.DEBUG` checks. Release builds fail closed until real entitlement and server-side signature verification are implemented.
