package xox.labvorty.vortylib.gui.theme;

import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;

public record ThemeSpriteSet(
        WidgetSprites buttonSprites,
        WidgetSprites sliderTrackSprites,
        WidgetSprites sliderHandleSprites,
        ResourceLocation panelSprite
) {

    public static ThemeSpriteSet vanilla() {
        return new ThemeSpriteSet(
                new WidgetSprites(
                        ResourceLocation.withDefaultNamespace("widget/button"),
                        ResourceLocation.withDefaultNamespace("widget/button_disabled"),
                        ResourceLocation.withDefaultNamespace("widget/button_highlighted")
                ),
                new WidgetSprites(
                        ResourceLocation.withDefaultNamespace("widget/slider"),
                        ResourceLocation.withDefaultNamespace("widget/slider"),
                        ResourceLocation.withDefaultNamespace("widget/slider_highlighted")
                ),
                new WidgetSprites(
                        ResourceLocation.withDefaultNamespace("widget/slider_handle"),
                        ResourceLocation.withDefaultNamespace("widget/slider_handle"),
                        ResourceLocation.withDefaultNamespace("widget/slider_handle_highlighted")
                ),
                ResourceLocation.withDefaultNamespace("widget/text_field")
        );
    }
}