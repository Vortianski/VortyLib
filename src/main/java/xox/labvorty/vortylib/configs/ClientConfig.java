package xox.labvorty.vortylib.configs;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<Boolean> CHAOSLIB_WARNING = BUILDER
            .comment("Display a message if ChaosLib is not installed when Iris is running")
            .define("chaoslibWarning", true);

    public static final ModConfigSpec SPEC = BUILDER.build();
}
