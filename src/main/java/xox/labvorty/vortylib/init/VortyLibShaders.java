package xox.labvorty.vortylib.init;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import xox.labvorty.vortylib.render.compat.CompatibleShaderInstance;

import java.io.IOException;

@EventBusSubscriber(value = Dist.CLIENT)
public class VortyLibShaders {
    public static int renderTime;
    public static float renderFrame;

    public static CompatibleShaderInstance ENTITY_END_PORTAL;
    public static CompatibleShaderInstance ENTITY_TRANSLUCENT_MASK;
    public static CompatibleShaderInstance ENTITY_NEGATIVE;
    public static CompatibleShaderInstance ENTITY_TRUE_NEGATIVE;
    public static CompatibleShaderInstance ENTITY_CRYSTAL;
    public static CompatibleShaderInstance ENTITY_STATIC_NOISE;
    public static CompatibleShaderInstance ENTITY_POLYCHROMATIC;
    public static CompatibleShaderInstance ENTITY_NEBULA;
    public static CompatibleShaderInstance ENTITY_STARFALL;

    public static Uniform endPortalTime;
    public static Uniform endPortalLayers;
    public static Uniform staticTime;
    public static Uniform staticLayers;
    public static Uniform nebulaTime;
    public static Uniform crystalTime;
    public static Uniform starfallTime;
    public static Uniform starfallSpeed;
    public static Uniform starfallRotation;
    public static Uniform starfallRotationSpeed;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void register(RegisterShadersEvent event) {
        try {
            ENTITY_END_PORTAL = new CompatibleShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath("vortylib", "entity_end_portal"),
                    DefaultVertexFormat.NEW_ENTITY
            );
            event.registerShader(ENTITY_END_PORTAL, shaderInstance -> {
                endPortalTime = shaderInstance.getUniform("GameTime");
                endPortalLayers = shaderInstance.getUniform("EndPortalLayers");
                endPortalTime.set((float) renderTime + renderFrame);
                endPortalLayers.set(15);

                ENTITY_END_PORTAL.apply();
            });

            ENTITY_TRANSLUCENT_MASK = new CompatibleShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath("vortylib", "entity_translucent_mask"),
                    DefaultVertexFormat.NEW_ENTITY
            );
            event.registerShader(ENTITY_TRANSLUCENT_MASK, shaderInstance -> {
                ENTITY_TRANSLUCENT_MASK.apply();
            });

            ENTITY_NEGATIVE = new CompatibleShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath("vortylib", "entity_negative"),
                    DefaultVertexFormat.NEW_ENTITY
            );
            event.registerShader(ENTITY_NEGATIVE, shaderInstance -> {
                ENTITY_NEGATIVE.apply();
            });

            ENTITY_TRUE_NEGATIVE = new CompatibleShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath("vortylib", "entity_true_negative"),
                    DefaultVertexFormat.NEW_ENTITY
            );
            event.registerShader(ENTITY_TRUE_NEGATIVE, shaderInstance -> {
                ENTITY_TRUE_NEGATIVE.apply();
            });

            ENTITY_CRYSTAL = new CompatibleShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath("vortylib", "entity_crystal"),
                    DefaultVertexFormat.NEW_ENTITY
            );
            event.registerShader(ENTITY_CRYSTAL, shaderInstance -> {
                crystalTime = shaderInstance.getUniform("Time");
                if (crystalTime != null) {
                    crystalTime.set((float) renderTime + renderFrame);
                }

                ENTITY_CRYSTAL.apply();
            });

            ENTITY_STATIC_NOISE = new CompatibleShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath("vortylib", "entity_static_noise"),
                    DefaultVertexFormat.NEW_ENTITY
            );
            event.registerShader(ENTITY_STATIC_NOISE, shaderInstance -> {
                staticTime = shaderInstance.getUniform("GameTime");
                staticLayers = shaderInstance.getUniform("StaticLayers");
                staticTime.set((float) renderTime + renderFrame);
                staticLayers.set(15);

                ENTITY_STATIC_NOISE.apply();
            });

            ENTITY_POLYCHROMATIC = new CompatibleShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath("vortylib", "entity_polychromatic"),
                    DefaultVertexFormat.NEW_ENTITY
            );
            event.registerShader(ENTITY_POLYCHROMATIC, shaderInstance -> {
                ENTITY_POLYCHROMATIC.apply();
            });

            ENTITY_NEBULA = new CompatibleShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath("vortylib", "entity_nebula"),
                    DefaultVertexFormat.NEW_ENTITY);
            event.registerShader(ENTITY_NEBULA, shaderInstance -> {
                nebulaTime = shaderInstance.getUniform("GameTime");
                if (nebulaTime != null) {
                    nebulaTime.set((float) renderTime + renderFrame);
                }

                ENTITY_NEBULA.apply();
            });

            ENTITY_STARFALL = new CompatibleShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath("vortylib", "entity_translucent_parallax"),
                    DefaultVertexFormat.NEW_ENTITY
            );
            event.registerShader(ENTITY_STARFALL, shaderInstance -> {
                starfallTime = shaderInstance.getUniform("GameTime");
                starfallSpeed = shaderInstance.getUniform("ParallaxSpeed");
                starfallRotation = shaderInstance.getUniform("ParallaxRotation");
                starfallRotationSpeed = shaderInstance.getUniform("ParallaxRotationSpeed");

                starfallTime.set((float) renderTime + renderFrame);
                starfallSpeed.set(10f, 15f);
                starfallRotation.set(45f);
                starfallRotationSpeed.set(0f);

                ENTITY_STARFALL.apply();
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Post event) {
        if (!Minecraft.getInstance().isPaused() ) {
            ++renderTime;
        }
    }

    @SubscribeEvent
    public static void renderTick(RenderFrameEvent.Pre event) {
        if (!Minecraft.getInstance().isPaused()) {
            renderFrame = event.getPartialTick().getGameTimeDeltaTicks();
        }
    }
}