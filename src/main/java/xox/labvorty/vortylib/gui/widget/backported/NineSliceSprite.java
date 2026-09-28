package xox.labvorty.vortylib.gui.widget.backported;

import net.minecraft.resources.ResourceLocation;

public record NineSliceSprite(
        ResourceLocation resourceLocation,
        int u,
        int v,
        int uvWidth,
        int uvHeight,
        int width,
        int height,
        Border border
) {}