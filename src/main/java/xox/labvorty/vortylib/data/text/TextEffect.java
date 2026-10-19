package xox.labvorty.vortylib.data.text;

import net.minecraft.resources.ResourceLocation;

public interface TextEffect {
    ResourceLocation type();
    EffectParams params();
    void apply(GlyphState glyph);
    default boolean isColorful() {
        return false;
    }
    default String serialize() {
        ResourceLocation type = type();
        String name = TextEffects.NAMESPACE.equals(type.getNamespace()) ? type.getPath() : type.toString();
        return name + params().serialize();
    }
}