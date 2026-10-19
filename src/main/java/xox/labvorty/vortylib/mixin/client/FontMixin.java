package xox.labvorty.vortylib.mixin.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import xox.labvorty.vortylib.mixin_helpers.FontAccessor;

import java.util.function.Function;

@Mixin(Font.class)
public abstract class FontMixin implements FontAccessor {
    @Shadow
    @Final
    private Function<ResourceLocation, FontSet> fonts;

    @Shadow
    @Final
    private boolean filterFishyGlyphs;

    @Override
    public Function<ResourceLocation, FontSet> getFonts() {
        return fonts;
    }

    @Override
    public boolean filterFishyGlyphs() {
        return filterFishyGlyphs;
    }

    @Invoker
    public abstract FontSet callGetFontSet(ResourceLocation id);
}
