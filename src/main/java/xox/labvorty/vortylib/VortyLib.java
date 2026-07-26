package xox.labvorty.vortylib;

import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;
import xox.labvorty.vortylib.configs.ClientConfig;
import xox.labvorty.vortylib.init.VortyLibDataComponents;
import xox.labvorty.vortylib.init.VortyLibEntities;

import java.util.HashMap;
import java.util.Map;

@Mod(VortyLib.MODID)
public class VortyLib {
    public static final String MODID = "vortylib";
    public static final Logger LOGGER = LogUtils.getLogger();

    public VortyLib(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::registerNetworking);

        VortyLibDataComponents.DATA_COMPONENT_TYPES.register(modEventBus);
        VortyLibEntities.ENTITIES.register(modEventBus);

        modContainer.registerConfig(
                ModConfig.Type.CLIENT,
                ClientConfig.SPEC
        );
    }

    private static boolean networkingRegistered = false;
    private static final Map<CustomPacketPayload.Type<?>, NetworkMessage<?>> MESSAGES = new HashMap<>();

    private record NetworkMessage<T extends CustomPacketPayload>(StreamCodec<? extends FriendlyByteBuf, T> reader, IPayloadHandler<T> handler) {
    }

    public static <T extends CustomPacketPayload> void addNetworkMessage(CustomPacketPayload.Type<T> id, StreamCodec<? extends FriendlyByteBuf, T> reader, IPayloadHandler<T> handler) {
        if (networkingRegistered)
            throw new IllegalStateException("Cannot register new network messages after networking has been registered!");
        MESSAGES.put(id, new NetworkMessage<>(reader, handler));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void registerNetworking(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(MODID);
        MESSAGES.forEach((id, networkMessage) -> registrar.playBidirectional(id, ((NetworkMessage) networkMessage).reader(), ((NetworkMessage) networkMessage).handler()));
        networkingRegistered = true;
    }
}
