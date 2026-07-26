package xox.labvorty.vortylib.init;

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
import xox.labvorty.vortylib.compat.iris.IrisRenderCompat;

import java.util.ArrayList;
import java.util.List;
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
    private static final Function<List<ResourceLocation>, RenderType> ENTITY_STARFALL = Util.memoize(
            data -> {
                ResourceLocation textureOne = data.get(0);
                ResourceLocation textureTwo = data.get(1);

                return createEntityStarfall(textureOne, textureTwo);
            }
    );

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

    private static RenderType createEntityStarfall(ResourceLocation textureOne, ResourceLocation textureTwo) {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> VortyLibShaders.ENTITY_STARFALL))
                .setTextureState(
                        RenderStateShard.MultiTextureStateShard.builder()
                                .add(textureOne,false,false)
                                .add(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/debug.png"), false, false)
                                .add(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/debug.png"), false, false)
                                .add(textureTwo,false,false)
                                .build()
                )
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderStateShard.NO_CULL)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(true);

        return RenderType.create(
                "entity_starfall",
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

    public static RenderType getEntityStarfall(ResourceLocation textureOne, ResourceLocation textureTwo) {
        List<ResourceLocation> data = new ArrayList<>();

        data.add(textureOne);
        data.add(textureTwo);

        return ENTITY_STARFALL.apply(data);
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
        event.registerRenderBuffer(getEntityStarfall(DEBUG_TEXTURE, DEBUG_TEXTURE));
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
}
