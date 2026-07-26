# VortyLib

A lightweight Minecraft library mod providing shared functionality for mods developed by **Vortianski**.

VortyLib bundles common rendering, block, and item utilities so downstream mods don't have to reimplement them individually with a particular focus on custom render types, Iris Shaders compatibility, and reusable block/item building blocks.

---

## ✨ Features

### Rendering
- **Custom RenderTypes with core shaders** - build render types backed by Minecraft's core shader system.
- **Iris Shaders support for custom render types** - custom render types remain compatible when Iris is installed.
- **Better Iris Shaders compatibility via ChaosApi** - smoother interop between VortyLib's render types and shader packs when [ChaosApi](https://www.curseforge.com/minecraft/mc-mods/chaosapi) is present.
- **Custom Font class** - renders text without culling issues.

### Blocks
- **SeatBlock** - a seat block with expandable settings.
- **ThreeAxisRotatedBlock** - supports three independent rotation properties (yaw, pitch, roll), ideal for custom blockstate definitions.
- **Z-axis blockstate rotation** - a special blockstate definition that adds Z-axis rotation support for models.
- **ItemDisplayBlockEntity** - a block entity for holding a single item stack, suited for pedestals, display cases, and similar blocks.

### Items
- **AdvancedItemOptions** interface which extends item behavior with:
    - Expandable tooltips that open a secondary screen after holding a key combination.
    - Disabling the default cooldown overlay rendering.

### Misc
- **Custom CreativeModeTab** - simplified creative tab with item group setup.

---

## 🚧 Planned Features
- Extended `GuiGraphics` with additional rendering utilities.

---

## 📦 Compatibility
| Mod                                                               | Purpose                                       |
|-------------------------------------------------------------------|-----------------------------------------------|
| [Iris Shaders](https://irisshaders.dev/)                          | Enables shader-compatible custom render types |
| [ChaosApi](https://www.curseforge.com/minecraft/mc-mods/chaosapi) | Improves Iris compatibility further           |

*(Both are optional - VortyLib functions without them, with reduced shader-related compatibility.)*

---

## 📬 Contact
- **Telegram:** [@vortianskii](https://t.me/vortianskii)
- **Discord:** @vortyyy