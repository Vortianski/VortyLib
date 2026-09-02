package xox.labvorty.vortylib.compat.iris;

import net.minecraft.client.renderer.RenderType;

import java.lang.reflect.Method;

public final class ChaosLibAccess {

    private static final Method WRAP_ENTITY_RENDER_LAYER;

    static {
        Method method = null;

        try {
            Class<?> irisCompat = Class.forName(
                    "dev.elifian.chaoslib.api.iris.IrisCompat"
            );

            method = irisCompat.getMethod(
                    "wrapEntityRenderLayer",
                    RenderType.class,
                    RenderType.class
            );
        } catch (Throwable ignored) {
        }

        WRAP_ENTITY_RENDER_LAYER = method;
    }

    private ChaosLibAccess() {
    }

    public static RenderType wrapEntityRenderLayer(
            RenderType renderLayer,
            RenderType shadowRenderLayer
    ) {
        if (WRAP_ENTITY_RENDER_LAYER == null) {
            return renderLayer;
        }

        try {
            return (RenderType) WRAP_ENTITY_RENDER_LAYER.invoke(
                    null,
                    renderLayer,
                    shadowRenderLayer
            );
        } catch (Throwable ignored) {
            return renderLayer;
        }
    }
}