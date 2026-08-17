package xox.labvorty.vortylib.mixin.client;

import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xox.labvorty.vortylib.mixin_helpers.PostChainAccessor;

import java.util.List;

@Mixin(PostChain.class)
public class PostChainMixin implements PostChainAccessor {
    @Shadow
    @Final
    private List<PostPass> passes;

    @Override
    public List<PostPass> vortylib$getPasses() {
        return this.passes;
    }
}
