package xox.labvorty.vortylib.init;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import xox.labvorty.vortylib.entity.renderer.SeatEntityRenderer;

@EventBusSubscriber
public class VortyLibRenderers {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(VortyLibEntities.SEAT_ENTITY.get(), SeatEntityRenderer::new);
    }
}
