package xox.labvorty.vortylib.mixin.client;

import com.mojang.serialization.*;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xox.labvorty.vortylib.data.text.StyleEffects;
import xox.labvorty.vortylib.data.text.TextEffect;
import xox.labvorty.vortylib.data.text.TextEffects;

import java.util.List;
import java.util.stream.Stream;

@Mixin(Style.Serializer.class)
public class StyleSerializerMixin {
    @Mutable
    @Shadow
    @Final
    public static MapCodec<Style> MAP_CODEC;

    @Mutable
    @Shadow
    @Final
    public static Codec<Style> CODEC;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void vortylib$attachCodec(CallbackInfo ci) {
        MAP_CODEC = vortylib$wrap(MAP_CODEC);
        CODEC = MAP_CODEC.codec();
    }

    @Unique
    private static MapCodec<Style> vortylib$wrap(MapCodec<Style> original) {
        MapCodec<List<String>> specs = Codec.STRING.listOf().optionalFieldOf(TextEffects.EFFECTS_KEY, List.of());
        return new MapCodec<>() {
            @Override
            public <T> DataResult<Style> decode(DynamicOps<T> ops, MapLike<T> input) {
                DataResult<Style> result = original.decode(ops, input);
                if (result.isError()) return result;
                DataResult<List<String>> decoded = specs.decode(ops, input);
                if (decoded.isError() || decoded.getOrThrow().isEmpty()) return result;
                List<TextEffect> effects = TextEffects.parseAll(decoded.getOrThrow());
                if (effects.isEmpty()) return result;
                return result.map(style -> StyleEffects.withEffects(style, effects));
            }

            @Override
            public <T> RecordBuilder<T> encode(Style style, DynamicOps<T> ops, RecordBuilder<T> builder) {
                original.encode(style, ops, builder);
                List<TextEffect> effects = StyleEffects.get(style);
                if (effects.isEmpty()) return builder;
                return specs.encode(TextEffects.serializeAll(effects), ops, builder);
            }

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.concat(original.keys(ops), Stream.of(ops.createString(TextEffects.EFFECTS_KEY)));
            }

            @Override
            public String toString() {
                return original + " (VortyLib text effects)";
            }
        };
    }
}