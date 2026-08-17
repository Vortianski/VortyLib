package xox.labvorty.vortylib.mixin_helpers;

import net.minecraft.client.renderer.PostPass;

import java.util.List;

public interface PostChainAccessor {
    List<PostPass> vortylib$getPasses();
}
