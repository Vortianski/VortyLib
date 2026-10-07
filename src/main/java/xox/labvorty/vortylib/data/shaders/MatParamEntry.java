package xox.labvorty.vortylib.data.shaders;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class MatParamEntry<T> extends ShaderParamEntry<T> {
    private final Supplier<T> factory;
    private final MatApplier<T> applier;

    public MatParamEntry(String uniform, Component label, Supplier<T> factory, MatApplier<T> applier) {
        super(uniform, label, factory.get());
        this.factory = factory;
        this.applier = applier;
    }

    @Override
    public void apply(Uniform u) {
        applier.apply(u, value);
    }

    @Override
    public MatParamEntry<T> copy() {
        return new MatParamEntry<>(uniform, label, factory, applier);
    }
}