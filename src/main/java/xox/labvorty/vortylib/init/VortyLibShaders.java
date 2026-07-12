package xox.labvorty.vortylib.init;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class VortyLibShaders {
    public static ShaderInstance ENTITY_END_PORTAL;
    public static ShaderInstance ENTITY_POLYCHROMATIC;
    public static ShaderInstance ENTITY_TRANSLUCENT_MASK;
    public static ShaderInstance ENTITY_STATIC_NOISE;
    public static ShaderInstance ENTITY_CRYSTAL;
    public static ShaderInstance ENTITY_NEGATIVE;
    public static ShaderInstance ENTITY_TRUE_NEGATIVE;
    public static ShaderInstance ENTITY_NEBULA;
    public static ShaderInstance ENTITY_STARFALL;

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

    public static int renderTime;
    public static float renderFrame;

    @SubscribeEvent
    public static void register(RegisterShadersEvent event) {
        try {
            ENTITY_END_PORTAL = new ShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(
                            "vortylib",
                            "entity_end_portal"
                    ),
                    DefaultVertexFormat.NEW_ENTITY
            );
            event.registerShader(ENTITY_END_PORTAL, (shaderInstance) -> {
                endPortalTime = shaderInstance.getUniform("GameTime");
                endPortalLayers = shaderInstance.getUniform("EndPortalLayers");
                endPortalTime.set((float) renderTime + renderFrame);
                endPortalLayers.set(15);

                ENTITY_END_PORTAL.apply();
            });

            ENTITY_POLYCHROMATIC = new ShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(
                            "vortylib",
                            "entity_polychromatic"
                    ),
                    DefaultVertexFormat.NEW_ENTITY
            );
            event.registerShader(ENTITY_POLYCHROMATIC, (shaderInstance) -> {
                ENTITY_POLYCHROMATIC.apply();
            });

            ENTITY_TRANSLUCENT_MASK = new ShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(
                            "vortylib",
                            "entity_translucent_mask"
                    ),
                    DefaultVertexFormat.NEW_ENTITY
            );
            event.registerShader(ENTITY_TRANSLUCENT_MASK, (shaderInstance) -> {
                ENTITY_TRANSLUCENT_MASK.apply();
            });

            ENTITY_STATIC_NOISE = new ShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(
                            "vortylib",
                            "entity_static_noise"
                    ),
                    DefaultVertexFormat.NEW_ENTITY
            );
            event.registerShader(ENTITY_STATIC_NOISE, (shaderInstance) -> {
                staticTime = shaderInstance.getUniform("GameTime");
                staticLayers = shaderInstance.getUniform("StaticLayers");
                staticTime.set((float) renderTime + renderFrame);
                staticLayers.set(15);

                ENTITY_STATIC_NOISE.apply();
            });

            ENTITY_CRYSTAL = new ShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(
                            "vortylib",
                            "entity_crystal"
                    ),
                    DefaultVertexFormat.NEW_ENTITY
            );
            event.registerShader(ENTITY_CRYSTAL, (shaderInstance) -> {
                crystalTime = shaderInstance.getUniform("Time");
                if (crystalTime != null) {
                    crystalTime.set((float) renderTime + renderFrame);
                }

                ENTITY_CRYSTAL.apply();
            });

            ENTITY_NEGATIVE = new ShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(
                            "vortylib",
                            "entity_negative"
                    ),
                    DefaultVertexFormat.NEW_ENTITY
            );
            event.registerShader(ENTITY_NEGATIVE, (shaderInstance) -> {
                ENTITY_NEGATIVE.apply();
            });

            ENTITY_TRUE_NEGATIVE = new ShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(
                            "vortylib",
                            "entity_true_negative"
                    ),
                    DefaultVertexFormat.NEW_ENTITY
            );
            event.registerShader(ENTITY_TRUE_NEGATIVE, (shaderInstance) -> {
                ENTITY_TRUE_NEGATIVE.apply();
            });

            ENTITY_NEBULA = new ShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(
                            "vortylib",
                            "entity_nebula"
                    ),
                    DefaultVertexFormat.NEW_ENTITY
            );
            event.registerShader(ENTITY_NEBULA, (shaderInstance) -> {
                nebulaTime = shaderInstance.getUniform("GameTime");
                if (nebulaTime != null) {
                    nebulaTime.set((float)renderTime + renderFrame);
                }

                ENTITY_NEBULA.apply();
            });

            ENTITY_STARFALL = new ShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(
                            "vortylib",
                            "entity_translucent_parallax"
                    ),
                    DefaultVertexFormat.NEW_ENTITY
            );
            event.registerShader(ENTITY_STARFALL, (shaderInstance) -> {
                starfallTime = shaderInstance.getUniform("GameTime");
                if (starfallTime != null) {
                    starfallTime.set((float)renderTime + renderFrame);
                }

                starfallSpeed = shaderInstance.getUniform("ParallaxSpeed");
                if (starfallSpeed != null) {
                    starfallSpeed.set(10f, 15f);
                }

                starfallRotation = shaderInstance.getUniform("ParallaxRotation");
                if (starfallRotation != null) {
                    starfallRotation.set(45f);
                }

                starfallRotationSpeed = shaderInstance.getUniform("ParallaxRotationSpeed");
                if (starfallRotationSpeed != null) {
                    starfallRotationSpeed.set(0f);
                }

                ENTITY_STARFALL.apply();
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
