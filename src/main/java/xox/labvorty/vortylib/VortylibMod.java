package xox.labvorty.vortylib;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import xox.labvorty.vortylib.configs.ClientConfig;

@Mod(VortylibMod.MOD_ID)
public class VortylibMod {
    public static final String MOD_ID = "vortylib";
    public static final Logger LOGGER = LogUtils.getLogger();

    public VortylibMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        context.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }
}
