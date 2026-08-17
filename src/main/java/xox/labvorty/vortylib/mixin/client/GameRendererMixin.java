package xox.labvorty.vortylib.mixin.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xox.labvorty.vortylib.data.shaders.PostChainManager;
import xox.labvorty.vortylib.data.shaders.ShaderTime;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;getMainRenderTarget()Lcom/mojang/blaze3d/pipeline/RenderTarget;"
            )
    )
    private void vortylib$processLayers(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
        ShaderTime.INSTANCE.tick();
        PostChainManager.INSTANCE.processAll(deltaTracker.getGameTimeDeltaTicks());
    }

    @Inject(method = "resize", at = @At("TAIL"))
    private void vortylib$resizeLayers(int width, int height, CallbackInfo ci) {
        PostChainManager.INSTANCE.resize(width, height);
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void vortylib$closeLayers(CallbackInfo ci) {
        PostChainManager.INSTANCE.clear();
    }
}