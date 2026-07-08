package xox.labvorty.vortylib.data.model;

import com.google.gson.*;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class BetterBlockStateUnbakedModel extends MultiVariant {
    public BetterBlockStateUnbakedModel(List<Variant> modelStates) {
        super(modelStates);
    }

    @Override
    public @Nullable BakedModel bake(@NotNull ModelBaker baker, @NotNull Function<Material, TextureAtlasSprite> spriteGetter, @NotNull ModelState state) {
        if (this.getVariants().isEmpty()) {
            return null;
        } else {
            WeightedBakedModel.Builder builder = new WeightedBakedModel.Builder();

            for(Variant modelState : this.getVariants()) {
                BakedModel bakedmodel = baker.bake(modelState.getModelLocation(), modelState, spriteGetter);
                builder.add(bakedmodel, modelState.getWeight());
            }

            return builder.build();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Deserializer implements JsonDeserializer<BetterBlockStateUnbakedModel> {
        @Override
        public BetterBlockStateUnbakedModel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            List<Variant> modelStates = new ArrayList<>();

            if (jsonElement.isJsonArray()) {
                JsonArray jsonarray = jsonElement.getAsJsonArray();
                if (jsonarray.isEmpty()) {
                    throw new JsonParseException("Empty variant array");
                }

                for (JsonElement jsonelement : jsonarray) {
                    Variant modelState = new BetterBlockStateModelState.Deserializer().deserialize(
                            jsonelement, type, context
                    );

                    modelStates.add(modelState);
                }
            } else {
                Variant modelState = new BetterBlockStateModelState.Deserializer().deserialize(
                        jsonElement, type, context
                );

                modelStates.add(modelState);
            }

            return new BetterBlockStateUnbakedModel(modelStates);
        }
    }
}
