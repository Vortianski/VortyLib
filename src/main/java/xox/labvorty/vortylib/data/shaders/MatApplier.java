package xox.labvorty.vortylib.data.shaders;

import com.mojang.blaze3d.shaders.Uniform;

@FunctionalInterface
public interface MatApplier<T> {
    void apply(Uniform uniform, T value);
}