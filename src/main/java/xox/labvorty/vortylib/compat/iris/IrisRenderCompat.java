package xox.labvorty.vortylib.compat.iris;

import net.minecraft.client.renderer.RenderType;
import net.neoforged.fml.ModList;

public final class IrisRenderCompat {
    private static final boolean IRIS_LOADED = ModList.get().isLoaded("iris");
    private static final boolean CHAOSLIB_LOADED = ModList.get().isLoaded("chaoslib");

    private IrisRenderCompat() {
    }

    public static boolean isIrisLoaded() {
        return IRIS_LOADED;
    }

    public static boolean isChaosLibLoaded() {
        return CHAOSLIB_LOADED;
    }

    public static boolean isAvailable() {
        return IRIS_LOADED && CHAOSLIB_LOADED;
    }

    public static RenderType wrapEntityRenderLayer(RenderType renderLayer, RenderType shadowRenderLayer) {
        if (!isAvailable()) {
            return renderLayer;
        }

        return ChaosLibAccess.wrapEntityRenderLayer(renderLayer, shadowRenderLayer);
    }
}
