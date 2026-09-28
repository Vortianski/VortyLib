package xox.labvorty.vortylib.data.config;

import net.minecraft.network.chat.Component;

public class SocialLink {
    private final String url;
    private final Component tooltip;
    private final SpriteInfo normal;
    private final SpriteInfo hovered;

    public SocialLink(String url, Component tooltip, SpriteInfo normal, SpriteInfo hovered) {
        this.url = url;
        this.tooltip = tooltip;
        this.normal = normal;
        this.hovered = hovered;
    }

    public SocialLink(String url, Component tooltip, SpriteInfo sprite) {
        this(url, tooltip, sprite, sprite);
    }

    public String getUrl() { return url; }
    public Component getTooltip() { return tooltip; }
    public SpriteInfo getNormal() { return normal; }
    public SpriteInfo getHovered() { return hovered; }
}