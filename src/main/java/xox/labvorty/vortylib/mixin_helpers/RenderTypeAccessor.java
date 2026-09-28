package xox.labvorty.vortylib.mixin_helpers;

import net.minecraft.resources.ResourceLocation;

public interface RenderTypeAccessor {
    ResourceLocation getResourceLocation();
    void setResourceLocation(ResourceLocation resourceLocation);
}
