package xox.labvorty.vortylib.data.text;

import net.minecraft.network.chat.Style;

import java.util.List;

public interface StyleEffects {
    List<TextEffect> vortylib$getEffects();
    void vortylib$setEffects(List<TextEffect> effects);
    Style vortylib$copyWithEffects(List<TextEffect> effects);
    static List<TextEffect> get(Style style) {
        return ((StyleEffects) (Object) style).vortylib$getEffects();
    }
    static Style withEffects(Style style, List<TextEffect> effects) {
        if (get(style).equals(effects)) return style;
        return ((StyleEffects) (Object) style).vortylib$copyWithEffects(effects);
    }
}