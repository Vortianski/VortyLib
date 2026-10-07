package xox.labvorty.vortylib.data.shaders;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.network.chat.Component;

public class IntParamEntry extends ShaderParamEntry<Integer> {
    private final int min;
    private final int max;

    public IntParamEntry(String uniform, Component label, int defaultValue, int min, int max) {
        super(uniform, label, defaultValue);
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    @Override
    public void apply(Uniform u) {
        u.set(value);
    }

    @Override
    public IntParamEntry copy() {
        return new IntParamEntry(uniform, label, defaultValue, min, max);
    }
}