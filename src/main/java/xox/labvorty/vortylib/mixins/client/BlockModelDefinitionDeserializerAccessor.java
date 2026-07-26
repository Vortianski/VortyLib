package xox.labvorty.vortylib.mixins.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.multipart.MultiPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockModelDefinition.Deserializer.class)
public interface BlockModelDefinitionDeserializerAccessor {
    @Invoker("getMultiPart")
    MultiPart vortylib$getMultiPart(JsonDeserializationContext context, JsonObject object);
}