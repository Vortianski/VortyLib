package xox.labvorty.vortylib.gui.theme;

import java.util.HashMap;
import java.util.Map;

public final class UiTheme {
    public static final int VANILLA = 0;
    private static final Map<Integer, ThemeSpriteSet> THEMES = new HashMap<>();
    private static int currentTheme = 0;

    static {
        THEMES.put(VANILLA, ThemeSpriteSet.vanilla());
    }

    private UiTheme() {}

    public static void register(int themeId, ThemeSpriteSet sprites) {
        if (themeId == VANILLA) {
            throw new IllegalArgumentException("Theme id 0 is reserved for the vanilla fallback");
        }
        THEMES.put(themeId, sprites);
    }

    public static int getCurrent() {
        return currentTheme;
    }

    public static void setCurrent(int themeId) {
        currentTheme = themeId;
    }

    public static ThemeSpriteSet current() {
        return THEMES.getOrDefault(currentTheme, THEMES.get(VANILLA));
    }
}