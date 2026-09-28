package xox.labvorty.vortylib.gui.theme;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public final class PanoramaTheme {
    public static final int VANILLA = 0;
    private static final Map<Integer, ResourceLocation> THEMES = new HashMap<>();
    private static int currentTheme = 0;

    static {
        THEMES.put(VANILLA, ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/title/background/panorama"));
    }

    public static void register(int themeId, ResourceLocation panorama) {
        if (themeId == VANILLA) {
            throw new IllegalArgumentException("Theme id 0 is reserved for the vanilla fallback");
        }
        THEMES.put(themeId, panorama);
    }

    public static int getCurrent() {
        return currentTheme;
    }

    public static void setCurrent(int themeId) {
        currentTheme = themeId;
    }

    public static ResourceLocation current() {
        return THEMES.getOrDefault(currentTheme, THEMES.get(VANILLA));
    }
}
