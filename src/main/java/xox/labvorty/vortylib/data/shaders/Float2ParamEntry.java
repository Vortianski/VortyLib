package xox.labvorty.vortylib.data.shaders;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.network.chat.Component;
import org.joml.Vector2f;

public class Float2ParamEntry extends ShaderParamEntry<Vector2f> {
    public Float2ParamEntry(String uniform, Component label, Vector2f defaultValue) {
        super(uniform, label, defaultValue);
    }

    @Override
    public void apply(Uniform u) {
        u.set(value.x, value.y);
    }

    @Override
    public ShaderParamEntry<Vector2f> copy() {
        return new Float2ParamEntry(uniform, label, defaultValue);
    }
}
