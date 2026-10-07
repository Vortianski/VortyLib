package xox.labvorty.vortylib.data.shaders;

import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ActiveLayer {
    private final ResourceLocation id;
    private final PostChain chain;
    private final List<ShaderParamEntry<?>> params;

    public ActiveLayer(ResourceLocation id, PostChain chain, List<ShaderParamEntry<?>> params) {
        this.id = id;
        this.chain = chain;
        this.params = params;
    }

    public ResourceLocation getId() {
        return id;
    }

    public PostChain getChain() {
        return chain;
    }

    public List<ShaderParamEntry<?>> getParams() {
        return params;
    }
}