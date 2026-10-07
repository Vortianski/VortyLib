package xox.labvorty.vortylib.data.shaders;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.network.chat.Component;
import org.joml.Vector4f;

public class Vector4fParamEntry extends ShaderParamEntry<Vector4f> {
    public Vector4fParamEntry(String uniform, Component label, Vector4f defaultValue) {
        super(uniform, label, new Vector4f(defaultValue));
    }

    @Override
    public void apply(Uniform u) {
        u.set(value);
    }

    @Override
    public Vector4fParamEntry copy() {
        return new Vector4fParamEntry(uniform, label, defaultValue);
    }
}