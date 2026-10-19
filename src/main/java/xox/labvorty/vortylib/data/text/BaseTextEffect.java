package xox.labvorty.vortylib.data.text;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public abstract class BaseTextEffect implements TextEffect {
    private final ResourceLocation type;
    private final EffectParams params;

    protected BaseTextEffect(ResourceLocation type, EffectParams params) {
        this.type = type;
        this.params = params;
    }

    public static TextEffect of(ResourceLocation type, EffectParams params, boolean colorful, Consumer<GlyphState> perGlyph) {
        return new BaseTextEffect(type, params) {
            @Override
            public void apply(GlyphState glyph) {
                perGlyph.accept(glyph);
            }

            @Override
            public boolean isColorful() {
                return colorful;
            }
        };
    }

    @Override
    public ResourceLocation type() {
        return type;
    }

    @Override
    public EffectParams params() {
        return params;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof TextEffect other && type.equals(other.type()) && params.equals(other.params());
    }

    @Override
    public int hashCode() {
        return 31 * type.hashCode() + params.hashCode();
    }

    @Override
    public String toString() {
        return serialize();
    }
}