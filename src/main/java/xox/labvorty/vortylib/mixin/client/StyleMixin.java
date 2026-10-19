package xox.labvorty.vortylib.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import xox.labvorty.vortylib.data.text.StyleEffects;
import xox.labvorty.vortylib.data.text.TextEffect;
import xox.labvorty.vortylib.data.text.TextEffects;

import java.util.List;

@Mixin(Style.class)
public class StyleMixin implements StyleEffects {
    @Shadow @Final private TextColor color;
    @Shadow @Final private Boolean bold;
    @Shadow @Final private Boolean italic;
    @Shadow @Final private Boolean underlined;
    @Shadow @Final private Boolean strikethrough;
    @Shadow @Final private Boolean obfuscated;
    @Shadow @Final private ClickEvent clickEvent;
    @Shadow @Final private HoverEvent hoverEvent;
    @Shadow @Final private String insertion;
    @Shadow @Final private ResourceLocation font;

    @Unique
    private List<TextEffect> vortylib$effects = List.of();

    @Override
    public List<TextEffect> vortylib$getEffects() {
        return vortylib$effects;
    }

    @Override
    public void vortylib$setEffects(List<TextEffect> effects) {
        if ((Object) this == Style.EMPTY) {
            if (!effects.isEmpty()) {
                LoggerFactory.getLogger("VortyLib").error(
                        "Refused to attach text effects to Style.EMPTY. Stack trace shows who tried:",
                        new Throwable("attempted write to Style.EMPTY"));
            }
            return;
        }
        vortylib$effects = effects.isEmpty() ? List.of() : List.copyOf(effects);
    }

    @Override
    public Style vortylib$copyWithEffects(List<TextEffect> effects) {
        Style copy = new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough,
                this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
        ((StyleEffects) (Object) copy).vortylib$setEffects(effects);
        return copy;
    }

    @ModifyReturnValue(
            method = {
                    "withColor(Lnet/minecraft/network/chat/TextColor;)Lnet/minecraft/network/chat/Style;",
                    "withBold", "withItalic", "withUnderlined", "withStrikethrough", "withObfuscated",
                    "withClickEvent", "withHoverEvent", "withInsertion", "withFont",
                    "applyFormat", "applyLegacyFormat", "applyFormats"
            },
            at = @At("RETURN")
    )
    private Style vortylib$keepEffects(Style original) {
        Style self = (Style) (Object) this;
        if (original == self || original == Style.EMPTY || vortylib$effects.isEmpty()) return original;
        ((StyleEffects) (Object) original).vortylib$setEffects(vortylib$effects);
        return original;
    }

    @ModifyReturnValue(method = "applyTo", at = @At("RETURN"))
    private Style vortylib$mergeEffects(Style original, Style that) {
        Style self = (Style) (Object) this;
        if (original == self || original == that || original == Style.EMPTY) return original;
        List<TextEffect> merged = TextEffects.merge(StyleEffects.get(that), vortylib$effects);
        if (!merged.isEmpty()) {
            ((StyleEffects) (Object) original).vortylib$setEffects(merged);
        }
        return original;
    }

    @ModifyReturnValue(method = "equals", at = @At("RETURN"))
    private boolean vortylib$equals(boolean original, Object other) {
        return original && (!(other instanceof StyleEffects o) || vortylib$effects.equals(o.vortylib$getEffects()));
    }

    @ModifyReturnValue(method = "hashCode", at = @At("RETURN"))
    private int vortylib$hashCode(int original) {
        return vortylib$effects.isEmpty() ? original : 31 * original + vortylib$effects.hashCode();
    }
}