package xox.labvorty.vortylib;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(VortyLib.MOD_ID)
public class VortyLib {
    public static final String MOD_ID = "vortylib";
    public static final Logger LOGGER = LogUtils.getLogger();

    public VortyLib() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    }
}