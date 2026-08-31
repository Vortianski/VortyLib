package xox.labvorty.vortylib.data.config;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class SocialType {
    private static final ResourceLocation SHEET = ResourceLocation.fromNamespaceAndPath("vortylib", "textures/gui/socials.png");

    public static SocialLink modrinth(String url) {
        return new SocialLink(
                url,
                Component.literal("Modrinth"),
                new SpriteInfo(SHEET, 16, 0, 64, 64, 16, 16),
                new SpriteInfo(SHEET, 16, 16, 64, 64, 16, 16)
        );
    }

    public static SocialLink curseforge(String url) {
        return new SocialLink(
                url,
                Component.literal("CurseForge"),
                new SpriteInfo(SHEET, 0, 0, 64, 64, 16, 16),
                new SpriteInfo(SHEET, 0, 16, 64, 64, 16, 16)
        );
    }

    public static SocialLink github(String url) {
        return new SocialLink(
                url,
                Component.literal("GitHub"),
                new SpriteInfo(SHEET, 32, 0, 64, 64, 16, 16),
                new SpriteInfo(SHEET, 32, 16, 64, 64, 16, 16)
        );
    }

    public static SocialLink kofi(String url) {
        return new SocialLink(
                url,
                Component.literal("Ko-Fi"),
                new SpriteInfo(SHEET, 32, 32, 64, 64, 16, 16),
                new SpriteInfo(SHEET, 32, 48, 64, 64, 16, 16)
        );
    }

    public static SocialLink discord(String url) {
        return new SocialLink(
                url,
                Component.literal("Discord"),
                new SpriteInfo(SHEET, 48, 0, 64, 64, 16, 16),
                new SpriteInfo(SHEET, 48, 16, 64, 64, 16, 16)
        );
    }

    public static SocialLink website(String url) {
        return new SocialLink(
                url,
                Component.literal("Website"),
                new SpriteInfo(SHEET, 0, 32, 64, 64, 16, 16),
                new SpriteInfo(SHEET, 0, 48, 64, 64, 16, 16)
        );
    }

    private SocialType() {}
}