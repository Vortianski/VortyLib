package xox.labvorty.vortylib.data.shaders;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;

public class Matrix4fParamEntry extends ShaderParamEntry<Matrix4f> {

    public Matrix4fParamEntry(String uniform, Component label, Matrix4f defaultValue) {
        super(uniform, label, new Matrix4f(defaultValue));
    }

    @Override
    public void apply(Uniform u) {
        u.set(value);
    }

    @Override
    public Matrix4fParamEntry copy() {
        return new Matrix4fParamEntry(uniform, label, defaultValue);
    }
}