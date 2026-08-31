package xox.labvorty.vortylib.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PanoramaConfiguredRenderer {
    public static final ResourceLocation PANORAMA_OVERLAY = ResourceLocation.withDefaultNamespace("textures/gui/title/background/panorama_overlay.png");
    private final Minecraft minecraft;
    private CubeMap cubeMap;
    private float spin;
    private float bob;

    public PanoramaConfiguredRenderer(CubeMap cubeMap) {
        this.cubeMap = cubeMap;
        this.minecraft = Minecraft.getInstance();
    }

    public void render(GuiGraphics guiGraphics, int width, int height, float fade, float partialTick) {
        partialTick = partialTick == 0.0F ? 0.0F : this.minecraft.getTimer().getRealtimeDeltaTicks();
        float f = (float)((double)partialTick * (Double)this.minecraft.options.panoramaSpeed().get());
        this.spin = wrap(this.spin + f * 0.1F, 360.0F);
        this.bob = wrap(this.bob + f * 0.001F, ((float)Math.PI * 2F));
        this.cubeMap.render(this.minecraft, 10.0F, -this.spin, fade);
        RenderSystem.enableBlend();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, fade);
        guiGraphics.blit(PANORAMA_OVERLAY, 0, 0, width, height, 0.0F, 0.0F, 16, 128, 16, 128);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
    }

    private static float wrap(float value, float max) {
        return value > max ? value - max : value;
    }

    public float getSpin() {
        return spin;
    }

    public float getBob() {
        return bob;
    }

    public void setCubeMap(CubeMap cubeMap) {
        this.cubeMap = cubeMap;
    }

    public CubeMap getCubeMap() {
        return cubeMap;
    }
}
