package xox.labvorty.vortylib.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.font.GlyphInfo;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Style;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.*;
import xox.labvorty.vortylib.data.text.GlyphState;
import xox.labvorty.vortylib.data.text.StyleEffects;
import xox.labvorty.vortylib.data.text.TextEffect;
import xox.labvorty.vortylib.data.text.TextEffects;
import xox.labvorty.vortylib.mixin_helpers.FontAccessor;

import java.util.List;

@Mixin(targets = "net.minecraft.client.gui.Font$StringRenderOutput")
public abstract class StringRenderOutputMixin {
    @Shadow float x;
    @Shadow float y;
    @Shadow @Final private Matrix4f pose;
    @Shadow @Final private boolean dropShadow;
    @Shadow @Mutable @Final private float a;
    @Shadow(remap = false) @Final Font this$0;
    @Unique private GlyphState vortylib$glyph;
    @Unique private Matrix4f vortylib$savedPose;

    @WrapMethod(method = "accept(ILnet/minecraft/network/chat/Style;I)Z")
    private boolean vortylib$accept(int index, Style style, int codePoint, Operation<Boolean> original) {
        List<TextEffect> effects = StyleEffects.get(style);
        if (effects.isEmpty()) return original.call(index, style, codePoint);

        FontAccessor access = (FontAccessor)this$0;
        GlyphInfo info = access.callGetFontSet(style.getFont()).getGlyphInfo(codePoint, access.filterFishyGlyphs());
        float advance = info.getAdvance(style.isBold());

        GlyphState glyph = vortylib$glyph;
        if (glyph == null) glyph = vortylib$glyph = new GlyphState();
        glyph.begin(TextEffects.time(), this.x, this.y, advance, index, codePoint, dropShadow);

        boolean any = false;
        for (TextEffect effect : effects) {
            if (TextEffects.shouldApply(effect)) {
                effect.apply(glyph);
                any = true;
            }
        }
        if (!any) return original.call(index, style, codePoint);

        float baseX = this.x;
        float baseY = this.y;
        float baseAlpha = this.a;
        float shiftedX = baseX + glyph.dx;
        Style drawStyle = glyph.color >= 0 ? style.withColor(glyph.color) : style;
        boolean transformed = (glyph.rotation != 0f || glyph.scale != 1f) && !Character.isWhitespace(codePoint);

        this.x = shiftedX;
        this.y = baseY + glyph.dy;
        this.a = baseAlpha * glyph.alpha;

        if (transformed) {
            float shadowOffset = dropShadow ? info.getShadowOffset() : 0f;
            float pivotX = this.x + shadowOffset + (advance - 1f) / 2f;
            float pivotY = this.y + shadowOffset + this$0.lineHeight / 2f;
            if (vortylib$savedPose == null) vortylib$savedPose = new Matrix4f();
            vortylib$savedPose.set(pose);
            pose.translate(pivotX, pivotY, 0f)
                    .rotateZ(glyph.rotation)
                    .scale(glyph.scale, glyph.scale, 1f)
                    .translate(-pivotX, -pivotY, 0f);
        }

        try {
            boolean result = original.call(index, drawStyle, codePoint);
            this.x = baseX + (this.x - shiftedX);
            return result;
        } finally {
            this.y = baseY;
            this.a = baseAlpha;
            if (transformed) pose.set(vortylib$savedPose);
        }
    }
}