# kʊɹs'd

A fabric mod where loot chests spawn procedurally generated bekʊɹs'd items. Cursed in their own wɛɪj.

bekʊɹs'd items (also called artifacts, used interchangeably) are modified loot that change gameplay mechanics in seemingly unpredictable ways.

# Concept

kʊɹs'd replaces "normal loot progression" with artifacts that:
- modify player behavior directly
- combine multiple hidden effects
- scale with intensity
- introduce tradeoffs instead of clean upgrades

Every item is effectively a small rule change to the game.

# Users

To download kʊɹs'd:
- Modrinth:
  - https://modrinth.com/project/kursd
- Github Releases:
  - https://github.com/pero-sk/kursd/releases

# Design intent

Artifacts are meant to feel like:

- discovered anomalies
- unstable equipment
- contradictory upgrades

Not:

- traditional enchanted items
- RPG stat modifiers
- predictable progression tools

# For Developers

Internally, artifacts are composed of:

- trait modules (hidden behavior logic)
- intensity stat
- context-based mutation systems

All effects are resolved through centralized per-tick processing to ensure consistent stacking and avoid order-dependent bugs.