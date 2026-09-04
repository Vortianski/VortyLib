package xox.labvorty.vortylib.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.resources.ResourceLocation;
import xox.labvorty.vortylib.gui.theme.PanoramaTheme;
import xox.labvorty.vortylib.gui.widget.PanoramaConfiguredRenderer;

public final class VortyLibBackground {
    public static PanoramaConfiguredRenderer panoramaRenderer = new PanoramaConfiguredRenderer(new CubeMap(PanoramaTheme.current()));

    public static void render(GuiGraphics guiGraphics, int width, int height, float partialTick, Runnable vanillaFallback) {
        ResourceLocation panorama = PanoramaTheme.current();
        CubeMap cubeMap = new CubeMap(panorama);

        if (!panoramaRenderer.getCubeMap().equals(cubeMap)) {
            panoramaRenderer.setCubeMap(cubeMap);
        }

        panoramaRenderer.render(guiGraphics, width, height, 1.0F, partialTick);
        guiGraphics.fill(0, 0, width, height, 0x80101010);
    }
}