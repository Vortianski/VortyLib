# VortyLib

A lightweight Minecraft library mod providing shared functionality for mods developed by **Vortianski**.

VortyLib bundles common rendering, block, item, GUI, and text utilities so downstream mods can reuse them instead of reimplementing the same functionality. It focuses on custom render types, Iris shader compatibility, and reusable building blocks.

---

## Features

### Rendering
- **Custom RenderTypes with core shaders** - build render types backed by Minecraft's core shader system.
- **Iris Shaders support for custom RenderTypes** - improves compatibility between custom render types and Iris-based shader rendering.
- **ChaosApi integration** - improves Iris compatibility further when [ChaosApi](https://www.curseforge.com/minecraft/mc-mods/chaosapi) is installed.
- **Custom Font** - renders text without the usual culling issues.
- **Layered post-processing effects** - a system for composing post-processing effects in layers.
- **Post-shader zones** - supports effects scoped to specific zones, such as an example post-shader zone.

### Blocks
- **SeatBlock** - a seat block with expandable settings.
- **ThreeAxisRotatedBlock** - supports three independent rotation properties (yaw, pitch, and roll), ideal for custom blockstate definitions.
- **Z-axis blockstate rotation** - a special blockstate definition that adds Z-axis rotation support for models.
- **ItemDisplayBlockEntity** - holds a single item stack, suited for pedestals, display cases, and similar blocks.

### Items
- **AdvancedItemOptions** - an interface for extending item behavior with:
    - Expandable tooltips that open a secondary screen after holding a key combination.
    - The ability to disable the default item cooldown overlay.
- **Inlined Creative Tab item groups** - simplifies defining and organizing item groups within a creative tab.

### GUI and Configuration
- **Simple config screen integration** - makes it easier to implement a configuration screen for a mod.
- **Config Multi-Screen** - utilities for organizing configuration options across multiple screens.
- **Mod List Screen** - a reusable mod-list screen.

### Text
- **Additional component text styles** - provides extra styles and effects for Minecraft text components.
- **Floating text** - allows rendering floating components in the world

---

## Planned Features

No additional features are currently planned.

---

## Compatibility

| Mod | Purpose |
| --- | --- |
| [Iris Shaders](https://irisshaders.dev/) | Enables shader-compatible custom render types. |
| [ChaosApi](https://www.curseforge.com/minecraft/mc-mods/chaosapi) | Improves Iris compatibility further. |

Both are optional. VortyLib works without them, but shader-related compatibility may be reduced.

---

## Contact

- **Telegram:** [@vortianskii](https://t.me/vortianskii)
- **Discord:** `@vortyyy`
