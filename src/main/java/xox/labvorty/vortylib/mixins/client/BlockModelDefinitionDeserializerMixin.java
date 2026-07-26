package xox.labvorty.vortylib.mixins.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.block.model.multipart.MultiPart;
import net.minecraft.util.GsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xox.labvorty.vortylib.data.model.BetterBlockStateUnbakedModel;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Mixin(value = BlockModelDefinition.Deserializer.class)
public abstract class BlockModelDefinitionDeserializerMixin {
    @Inject(
            method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/renderer/block/model/BlockModelDefinition;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void vortylib$deserializeModel(JsonElement jsonElement, Type type, JsonDeserializationContext context, CallbackInfoReturnable<BlockModelDefinition> cir) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        MultiPart multiPart = ((BlockModelDefinitionDeserializerAccessor)(Object)this).vortylib$getMultiPart(context, jsonObject);

        if (jsonObject.has("better_model")) {
            Map<String, MultiVariant> map = new HashMap<>();

            JsonObject jsonobject = GsonHelper.getAsJsonObject(jsonObject, "better_model");

            for(Map.Entry<String, JsonElement> entry : jsonobject.entrySet()) {
                BetterBlockStateUnbakedModel model = new BetterBlockStateUnbakedModel.Deserializer().deserialize(entry.getValue(), BetterBlockStateUnbakedModel.class, context);

                map.put(entry.getKey(), model);
            }

            cir.setReturnValue(new BlockModelDefinition(map, multiPart));
        }
    }
}
