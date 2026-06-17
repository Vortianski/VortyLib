package xox.labvorty.vortylib.render.compat;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.io.IOException;

public class CompatibleShaderInstance extends ShaderInstance {
    public CompatibleShaderInstance(ResourceProvider resourceProvider, ResourceLocation shaderLocation, VertexFormat vertexFormat) throws IOException {
        super(resourceProvider, shaderLocation, vertexFormat);
    }

    //Force iris to render this shader. Missing shadows for now
    public boolean iris$shouldSkipThis() {
        return false;
    }
}
