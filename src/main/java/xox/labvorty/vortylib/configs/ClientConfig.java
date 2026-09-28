package xox.labvorty.vortylib.configs;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue CHAOSLIB_WARNING = BUILDER
            .comment("Display a message if ChaosLib is not installed when Iris is running")
            .define("chaoslibWarning", true);

    public static final ForgeConfigSpec.IntValue UI_THEME = BUILDER
            .comment("Determines the theme of buttons in config screens")
            .defineInRange("uiTheme", 0, 0, 2);

    public static final ForgeConfigSpec.IntValue PANORAMA_THEME = BUILDER
            .comment("Determines the theme of panorama in config screens")
            .defineInRange("panoramaTheme", 0, 0, 2);

    public static final ForgeConfigSpec.BooleanValue MENU_BUTTON = BUILDER
            .comment("Whether config button is rendered in main menu")
            .define(
                    "menuConfigButton",
                    true
            );

    public static final ForgeConfigSpec.BooleanValue PAUSE_BUTTON = BUILDER
            .comment("Whether config button is rendered in pause menu")
            .define(
                    "pauseConfigButton",
                    true
            );

    public static final ForgeConfigSpec SPEC = BUILDER.build();
}