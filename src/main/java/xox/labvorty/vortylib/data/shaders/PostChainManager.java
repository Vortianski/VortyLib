package xox.labvorty.vortylib.data.shaders;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class PostChainManager {
    public static final PostChainManager INSTANCE = new PostChainManager();

    private final LinkedHashMap<ResourceLocation, PostChain> layers = new LinkedHashMap<>();
    private final Map<ResourceLocation, Consumer<PostChain>> updaters = new HashMap<>();
    private float totalTime = 0f;

    private PostChainManager() {}

    public void addLayer(ResourceLocation id) {
        addLayer(id, null);
    }

    public void addLayer(ResourceLocation id, Consumer<PostChain> perFrameUpdater) {
        Minecraft mc = Minecraft.getInstance();
        removeLayer(id);
        try {
            PostChain chain = new PostChain(mc.getTextureManager(), mc.getResourceManager(), mc.getMainRenderTarget(), id);
            chain.resize(mc.getWindow().getWidth(), mc.getWindow().getHeight());
            layers.put(id, chain);
            if (perFrameUpdater != null) updaters.put(id, perFrameUpdater);
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    public void removeLayer(ResourceLocation id) {
        PostChain chain = layers.remove(id);
        if (chain != null) chain.close();
        updaters.remove(id);
    }

    public void clear() {
        layers.values().forEach(PostChain::close);
        layers.clear();
        updaters.clear();
    }

    public void resize(int width, int height) {
        layers.values().forEach(c -> c.resize(width, height));
    }

    public float getTotalTime() {
        return totalTime;
    }

    public void processAll(float partialTick) {
        for (Map.Entry<ResourceLocation, PostChain> entry : layers.entrySet()) {
            Consumer<PostChain> updater = updaters.get(entry.getKey());
            if (updater != null) {
                updater.accept(entry.getValue());
            }

            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();
            RenderSystem.resetTextureMatrix();
            entry.getValue().process(partialTick);
        }
    }
}