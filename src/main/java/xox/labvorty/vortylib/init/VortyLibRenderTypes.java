package xox.labvorty.vortylib.init;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent;
import org.joml.Vector3f;
import org.joml.Vector4f;
import oshi.util.tuples.Pair;
import xox.labvorty.vortylib.compat.iris.IrisRenderCompat;
import xox.labvorty.vortylib.render.compat.CompatibleShaderInstance;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT)
public class VortyLibRenderTypes {
    static ResourceLocation DEBUG_TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/debug.png");

    private static final Function<ResourceLocation, RenderType> TEXT_NO_CULL = Util.memoize(VortyLibRenderTypes::createTextNoCull);
    private static final Function<List<ResourceLocation>, RenderType> ENTITY_END_PORTAL = Util.memoize(
            data -> {
                ResourceLocation textureOne = data.get(0);
                ResourceLocation textureTwo = data.get(1);
                ResourceLocation textureThree = data.get(2);

                return createEntityEndPortal(textureOne, textureTwo, textureThree);
            }
    );
    private static final Function<List<ResourceLocation>, RenderType> ENTITY_TRANSLUCENT_MASK = Util.memoize(
            data -> {
                ResourceLocation textureOne = data.get(0);
                ResourceLocation textureTwo = data.get(1);

                return createEntityTranslucentMask(textureOne, textureTwo);
            }
    );
    private static final Function<ResourceLocation, RenderType> ENTITY_NEGATIVE = Util.memoize(VortyLibRenderTypes::createEntityNegative);
    private static final Function<ResourceLocation, RenderType> ENTITY_TRUE_NEGATIVE = Util.memoize(VortyLibRenderTypes::createEntityTrueNegative);
    private static final Function<ResourceLocation, RenderType> ENTITY_CRYSTAL = Util.memoize(VortyLibRenderTypes::createEntityCrystal);
    private static final Function<ResourceLocation, RenderType> ENTITY_STATIC_NOISE = Util.memoize(VortyLibRenderTypes::createEntityStaticNoise);
    private static final Function<ResourceLocation, RenderType> ENTITY_POLYCHROMATIC = Util.memoize(VortyLibRenderTypes::createEntityPolychromatic);
    private static final Function<ResourceLocation, RenderType> ENTITY_POLYCHROMATIC_CULL = Util.memoize(VortyLibRenderTypes::createEntityPolychromaticCull);
    private static final Function<ResourceLocation, RenderType> ENTITY_NEBULA = Util.memoize(VortyLibRenderTypes::createEntityNebula);
    private static final Function<ResourceLocation, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_CULL = Util.memoize(VortyLibRenderTypes::createEntityTranslucentEmissiveCull);
    private static final Function<ResourceLocation, RenderType> ENTITY_CHROMATIC_ABERRATION = Util.memoize(VortyLibRenderTypes::createEntityChromaticAberration);
    private static final Function<ParallaxRenderOptions, RenderType> ENTITY_PARALLAX = Util.memoize(VortyLibRenderTypes::createEntityParallax);
    private static final Map<Vector3f, RenderType> ENTITY_COLORED_GLINT = new HashMap<>();
    private static final Function<ResourceLocation, RenderType> ENTITY_SPIRAL = Util.memoize(VortyLibRenderTypes::createEntitySpiral);

    private static RenderType createTextNoCull(ResourceLocation resourceLocation) {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.RENDERTYPE_TEXT_SHADER)
                .setTextureState(new CustomizableTextureState(resourceLocation, () -> NeoForgeRenderTypes.enableTextTextureLinearFiltering, () -> false))
                .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
                .setLightmapState(RenderType.LIGHTMAP)
                .setCullState(RenderType.NO_CULL)
                .createCompositeState(false);

        return RenderType.create(
                "text_no_cull",
                DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
                VertexFormat.Mode.QUADS,
                256,
                false,
                true,
                compositeState
        );
    }

    private static RenderType createEntityEndPortal(ResourceLocation textureOne, ResourceLocation textureTwo, ResourceLocation textureThree) {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> VortyLibShaders.ENTITY_END_PORTAL))
                .setTextureState(RenderStateShard.MultiTextureStateShard.builder()
                        .add(textureOne, false, false)
                        .add(textureTwo, false, false)
                        .add(textureThree, false, false)
                        .add(textureTwo, false, false)
                        .build()
                )
                .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderStateShard.NO_CULL)
                .setOverlayState(RenderStateShard.OVERLAY)
                .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
                .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                .createCompositeState(true);

        return RenderType.create(
                "entity_end_portal",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                1536,
                false,
                false,
                compositeState
        );
    }

    private static RenderType createEntityTranslucentMask(ResourceLocation textureOne, ResourceLocation textureTwo) {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> VortyLibShaders.ENTITY_TRANSLUCENT_MASK))
                .setTextureState(
                        RenderStateShard.MultiTextureStateShard.builder()
                                .add(textureOne, false, false)
                                .add(DEBUG_TEXTURE, false, false)
                                .add(DEBUG_TEXTURE, false, false)
                                .add(textureTwo, false, false)
                                .build()
                )
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderStateShard.NO_CULL)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(true);

        return RenderType.create(
                "entity_translucent_mask",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                1536,
                true,
                true,
                compositeState
        );
    }

    private static RenderType createEntityNegative(ResourceLocation resourceLocation) {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> VortyLibShaders.ENTITY_NEGATIVE))
                .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderStateShard.NO_CULL)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(true);

        return RenderType.create(
                "entity_negative",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                1536,
                true,
                true,
                compositeState
        );
    }

    private static RenderType createEntityTrueNegative(ResourceLocation resourceLocation) {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> VortyLibShaders.ENTITY_TRUE_NEGATIVE))
                .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderStateShard.NO_CULL)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(true);

        return RenderType.create(
                "entity_true_negative",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                1536,
                true,
                true,
                compositeState
        );
    }

    private static RenderType createEntityCrystal(ResourceLocation resourceLocation) {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> VortyLibShaders.ENTITY_CRYSTAL))
                .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderStateShard.NO_CULL)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(true);


        return RenderType.create(
                "entity_crystal",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                1536,
                true,
                true,
                compositeState
        );
    }

    private static RenderType createEntityStaticNoise(ResourceLocation resourceLocation) {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> VortyLibShaders.ENTITY_STATIC_NOISE))
                .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderStateShard.NO_CULL)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(true);

        return RenderType.create(
                "entity_static_noise",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                1536,
                true,
                true,
                compositeState
        );
    }

    private static RenderType createEntityPolychromatic(ResourceLocation resourceLocation) {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> VortyLibShaders.ENTITY_POLYCHROMATIC))
                .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderStateShard.NO_CULL)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(true);


        return RenderType.create(
                "entity_polychromatic",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                1536,
                true,
                true,
                compositeState
        );
    }

    private static RenderType createEntityPolychromaticCull(ResourceLocation resourceLocation) {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> VortyLibShaders.ENTITY_POLYCHROMATIC))
                .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderStateShard.CULL)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(true);


        return RenderType.create(
                "entity_polychromatic_cull",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                1536,
                true,
                true,
                compositeState
        );
    }

    private static RenderType createEntityNebula(ResourceLocation resourceLocation) {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> VortyLibShaders.ENTITY_NEBULA))
                .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderStateShard.NO_CULL)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(true);


        return RenderType.create(
                "entity_nebula",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                1536,
                true,
                true,
                compositeState
        );
    }

    private static RenderType createEntityTranslucentEmissiveCull(ResourceLocation resourceLocation) {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderStateShard.CULL)
                .setOverlayState(RenderStateShard.OVERLAY)
                .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                .createCompositeState(false);

        return RenderType.create(
                "entity_translucent_emissive_cull",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                1536,
                true,
                true,
                compositeState
        );
    }

    private static RenderType createEntityChromaticAberration(ResourceLocation resourceLocation) {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> VortyLibShaders.ENTITY_CHROMATIC_ABERRATION))
                .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderStateShard.NO_CULL)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(true);

        return RenderType.create(
                "entity_chromatic_aberration",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                1536,
                true,
                true,
                compositeState
        );
    }

    private static RenderType createEntityParallax(ParallaxRenderOptions parallaxRenderOptions) {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> {
                    CompatibleShaderInstance shaderInstance = VortyLibShaders.ENTITY_PARALLAX;

                    Uniform speed = shaderInstance.getUniform("ParallaxSpeed");
                    if (speed != null) {
                        speed.set(parallaxRenderOptions.speed().getA(), parallaxRenderOptions.speed().getB());
                    }

                    Uniform rotation = shaderInstance.getUniform("ParallaxRotation");
                    if (rotation != null) {
                        rotation.set(parallaxRenderOptions.rotation());
                    }

                    Uniform rotationSpeed = shaderInstance.getUniform("ParallaxRotationSpeed");
                    if (rotationSpeed != null) {
                        rotationSpeed.set(parallaxRenderOptions.rotationSpeed());
                    }

                    Uniform scale = shaderInstance.getUniform("ParallaxScale");
                    if (scale != null) {
                        scale.set(parallaxRenderOptions.scale());
                    }

                    Uniform color = shaderInstance.getUniform("ParallaxColor");
                    if (color != null) {
                        color.set(parallaxRenderOptions.color.x, parallaxRenderOptions.color.y, parallaxRenderOptions.color.z, parallaxRenderOptions.color.w);
                    }

                    shaderInstance.apply();

                    return shaderInstance;
                }))
                .setTextureState(
                        RenderStateShard.MultiTextureStateShard.builder()
                                .add(parallaxRenderOptions.resourceLocation(),false,false)
                                .add(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/debug.png"), false, false)
                                .add(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/debug.png"), false, false)
                                .add(parallaxRenderOptions.maskLocation(),false,false)
                                .build()
                )
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderStateShard.NO_CULL)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(true);

        return RenderType.create(
                "entity_translucent_parallax",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                1536,
                true,
                true,
                compositeState
        );
    }

    private static RenderType createEntityColoredGlint(ColoredGlintOptions coloredGlintOptions) {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> {
                    CompatibleShaderInstance shaderInstance = VortyLibShaders.ENTITY_COLORED_GLINT;

                    Uniform color = shaderInstance.getUniform("GlintColor");
                    if (color != null) {
                        color.set(coloredGlintOptions.color.x, coloredGlintOptions.color.y, coloredGlintOptions.color.z);
                    }

                    return shaderInstance;
                }))
                .setTextureState(new RenderStateShard.TextureStateShard(ResourceLocation.fromNamespaceAndPath("vortylib", "textures/misc/enchanted_glint_entity.png"), true, false))
                .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                .setCullState(RenderStateShard.NO_CULL)
                .setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
                .setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
                .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
                .setTexturingState(RenderStateShard.ENTITY_GLINT_TEXTURING)
                .createCompositeState(false);

        return RenderType.create(
                "entity_colored_glint",
                DefaultVertexFormat.POSITION_TEX,
                VertexFormat.Mode.QUADS,
                1536,
                compositeState
        );
    }

    private static RenderType createEntitySpiral(ResourceLocation resourceLocation) {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> VortyLibShaders.ENTITY_SPIRAL))
                .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderStateShard.NO_CULL)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(true);

        return RenderType.create(
                "entity_spiral",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                1536,
                true,
                true,
                compositeState
        );
    }

    public static RenderType getTextNoCull(ResourceLocation resourceLocation) {
        return wrapThis(TEXT_NO_CULL.apply(resourceLocation), resourceLocation);
    }

    public static RenderType getEntityEndPortal(ResourceLocation textureOne, ResourceLocation textureTwo, ResourceLocation textureThree) {
        return wrapThis(ENTITY_END_PORTAL.apply(List.of(textureOne, textureTwo, textureThree)), textureTwo);
    }

    public static RenderType getEntityTranslucentMask(ResourceLocation textureOne, ResourceLocation textureTwo) {
        return wrapThis(ENTITY_TRANSLUCENT_MASK.apply(List.of(textureOne, textureTwo)), textureOne);
    }

    public static RenderType getEntityNegative(ResourceLocation resourceLocation) {
        return wrapThis(ENTITY_NEGATIVE.apply(resourceLocation), resourceLocation);
    }

    public static RenderType getEntityTrueNegative(ResourceLocation resourceLocation) {
        return wrapThis(ENTITY_TRUE_NEGATIVE.apply(resourceLocation), resourceLocation);
    }

    public static RenderType getEntityCrystal(ResourceLocation resourceLocation) {
        return wrapThis(ENTITY_CRYSTAL.apply(resourceLocation), resourceLocation);
    }

    public static RenderType getEntityStaticNoise(ResourceLocation resourceLocation) {
        return wrapThis(ENTITY_STATIC_NOISE.apply(resourceLocation), resourceLocation);
    }

    public static RenderType getEntityPolychromatic(ResourceLocation resourceLocation) {
        return wrapThis(ENTITY_POLYCHROMATIC.apply(resourceLocation), resourceLocation);
    }

    public static RenderType getEntityPolychromaticCull(ResourceLocation resourceLocation) {
        return wrapThis(ENTITY_POLYCHROMATIC_CULL.apply(resourceLocation), resourceLocation);
    }

    public static RenderType getEntityNebula(ResourceLocation resourceLocation) {
        return wrapThis(ENTITY_NEBULA.apply(resourceLocation), resourceLocation);
    }

    public static RenderType getEntityTranslucentEmissiveCull(ResourceLocation resourceLocation) {
        return ENTITY_TRANSLUCENT_EMISSIVE_CULL.apply(resourceLocation);
    }

    public static RenderType getEntityChromaticAberration(ResourceLocation resourceLocation) {
        return wrapThis(ENTITY_CHROMATIC_ABERRATION.apply(resourceLocation), resourceLocation);
    }

    public static RenderType getEntityParallax(ResourceLocation resourceLocation, ResourceLocation resourceLocation0, Pair<Float, Float> speed, float rotation, float rotationSpeed, float scale) {
        return wrapThis(ENTITY_PARALLAX.apply(new ParallaxRenderOptions(resourceLocation, resourceLocation0, speed, rotation, rotationSpeed, scale)), resourceLocation);
    }

    public static RenderType getEntityParallax(ResourceLocation resourceLocation, ResourceLocation resourceLocation0, Pair<Float, Float> speed, float rotation, float rotationSpeed, float scale, Vector4f vector4f) {
        return wrapThis(ENTITY_PARALLAX.apply(new ParallaxRenderOptions(resourceLocation, resourceLocation0, speed, rotation, rotationSpeed, scale, vector4f)), resourceLocation);
    }

    public static RenderType getEntityColoredGlint(Vector3f color) {
        float r = Math.round(color.x * 3f) / 3f;
        float g = Math.round(color.y * 3f) / 3f;
        float b = Math.round(color.z * 3f) / 3f;

        Vector3f closestColor = new Vector3f(r, g, b);

        RenderType renderType = ENTITY_COLORED_GLINT.get(closestColor);

        if (renderType == null) {
            throw new IllegalStateException("Missing pre-generated entity colored glint for color " + closestColor);
        }

        return wrapThis(renderType, ResourceLocation.fromNamespaceAndPath("vortylib", "textures/misc/enchanted_glint_entity.png"));
    }

    private static RenderType registerEntityColoredGlint(Vector3f color) {
        RenderType renderType = createEntityColoredGlint(
                new ColoredGlintOptions(color)
        );

        ENTITY_COLORED_GLINT.put(color, renderType);

        return renderType;
    }

    public static RenderType getEntitySpiral(ResourceLocation resourceLocation) {
        return wrapThis(ENTITY_SPIRAL.apply(resourceLocation), resourceLocation);
    }

    public static RenderType wrapThis(RenderType renderType, ResourceLocation resourceLocation) {
        return IrisRenderCompat.wrapEntityRenderLayer(renderType, RenderType.entityTranslucent(resourceLocation));
    }

    @SubscribeEvent
    public static void register(RegisterRenderBuffersEvent event) {
        event.registerRenderBuffer(getTextNoCull(DEBUG_TEXTURE));
        event.registerRenderBuffer(getEntityEndPortal(DEBUG_TEXTURE, DEBUG_TEXTURE, DEBUG_TEXTURE));
        event.registerRenderBuffer(getEntityTranslucentMask(DEBUG_TEXTURE, DEBUG_TEXTURE));
        event.registerRenderBuffer(getEntityNegative(DEBUG_TEXTURE));
        event.registerRenderBuffer(getEntityTrueNegative(DEBUG_TEXTURE));
        event.registerRenderBuffer(getEntityCrystal(DEBUG_TEXTURE));
        event.registerRenderBuffer(getEntityStaticNoise(DEBUG_TEXTURE));
        event.registerRenderBuffer(getEntityPolychromatic(DEBUG_TEXTURE));
        event.registerRenderBuffer(getEntityPolychromaticCull(DEBUG_TEXTURE));
        event.registerRenderBuffer(getEntityNebula(DEBUG_TEXTURE));
        event.registerRenderBuffer(getEntityTranslucentEmissiveCull(DEBUG_TEXTURE));
        event.registerRenderBuffer(getEntityChromaticAberration(DEBUG_TEXTURE));
        event.registerRenderBuffer(getEntityParallax(DEBUG_TEXTURE, DEBUG_TEXTURE, new Pair<>(0f, 0f), 0, 0, 0));
        for (int r = 0; r < 4; r++) {
            for (int g = 0; g < 4; g++) {
                for (int b = 0; b < 4; b++) {
                    Vector3f color = new Vector3f(
                            r / 3f,
                            g / 3f,
                            b / 3f
                    );

                    event.registerRenderBuffer(registerEntityColoredGlint(color));
                }
            }
        }
        event.registerRenderBuffer(getEntitySpiral(DEBUG_TEXTURE));
    }

    private static class CustomizableTextureState extends RenderStateShard.TextureStateShard {
        private final BooleanSupplier blurSupplier;
        private final BooleanSupplier mipmapSupplier;

        private CustomizableTextureState(ResourceLocation resLoc, BooleanSupplier blur, BooleanSupplier mipmap) {
            super(resLoc, blur.getAsBoolean(), mipmap.getAsBoolean());
            this.blurSupplier = blur;
            this.mipmapSupplier = mipmap;
        }

        public void setupRenderState() {
            this.blur = this.blurSupplier.getAsBoolean();
            this.mipmap = this.mipmapSupplier.getAsBoolean();
            super.setupRenderState();
        }
    }

    private record ColoredGlintOptions(
            Vector3f color
    ) {}

    private record ParallaxRenderOptions(ResourceLocation resourceLocation, ResourceLocation maskLocation, Pair<Float, Float> speed, float rotation, float rotationSpeed, float scale, Vector4f color) {
            public ParallaxRenderOptions(
                    ResourceLocation resourceLocation,
                    ResourceLocation maskLocation,
                    Pair<Float, Float> speed,
                    float rotation,
                    float rotationSpeed,
                    float scale
            ) {
                this(resourceLocation, maskLocation, speed, rotation, rotationSpeed, scale, new Vector4f(1, 1, 1, 1));
            }
    }
}
