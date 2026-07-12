package xox.labvorty.vortylib.data.model;

import com.google.gson.*;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.lang.reflect.Type;

public class BetterBlockStateModelState implements ModelState {
    private final ResourceLocation modelLocation;
    private final Transformation rotation;
    private final boolean uvLock;
    private final int weight;

    public BetterBlockStateModelState(ResourceLocation modelLocation, Transformation rotation, boolean uvLock, int weight) {
        this.modelLocation = modelLocation;
        this.rotation = rotation;
        this.uvLock = uvLock;
        this.weight = weight;
    }

    public ResourceLocation getModelLocation() {
        return this.modelLocation;
    }

    public @NotNull Transformation getRotation() {
        return this.rotation;
    }

    public boolean isUvLocked() {
        return this.uvLock;
    }

    public int getWeight() {
        return this.weight;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Deserializer implements JsonDeserializer<Variant> {
        public Variant deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonobject = json.getAsJsonObject();

            ResourceLocation resourcelocation = this.getModel(jsonobject);
            Transformation transformation = getTransformation(jsonobject);
            boolean flag = this.getUvLock(jsonobject);
            int i = this.getWeight(jsonobject);

            return new Variant(resourcelocation, transformation, flag, i);
        }

        private boolean getUvLock(JsonObject json) {
            return GsonHelper.getAsBoolean(json, "uvlock", false);
        }

        protected Transformation getTransformation(JsonObject jsonObject) {
            float x = snap(GsonHelper.getAsFloat(jsonObject, "x", 0));
            float y = snap(GsonHelper.getAsFloat(jsonObject, "y", 0));
            float z = snap(GsonHelper.getAsFloat(jsonObject, "z", 0));

            Quaternionf rotation = new Quaternionf()
                    .rotateY((float) Math.toRadians(-y))
                    .rotateX((float) Math.toRadians(-x))
                    .rotateZ((float) Math.toRadians(-z));

            return new Transformation(
                    null,
                    rotation,
                    null,
                    null
            );
        }

        protected float snap(float degrees) {
            return Math.round(degrees / 90f) * 90f;
        }

        protected ResourceLocation getModel(JsonObject json) {
            return ResourceLocation.parse(GsonHelper.getAsString(json, "model"));
        }

        protected int getWeight(JsonObject json) {
            int i = GsonHelper.getAsInt(json, "weight", 1);
            if (i < 1) {
                throw new JsonParseException("Invalid weight " + i + " found, expected integer >= 1");
            } else {
                return i;
            }
        }
    }
}
