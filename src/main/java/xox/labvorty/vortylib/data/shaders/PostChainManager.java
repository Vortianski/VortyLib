package xox.labvorty.vortylib.data.shaders;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import xox.labvorty.vortylib.mixin_helpers.PostChainAccessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class PostChainManager {
    public static final PostChainManager INSTANCE = new PostChainManager();

    private final List<ActiveLayer> layers = new ArrayList<>();
    private float totalTime = 0f;

    private PostChainManager() {
    }

    public List<ActiveLayer> getLayers() {
        return Collections.unmodifiableList(layers);
    }

    public List<ResourceLocation> getOrder() {
        List<ResourceLocation> order = new ArrayList<>(layers.size());
        for (ActiveLayer layer : layers) {
            order.add(layer.getId());
        }
        return order;
    }

    public Optional<ActiveLayer> getLayer(ResourceLocation id) {
        return layers.stream().filter(l -> l.getId().equals(id)).findFirst();
    }

    public void addLayer(ResourceLocation id) {
        if (getLayer(id).isPresent()) return;

        Minecraft mc = Minecraft.getInstance();
        try {
            PostChain chain = new PostChain(mc.getTextureManager(), mc.getResourceManager(), mc.getMainRenderTarget(), id);
            chain.resize(mc.getWindow().getWidth(), mc.getWindow().getHeight());

            List<ShaderParamEntry<?>> params = ShaderParamRegistry.get(id)
                    .map(ShaderDefinition::instantiateParams)
                    .orElseGet(ArrayList::new);

            layers.add(new ActiveLayer(id, chain, params));
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    public void removeLayer(ResourceLocation id) {
        layers.removeIf(layer -> {
            if (layer.getId().equals(id)) {
                layer.getChain().close();
                return true;
            }
            return false;
        });
    }

    public boolean moveUp(ResourceLocation id) {
        int index = indexOf(id);
        if (index <= 0) return false;
        Collections.swap(layers, index, index - 1);
        return true;
    }

    public boolean moveDown(ResourceLocation id) {
        int index = indexOf(id);
        if (index < 0 || index >= layers.size() - 1) return false;
        Collections.swap(layers, index, index + 1);
        return true;
    }

    private int indexOf(ResourceLocation id) {
        for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i).getId().equals(id)) return i;
        }
        return -1;
    }

    public void clear() {
        layers.forEach(layer -> layer.getChain().close());
        layers.clear();
    }

    public void resize(int width, int height) {
        layers.forEach(layer -> layer.getChain().resize(width, height));
    }

    public float getTotalTime() {
        return totalTime;
    }

    public void processAll(float partialTick) {
        totalTime += partialTick;

        for (ActiveLayer layer : layers) {
            applyParams(layer);

            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();
            RenderSystem.resetTextureMatrix();
            layer.getChain().process(partialTick);
        }
    }

    private void applyParams(ActiveLayer layer) {
        if (layer.getParams().isEmpty()) return;

        List<PostPass> passes = ((PostChainAccessor) (Object) layer.getChain()).vortylib$getPasses();
        for (PostPass pass : passes) {
            for (ShaderParamEntry<?> param : layer.getParams()) {
                Uniform u = pass.getEffect().getUniform(param.getUniform());
                if (u != null) {
                    param.apply(u);
                }
            }
        }
    }
}