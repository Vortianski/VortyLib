package xox.labvorty.vortylib.data.config;

import net.minecraft.resources.ResourceLocation;

public record SpriteInfo(
    ResourceLocation texture,
    int u,
    int v,
    int sheetWidth,
    int sheetHeight,
    int spriteWidth,
    int spriteHeight
) {
    public SpriteInfo(ResourceLocation texture, int u, int v, int sheetWidth, int sheetHeight) {
        this(texture, u, v, sheetWidth, sheetHeight, 16, 16);
    }

    public SpriteInfo(ResourceLocation texture) {
        this(texture, 0, 0, 256, 256, 16, 16);
    }
}