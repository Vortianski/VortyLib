package xox.labvorty.vortylib.data.shaders;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.network.chat.Component;
import org.joml.Vector3f;

public class Vector3fParamEntry extends ShaderParamEntry<Vector3f> {

    public Vector3fParamEntry(String uniform, Component label, Vector3f defaultValue) {
        super(uniform, label, new Vector3f(defaultValue));
    }

    @Override
    public void apply(Uniform u) {
        u.set(value);
    }

    @Override
    public Vector3fParamEntry copy() {
        return new Vector3fParamEntry(uniform, label, defaultValue);
    }
}