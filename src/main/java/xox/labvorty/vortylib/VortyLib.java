package xox.labvorty.vortylib;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import xox.labvorty.vortylib.init.VortyLibDataComponents;

@Mod(VortyLib.MODID)
public class VortyLib {
    public static final String MODID = "vortylib";
    public static final Logger LOGGER = LogUtils.getLogger();

    public VortyLib(IEventBus modEventBus, ModContainer modContainer) {
        VortyLibDataComponents.DATA_COMPONENT_TYPES.register(modEventBus);
    }
}
