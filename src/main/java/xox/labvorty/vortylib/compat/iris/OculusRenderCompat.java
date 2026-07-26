package xox.labvorty.vortylib.compat.iris;

import dev.elifian.chaoslib.compat.oculus.OculusCompat;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.fml.ModList;

public final class OculusRenderCompat {
    private static final boolean OCULUS = ModList.get().isLoaded("oculus");
    private static final boolean CHAOSLIB = ModList.get().isLoaded("chaoslib");

    private OculusRenderCompat() {
    }

    public static boolean isAvailable() {
        return OCULUS && CHAOSLIB;
    }

    public static RenderType wrapEntityRenderLayer(RenderType renderLayer) {
        if (!isAvailable()) {
            return renderLayer;
        }

        return ChaosLibAccess.wrapEntityRenderLayer(renderLayer);
    }

    private static final class ChaosLibAccess {
        private ChaosLibAccess() {
        }

        private static RenderType wrapEntityRenderLayer(RenderType renderLayer) {
            return OculusCompat.wrapEntityRenderLayer(renderLayer);
        }
    }
}
