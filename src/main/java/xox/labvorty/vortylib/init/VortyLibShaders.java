package xox.labvorty.vortylib.init;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
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
    public static Window window;

    public static CompatibleShaderInstance ENTITY_END_PORTAL;
    public static CompatibleShaderInstance ENTITY_TRANSLUCENT_MASK;
    public static CompatibleShaderInstance ENTITY_NEGATIVE;
    public static CompatibleShaderInstance ENTITY_TRUE_NEGATIVE;
    public static CompatibleShaderInstance ENTITY_CRYSTAL;
    public static CompatibleShaderInstance ENTITY_STATIC_NOISE;
    public static CompatibleShaderInstance ENTITY_POLYCHROMATIC;
    public static CompatibleShaderInstance ENTITY_NEBULA;
    public static CompatibleShaderInstance ENTITY_CHROMATIC_ABERRATION;
    public static CompatibleShaderInstance ENTITY_PARALLAX;
    public static CompatibleShaderInstance ENTITY_COLORED_GLINT;
    public static CompatibleShaderInstance ENTITY_SPIRAL;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void register(RegisterShadersEvent event) {
        try {
            ENTITY_END_PORTAL = new CompatibleShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath("vortylib", "entity_end_portal"),
                    DefaultVertexFormat.NEW_ENTITY
            );
            event.registerShader(ENTITY_END_PORTAL, shaderInstance -> {
                Uniform time = shaderInstance.getUniform("GameTime");
                Uniform layers = shaderInstance.getUniform("EndPortalLayers");

                if (time != null) {
                    time.set((float)renderTime + renderFrame);
                }

                if (layers != null) {
                    layers.set(15);
                }

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
                Uniform time = shaderInstance.getUniform("Time");

                if (time != null) {
                    time.set((float)renderTime + renderFrame);
                }

                ENTITY_CRYSTAL.apply();
            });

            ENTITY_STATIC_NOISE = new CompatibleShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath("vortylib", "entity_static_noise"),
                    DefaultVertexFormat.NEW_ENTITY
            );
            event.registerShader(ENTITY_STATIC_NOISE, shaderInstance -> {
                Uniform time = shaderInstance.getUniform("GameTime");
                Uniform layers = shaderInstance.getUniform("StaticLayers");

                if (time != null) {
                    time.set((float)renderTime + renderFrame);
                }

                if (layers != null) {
                    layers.set(15);
                }

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
                Uniform time = shaderInstance.getUniform("GameTime");

                if (time != null) {
                    time.set((float)renderTime + renderFrame);
                }

                ENTITY_NEBULA.apply();
            });

            ENTITY_CHROMATIC_ABERRATION = new CompatibleShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath("vortylib", "entity_chromatic_aberration"),
                    DefaultVertexFormat.NEW_ENTITY
            );
            event.registerShader(ENTITY_CHROMATIC_ABERRATION, shaderInstance -> {
                Uniform time = shaderInstance.getUniform("GameTime");

                if (time != null) {
                    time.set((float)renderTime + renderFrame);
                }

                ENTITY_CHROMATIC_ABERRATION.apply();
            });

            ENTITY_PARALLAX = new CompatibleShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath("vortylib", "entity_translucent_parallax"),
                    DefaultVertexFormat.NEW_ENTITY
            );
            event.registerShader(ENTITY_PARALLAX, shaderInstance -> {
                Uniform time = shaderInstance.getUniform("GameTime");
                Uniform screenSize = shaderInstance.getUniform("ScreenSize");

                if (time != null) {
                    time.set((float)renderTime + renderFrame);
                }

                if (window != null) {
                    if (screenSize != null) {
                        screenSize.set((float)window.getWidth(), (float)window.getHeight());
                    }
                }

                ENTITY_PARALLAX.apply();
            });

            ENTITY_COLORED_GLINT = new CompatibleShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath("vortylib", "entity_colored_glint"),
                    DefaultVertexFormat.POSITION_TEX
            );
            event.registerShader(ENTITY_COLORED_GLINT, shaderInstance -> {
                Uniform alpha = shaderInstance.GLINT_ALPHA;

                if (alpha != null) {
                    alpha.set(RenderSystem.getShaderGlintAlpha());
                }

                ENTITY_COLORED_GLINT.apply();
            });

            ENTITY_SPIRAL = new CompatibleShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath("vortylib", "entity_spiral"),
                    DefaultVertexFormat.NEW_ENTITY
            );
            event.registerShader(ENTITY_SPIRAL, shaderInstance -> {
                Uniform gameTime = ENTITY_SPIRAL.GAME_TIME;
                Uniform screenSize = ENTITY_SPIRAL.getUniform("ScreenSize");

                if (gameTime != null) {
                    gameTime.set((float)renderTime + renderFrame);
                }

                if (screenSize != null && window != null) {
                    screenSize.set((float)window.getWidth(), (float)window.getHeight());
                }
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
            window = Minecraft.getInstance().getWindow();
        }
    }
}