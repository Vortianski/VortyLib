package xox.labvorty.vortylib.data.shaders;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.network.chat.Component;

public abstract class ShaderParamEntry<T> {
    protected final String uniform;
    protected final Component label;
    protected final T defaultValue;
    protected T value;

    protected ShaderParamEntry(String uniform, Component label, T defaultValue) {
        this.uniform = uniform;
        this.label = label;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public String getUniform() {
        return uniform;
    }

    public Component getLabel() {
        return label;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void resetToDefault() {
        this.value = defaultValue;
    }

    public abstract void apply(Uniform u);

    public abstract ShaderParamEntry<T> copy();
}