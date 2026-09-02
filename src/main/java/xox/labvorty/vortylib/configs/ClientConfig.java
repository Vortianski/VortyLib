package xox.labvorty.vortylib.configs;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue CHAOSLIB_WARNING = BUILDER
            .comment("Display a message if ChaosLib is not installed when Iris is running")
            .define("chaoslibWarning", true);

    public static final ModConfigSpec.IntValue UI_THEME = BUILDER
            .comment("Determines the theme of buttons in config screens")
            .defineInRange("uiTheme", 0, 0, 2);

    public static final ModConfigSpec.IntValue PANORAMA_THEME = BUILDER
            .comment("Determines the theme of panorama in config screens")
            .defineInRange("panoramaTheme", 0, 0, 2);

    public static final ModConfigSpec.BooleanValue MENU_BUTTON = BUILDER
            .comment("Whether config button is rendered in main menu")
            .define(
                    "menuConfigButton",
                    true
            );

    public static final ModConfigSpec.BooleanValue PAUSE_BUTTON = BUILDER
            .comment("Whether config button is rendered in pause menu")
            .define(
                    "pauseConfigButton",
                    true
            );

    public static final ModConfigSpec SPEC = BUILDER.build();
}