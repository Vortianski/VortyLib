package xox.labvorty.vortylib;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(VortyLib.MODID)
public class VortyLib {
    public static final String MODID = "vortylib";
    public static final Logger LOGGER = LogUtils.getLogger();

    public VortyLib(IEventBus modEventBus, ModContainer modContainer) {
    }
}
