package xox.labvorty.vortylib.compat.iris;

import dev.elifian.chaoslib.api.iris.IrisCompat;
import net.minecraft.client.renderer.RenderType;

public final class ChaosLibAccess {
        private ChaosLibAccess() {
        }

        public static RenderType wrapEntityRenderLayer(RenderType renderLayer, RenderType shadowRenderLayer) {
            return IrisCompat.wrapEntityRenderLayer(renderLayer, shadowRenderLayer);
        }
}