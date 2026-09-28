package xox.labvorty.vortylib.events;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.joml.Vector3f;
import xox.labvorty.vortylib.init.VortyLibRenderTypes;
import xox.labvorty.vortylib.init.VortyLibShaders;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ShaderEvents {
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) return;

        if (!Minecraft.getInstance().isPaused() ) {
            ++VortyLibShaders.renderTime;
        }
    }

    @SubscribeEvent
    public static void renderTick(RenderLevelStageEvent event) {
        if (!Minecraft.getInstance().isPaused()) {
            VortyLibShaders.renderFrame = event.getPartialTick();
            VortyLibShaders.window = Minecraft.getInstance().getWindow();
        }
    }

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (int r = 0; r < 4; r++) {
                for (int g = 0; g < 4; g++) {
                    for (int b = 0; b < 4; b++) {
                        Vector3f color = new Vector3f(
                                r / 3f,
                                g / 3f,
                                b / 3f
                        );

                        VortyLibRenderTypes.registerEntityColoredGlint(color);
                    }
                }
            }
        });
    }
}
