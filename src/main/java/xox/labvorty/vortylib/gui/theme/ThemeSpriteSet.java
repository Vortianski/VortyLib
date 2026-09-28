package xox.labvorty.vortylib.gui.theme;

import net.minecraft.resources.ResourceLocation;
import xox.labvorty.vortylib.gui.widget.backported.Border;
import xox.labvorty.vortylib.gui.widget.backported.NineSliceSprite;
import xox.labvorty.vortylib.gui.widget.backported.WidgetSprite;

public record ThemeSpriteSet(
        WidgetSprite buttonSprites,
        WidgetSprite sliderTrackSprites,
        WidgetSprite sliderHandleSprites,
        WidgetSprite panelSprites
) {
    public static ThemeSpriteSet vanilla() {
        WidgetSprite button = new WidgetSprite(
                new NineSliceSprite(
                        ResourceLocation.parse("vortylib:textures/gui/sprites/widget/vanilla/widgets.png"),
                        0,
                        20,
                        200,
                        20,
                        200,
                        20,
                        new Border.Single(4)
                ),
                new NineSliceSprite(
                        ResourceLocation.parse("vortylib:textures/gui/sprites/widget/vanilla/widgets.png"),
                        0,
                        0,
                        200,
                        20,
                        200,
                        20,
                        new Border.Single(4)
                ),
                new NineSliceSprite(
                        ResourceLocation.parse("vortylib:textures/gui/sprites/widget/vanilla/widgets.png"),
                        0,
                        40,
                        200,
                        20,
                        200,
                        20,
                        new Border.Single(4)
                ),
                new NineSliceSprite(
                        ResourceLocation.parse("vortylib:textures/gui/sprites/widget/vanilla/widgets.png"),
                        0,
                        40,
                        200,
                        20,
                        200,
                        20,
                        new Border.Single(4)
                )
        );
        WidgetSprite slider = new WidgetSprite(
                new NineSliceSprite(
                        ResourceLocation.parse("vortylib:textures/gui/sprites/widget/vanilla/widgets.png"),
                        0,
                        60,
                        200,
                        20,
                        200,
                        20,
                        new Border.Single(1)
                ),
                new NineSliceSprite(
                        ResourceLocation.parse("vortylib:textures/gui/sprites/widget/vanilla/widgets.png"),
                        0,
                        60,
                        200,
                        20,
                        200,
                        20,
                        new Border.Single(1)
                ),
                new NineSliceSprite(
                        ResourceLocation.parse("vortylib:textures/gui/sprites/widget/vanilla/widgets.png"),
                        0,
                        80,
                        200,
                        20,
                        200,
                        20,
                        new Border.Single(1)
                ),
                new NineSliceSprite(
                        ResourceLocation.parse("vortylib:textures/gui/sprites/widget/vanilla/widgets.png"),
                        0,
                        80,
                        200,
                        20,
                        200,
                        20,
                        new Border.Single(1)
                )
        );
        WidgetSprite sliderHandle = new WidgetSprite(
                new NineSliceSprite(
                        ResourceLocation.parse("vortylib:textures/gui/sprites/widget/vanilla/widgets.png"),
                        200,
                        60,
                        8,
                        20,
                        8,
                        20,
                        new Border.All(
                                2,
                                2,
                                2,
                                3
                        )
                ),
                new NineSliceSprite(
                        ResourceLocation.parse("vortylib:textures/gui/sprites/widget/vanilla/widgets.png"),
                        200,
                        60,
                        8,
                        20,
                        8,
                        20,
                        new Border.All(
                                2,
                                2,
                                2,
                                3
                        )
                ),
                new NineSliceSprite(
                        ResourceLocation.parse("vortylib:textures/gui/sprites/widget/vanilla/widgets.png"),
                        200,
                        80,
                        8,
                        20,
                        8,
                        20,
                        new Border.All(
                                2,
                                2,
                                2,
                                3
                        )
                ),
                new NineSliceSprite(
                        ResourceLocation.parse("vortylib:textures/gui/sprites/widget/vanilla/widgets.png"),
                        200,
                        80,
                        8,
                        20,
                        8,
                        20,
                        new Border.All(
                                2,
                                2,
                                2,
                                3
                        )
                )
        );
        WidgetSprite panel = new WidgetSprite(
                new NineSliceSprite(
                        ResourceLocation.parse("vortylib:textures/gui/sprites/widget/alpha/widgets.png"),
                        0,
                        100,
                        200,
                        20,
                        200,
                        20,
                        new Border.Single(1)
                ),
                new NineSliceSprite(
                        ResourceLocation.parse("vortylib:textures/gui/sprites/widget/alpha/widgets.png"),
                        0,
                        100,
                        200,
                        20,
                        200,
                        20,
                        new Border.Single(1)
                ),
                new NineSliceSprite(
                        ResourceLocation.parse("vortylib:textures/gui/sprites/widget/alpha/widgets.png"),
                        0,
                        100,
                        200,
                        20,
                        200,
                        20,
                        new Border.Single(1)
                ),
                new NineSliceSprite(
                        ResourceLocation.parse("vortylib:textures/gui/sprites/widget/alpha/widgets.png"),
                        0,
                        100,
                        200,
                        20,
                        200,
                        20,
                        new Border.Single(1)
                )
        );

        return new ThemeSpriteSet(button, slider, sliderHandle, panel);
    }
}