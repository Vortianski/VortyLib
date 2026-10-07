package xox.labvorty.vortylib.data.shaders;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.network.chat.Component;

public class FloatParamEntry extends ShaderParamEntry<Float> {
    private final float min;
    private final float max;

    public FloatParamEntry(String uniform, Component label, float defaultValue, float min, float max) {
        super(uniform, label, defaultValue);
        this.min = min;
        this.max = max;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    @Override
    public void apply(Uniform u) {
        u.set(value);
    }

    @Override
    public FloatParamEntry copy() {
        return new FloatParamEntry(uniform, label, defaultValue, min, max);
    }
}