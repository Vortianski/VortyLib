package xox.labvorty.vortylib.data.shaders;

import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class ShaderParamRegistry {
    private static final Map<ResourceLocation, ShaderDefinition> DEFINITIONS = new LinkedHashMap<>();

    private ShaderParamRegistry() {
    }

    public static void register(ShaderDefinition definition) {
        DEFINITIONS.put(definition.getId(), definition);
    }

    public static Optional<ShaderDefinition> get(ResourceLocation id) {
        return Optional.ofNullable(DEFINITIONS.get(id));
    }

    public static boolean isRegistered(ResourceLocation id) {
        return DEFINITIONS.containsKey(id);
    }

    public static Map<ResourceLocation, ShaderDefinition> getAll() {
        return Collections.unmodifiableMap(DEFINITIONS);
    }
}