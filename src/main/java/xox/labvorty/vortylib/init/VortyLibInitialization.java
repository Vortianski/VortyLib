package xox.labvorty.vortylib.init;

import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import xox.labvorty.vortylib.configs.ClientConfig;
import xox.labvorty.vortylib.data.config.ConfigHolder;
import xox.labvorty.vortylib.data.config.ModEntry;
import xox.labvorty.vortylib.data.config.ModRegistry;
import xox.labvorty.vortylib.data.config.SocialType;
import xox.labvorty.vortylib.gui.theme.PanoramaTheme;
import xox.labvorty.vortylib.gui.theme.ThemeSpriteSet;
import xox.labvorty.vortylib.gui.theme.UiTheme;

@EventBusSubscriber
public class VortyLibInitialization {
    @SubscribeEvent
    public static void onCommon(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            UiTheme.setCurrent(ClientConfig.UI_THEME.get());

            UiTheme.register(
                    1,
                    new ThemeSpriteSet(
                            new WidgetSprites(
                                    ResourceLocation.fromNamespaceAndPath("vortylib", "widget/alpha/button"),
                                    ResourceLocation.fromNamespaceAndPath("vortylib", "widget/alpha/button_disabled"),
                                    ResourceLocation.fromNamespaceAndPath("vortylib", "widget/alpha/button_highlighted")
                            ),
                            new WidgetSprites(
                                    ResourceLocation.fromNamespaceAndPath("vortylib", "widget/alpha/slider"),
                                    ResourceLocation.fromNamespaceAndPath("vortylib", "widget/alpha/slider"),
                                    ResourceLocation.fromNamespaceAndPath("vortylib", "widget/alpha/slider_highlighted")
                            ),
                            new WidgetSprites(
                                    ResourceLocation.fromNamespaceAndPath("vortylib", "widget/alpha/slider_handle"),
                                    ResourceLocation.fromNamespaceAndPath("vortylib", "widget/alpha/slider_handle"),
                                    ResourceLocation.fromNamespaceAndPath("vortylib", "widget/alpha/slider_handle_highlighted")
                            ),
                            ResourceLocation.fromNamespaceAndPath("vortylib", "widget/alpha/text_field")
                    )
            );

            UiTheme.register(
                    2,
                    new ThemeSpriteSet(
                            new WidgetSprites(
                                    ResourceLocation.fromNamespaceAndPath("vortylib", "widget/win98/button"),
                                    ResourceLocation.fromNamespaceAndPath("vortylib", "widget/win98/button_disabled"),
                                    ResourceLocation.fromNamespaceAndPath("vortylib", "widget/win98/button_highlighted")
                            ),
                            new WidgetSprites(
                                    ResourceLocation.fromNamespaceAndPath("vortylib", "widget/win98/slider"),
                                    ResourceLocation.fromNamespaceAndPath("vortylib", "widget/win98/slider"),
                                    ResourceLocation.fromNamespaceAndPath("vortylib", "widget/win98/slider_highlighted")
                            ),
                            new WidgetSprites(
                                    ResourceLocation.fromNamespaceAndPath("vortylib", "widget/win98/slider_handle"),
                                    ResourceLocation.fromNamespaceAndPath("vortylib", "widget/win98/slider_handle"),
                                    ResourceLocation.fromNamespaceAndPath("vortylib", "widget/win98/slider_handle_highlighted")
                            ),
                            ResourceLocation.fromNamespaceAndPath("vortylib", "widget/win98/text_field")
                    )
            );

            PanoramaTheme.register(
                    1,
                    ResourceLocation.fromNamespaceAndPath("vortylib", "textures/gui/panorama/alpha_blur/panorama")
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
    }
}
