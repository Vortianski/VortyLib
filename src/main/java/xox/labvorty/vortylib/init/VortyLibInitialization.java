package xox.labvorty.vortylib.init;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.joml.Vector3f;
import xox.labvorty.vortylib.configs.ClientConfig;
import xox.labvorty.vortylib.data.config.ConfigHolder;
import xox.labvorty.vortylib.data.config.ModEntry;
import xox.labvorty.vortylib.data.config.ModRegistry;
import xox.labvorty.vortylib.data.config.SocialType;
import xox.labvorty.vortylib.gui.theme.PanoramaTheme;
import xox.labvorty.vortylib.gui.theme.ThemeSpriteSet;
import xox.labvorty.vortylib.gui.theme.UiTheme;
import xox.labvorty.vortylib.gui.widget.backported.Border;
import xox.labvorty.vortylib.gui.widget.backported.NineSliceSprite;
import xox.labvorty.vortylib.gui.widget.backported.WidgetSprite;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class VortyLibInitialization {
    @SubscribeEvent
    public static void onCommon(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            UiTheme.setCurrent(ClientConfig.UI_THEME.get());

            PanoramaTheme.register(
                    1,
                    ResourceLocation.fromNamespaceAndPath("vortylib", "textures/gui/panorama/alpha_blur/panorama")
            );

            //alpha
            UiTheme.register(
                    1,
                    new ThemeSpriteSet(
                            new WidgetSprite(
                                    new NineSliceSprite(
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/alpha/widgets.png"),
                                            0,
                                            20,
                                            200,
                                            20,
                                            200,
                                            20,
                                            new Border.Single(3)
                                    ),
                                    new NineSliceSprite(
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/alpha/widgets.png"),
                                            0,
                                            0,
                                            200,
                                            20,
                                            200,
                                            20,
                                            new Border.Single(1)
                                    ),
                                    new NineSliceSprite(
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/alpha/widgets.png"),
                                            0,
                                            40,
                                            200,
                                            20,
                                            200,
                                            20,
                                            new Border.Single(3)
                                    ),
                                    new NineSliceSprite(
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/alpha/widgets.png"),
                                            0,
                                            40,
                                            200,
                                            20,
                                            200,
                                            20,
                                            new Border.Single(3)
                                    )
                            ),
                            new WidgetSprite(
                                    new NineSliceSprite(
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/alpha/widgets.png"),
                                            0,
                                            60,
                                            200,
                                            20,
                                            200,
                                            20,
                                            new Border.Single(1)
                                    ),
                                    new NineSliceSprite(
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/alpha/widgets.png"),
                                            0,
                                            60,
                                            200,
                                            20,
                                            200,
                                            20,
                                            new Border.Single(1)
                                    ),
                                    new NineSliceSprite(
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/alpha/widgets.png"),
                                            0,
                                            80,
                                            200,
                                            20,
                                            200,
                                            20,
                                            new Border.Single(1)
                                    ),
                                    new NineSliceSprite(
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/alpha/widgets.png"),
                                            0,
                                            80,
                                            200,
                                            20,
                                            200,
                                            20,
                                            new Border.Single(1)
                                    )
                            ),
                            new WidgetSprite(
                                    new NineSliceSprite(
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/alpha/widgets.png"),
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
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/alpha/widgets.png"),
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
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/alpha/widgets.png"),
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
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/alpha/widgets.png"),
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
                            ),
                            new WidgetSprite(
                                    new NineSliceSprite(
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/alpha/widgets.png"),
                                            0,
                                            100,
                                            200,
                                            20,
                                            200,
                                            20,
                                            new Border.Single(2)
                                    ),
                                    new NineSliceSprite(
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/alpha/widgets.png"),
                                            0,
                                            100,
                                            200,
                                            20,
                                            200,
                                            20,
                                            new Border.Single(2)
                                    ),
                                    new NineSliceSprite(
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/alpha/widgets.png"),
                                            0,
                                            100,
                                            200,
                                            20,
                                            200,
                                            20,
                                            new Border.Single(2)
                                    ),
                                    new NineSliceSprite(
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/alpha/widgets.png"),
                                            0,
                                            100,
                                            200,
                                            20,
                                            200,
                                            20,
                                            new Border.Single(2)
                                    )
                            )
                    )
            );

            //win98
            UiTheme.register(
                    2,
                    new ThemeSpriteSet(
                            new WidgetSprite(
                                    new NineSliceSprite(
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/win98/widgets.png"),
                                            0,
                                            20,
                                            200,
                                            20,
                                            200,
                                            20,
                                            new Border.Single(3)
                                    ),
                                    new NineSliceSprite(
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/win98/widgets.png"),
                                            0,
                                            0,
                                            200,
                                            20,
                                            200,
                                            20,
                                            new Border.Single(1)
                                    ),
                                    new NineSliceSprite(
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/win98/widgets.png"),
                                            0,
                                            40,
                                            200,
                                            20,
                                            200,
                                            20,
                                            new Border.Single(3)
                                    ),
                                    new NineSliceSprite(
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/win98/widgets.png"),
                                            0,
                                            40,
                                            200,
                                            20,
                                            200,
                                            20,
                                            new Border.Single(3)
                                    )
                            ),
                            new WidgetSprite(
                                    new NineSliceSprite(
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/win98/widgets.png"),
                                            0,
                                            60,
                                            200,
                                            20,
                                            200,
                                            20,
                                            new Border.Single(1)
                                    ),
                                    new NineSliceSprite(
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/win98/widgets.png"),
                                            0,
                                            60,
                                            200,
                                            20,
                                            200,
                                            20,
                                            new Border.Single(1)
                                    ),
                                    new NineSliceSprite(
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/win98/widgets.png"),
                                            0,
                                            80,
                                            200,
                                            20,
                                            200,
                                            20,
                                            new Border.Single(1)
                                    ),
                                    new NineSliceSprite(
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/win98/widgets.png"),
                                            0,
                                            80,
                                            200,
                                            20,
                                            200,
                                            20,
                                            new Border.Single(1)
                                    )
                            ),
                            new WidgetSprite(
                                    new NineSliceSprite(
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/win98/widgets.png"),
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
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/win98/widgets.png"),
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
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/win98/widgets.png"),
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
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/win98/widgets.png"),
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
                            ),
                            new WidgetSprite(
                                    new NineSliceSprite(
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/win98/widgets.png"),
                                            0,
                                            100,
                                            200,
                                            20,
                                            200,
                                            20,
                                            new Border.Single(2)
                                    ),
                                    new NineSliceSprite(
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/win98/widgets.png"),
                                            0,
                                            100,
                                            200,
                                            20,
                                            200,
                                            20,
                                            new Border.Single(2)
                                    ),
                                    new NineSliceSprite(
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/win98/widgets.png"),
                                            0,
                                            100,
                                            200,
                                            20,
                                            200,
                                            20,
                                            new Border.Single(2)
                                    ),
                                    new NineSliceSprite(
                                            ResourceLocation.parse("vortylib:textures/gui/sprites/widget/win98/widgets.png"),
                                            0,
                                            100,
                                            200,
                                            20,
                                            200,
                                            20,
                                            new Border.Single(2)
                                    )
                            )
                    )
            );

            ModRegistry.register(
                    ModEntry.builder("vortylib", Component.literal("VortyLib"))
                            .banner(ResourceLocation.fromNamespaceAndPath("vortylib", "textures/gui/vortylib.png"), 115, 64)
                            .clientConfig(ConfigHolder.builder(ClientConfig.SPEC)
                                    .addBoolean(Component.literal("ChaosLib Warning"), Component.literal("Show a warning if ChaosLib is not loaded"), ClientConfig.CHAOSLIB_WARNING, true)
                                    .addInt(Component.literal("UI Theme"), Component.literal("Determines the theme of buttons in config screens"), ClientConfig.UI_THEME, 0, 0, 2)
                                    .addInt(Component.literal("Panorama Theme"), Component.literal("Determines the theme of panorama in config screens"), ClientConfig.PANORAMA_THEME, 0, 0, 2)
                                    .addBoolean(Component.literal("Menu Config Button"), Component.literal("Whether config button is rendered in main menu"), ClientConfig.MENU_BUTTON, true)
                                    .addBoolean(Component.literal("Pause Config Button"), Component.literal("Whether config button is rendered in pause menu"), ClientConfig.PAUSE_BUTTON, true)
                                    .build()
                            )
                            .addSocial(SocialType.modrinth("https://modrinth.com/mod/vortylib"))
                            .addSocial(SocialType.curseforge("https://www.curseforge.com/minecraft/mc-mods/vortylib"))
                            .addSocial(SocialType.github("https://github.com/Vortianski/VortyLib"))
                            .addSocial(SocialType.discord("https://discord.gg/ZesGqhGnAN"))
                            .addSocial(SocialType.kofi("https://ko-fi.com/vortianski"))
                            .build()
            );
        });

        for (int r = 0; r < 4; r++) {
            for (int g = 0; g < 4; g++) {
                for (int b = 0; b < 4; b++) {
                    Vector3f color = new Vector3f(
                            r / 3f,
                            g / 3f,
                            b / 3f
                    );

                    VortyLibRenderTypes.registerEntityColoredGlint(color);
                }
            }
        }
    }
}
