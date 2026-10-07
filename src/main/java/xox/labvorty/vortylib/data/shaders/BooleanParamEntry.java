package xox.labvorty.vortylib.data.shaders;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.network.chat.Component;

public class BooleanParamEntry extends ShaderParamEntry<Boolean> {

    public BooleanParamEntry(String uniform, Component label, boolean defaultValue) {
        super(uniform, label, defaultValue);
    }

    @Override
    public void apply(Uniform u) {
        u.set(value ? 1 : 0);
    }

    @Override
    public BooleanParamEntry copy() {
        return new BooleanParamEntry(uniform, label, defaultValue);
    }
}