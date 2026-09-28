package xox.labvorty.vortylib.mixins.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ForgeRenderTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xox.labvorty.vortylib.mixin_helpers.RenderTypeAccessor;

@Mixin(RenderType.class)
public class RenderTypeMixin implements RenderTypeAccessor {
    private ResourceLocation textResource;

    @Inject(
            at = @At("HEAD"),
            method = "text",
            cancellable = true
    )
    private static void backrooms$saveTextTexture(ResourceLocation location, CallbackInfoReturnable<RenderType> cir) {
        RenderType renderType = ForgeRenderTypes.getText(location);

        ((RenderTypeAccessor)renderType).setResourceLocation(location);

        cir.setReturnValue(
                renderType
        );
    }

    @Override
    public void setResourceLocation(ResourceLocation resourceLocation) {
        this.textResource = resourceLocation;
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return textResource;
    }
}
