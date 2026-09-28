package xox.labvorty.vortylib.render.compat;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;

@OnlyIn(Dist.CLIENT)
public class CompatibleShaderInstance extends ShaderInstance {
    private boolean framebufferBound;

    public CompatibleShaderInstance(
            ResourceProvider resourceProvider,
            ResourceLocation shaderLocation,
            VertexFormat vertexFormat
    ) throws IOException {
        super(resourceProvider, shaderLocation, vertexFormat);
    }

    @SuppressWarnings("unused")
    public boolean iris$shouldSkipThis() {
        return false;
    }
}
