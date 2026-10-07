package xox.labvorty.vortylib.data.shaders;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.network.chat.Component;
import org.joml.Matrix3f;

public class Matrix3fParamEntry extends ShaderParamEntry<Matrix3f> {

    public Matrix3fParamEntry(String uniform, Component label, Matrix3f defaultValue) {
        super(uniform, label, new Matrix3f(defaultValue));
    }

    @Override
    public void apply(Uniform u) {
        u.set(value);
    }

    @Override
    public Matrix3fParamEntry copy() {
        return new Matrix3fParamEntry(uniform, label, defaultValue);
    }
}