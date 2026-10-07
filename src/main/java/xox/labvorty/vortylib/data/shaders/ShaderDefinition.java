package xox.labvorty.vortylib.data.shaders;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class ShaderDefinition {
    private final ResourceLocation id;
    private final Component label;
    private final List<ShaderParamEntry<?>> template;

    private ShaderDefinition(ResourceLocation id, Component label, List<ShaderParamEntry<?>> template) {
        this.id = id;
        this.label = label;
        this.template = template;
    }

    public ResourceLocation getId() {
        return id;
    }

    public Component getLabel() {
        return label;
    }

    public List<ShaderParamEntry<?>> getTemplate() {
        return Collections.unmodifiableList(template);
    }

    public List<ShaderParamEntry<?>> instantiateParams() {
        List<ShaderParamEntry<?>> copies = new ArrayList<>(template.size());
        for (ShaderParamEntry<?> entry : template) {
            copies.add(entry.copy());
        }
        return copies;
    }

    public static Builder builder(ResourceLocation id, Component label) {
        return new Builder(id, label);
    }

    public static class Builder {
        private final ResourceLocation id;
        private final Component label;
        private final List<ShaderParamEntry<?>> entries = new ArrayList<>();

        private Builder(ResourceLocation id, Component label) {
            this.id = id;
            this.label = label;
        }

        public Builder addFloat(String uniform, Component label, float defaultValue, float min, float max) {
            entries.add(new FloatParamEntry(uniform, label, defaultValue, min, max));
            return this;
        }

        public Builder addInt(String uniform, Component label, int defaultValue, int min, int max) {
            entries.add(new IntParamEntry(uniform, label, defaultValue, min, max));
            return this;
        }

        public Builder addBoolean(String uniform, Component label, boolean defaultValue) {
            entries.add(new BooleanParamEntry(uniform, label, defaultValue));
            return this;
        }

        public Builder addVector3f(String uniform, Component label, Vector3f defaultValue) {
            entries.add(new Vector3fParamEntry(uniform, label, defaultValue));
            return this;
        }

        public Builder addVector4f(String uniform, Component label, Vector4f defaultValue) {
            entries.add(new Vector4fParamEntry(uniform, label, defaultValue));
            return this;
        }

        public Builder addMatrix3f(String uniform, Component label, Matrix3f defaultValue) {
            entries.add(new Matrix3fParamEntry(uniform, label, defaultValue));
            return this;
        }

        public Builder addMatrix4f(String uniform, Component label, Matrix4f defaultValue) {
            entries.add(new Matrix4fParamEntry(uniform, label, defaultValue));
            return this;
        }

        public Builder addFloat2(String uniform, Component label, Vector2f defaultValue) {
            entries.add(new Float2ParamEntry(uniform, label, defaultValue));
            return this;
        }

        public <T> Builder addMat(String uniform, Component label, Supplier<T> factory, MatApplier<T> applier) {
            entries.add(new MatParamEntry<>(uniform, label, factory, applier));
            return this;
        }

        public ShaderDefinition build() {
            return new ShaderDefinition(id, label, entries);
        }
    }
}