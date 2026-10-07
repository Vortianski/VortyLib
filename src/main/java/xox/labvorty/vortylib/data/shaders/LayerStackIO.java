package xox.labvorty.vortylib.data.shaders;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class LayerStackIO {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private LayerStackIO() {
    }

    public static void save(Path file) {
        JsonArray root = new JsonArray();
        for (ActiveLayer layer : PostChainManager.INSTANCE.getLayers()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("id", layer.getId().toString());

            JsonObject values = new JsonObject();
            for (ShaderParamEntry<?> param : layer.getParams()) {
                Object v = param.getValue();
                if (v instanceof Number n) {
                    values.addProperty(param.getUniform(), n);
                } else if (v instanceof Boolean b) {
                    values.addProperty(param.getUniform(), b);
                }
            }
            obj.add("params", values);
            root.add(obj);
        }

        try {
            if (file.getParent() != null) Files.createDirectories(file.getParent());
            Files.writeString(file, GSON.toJson(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load(Path file) {
        if (!Files.exists(file)) return;

        try (var reader = Files.newBufferedReader(file)) {
            JsonArray root = GSON.fromJson(reader, JsonArray.class);
            if (root == null) return;

            for (JsonElement el : root) {
                JsonObject obj = el.getAsJsonObject();
                ResourceLocation id = ResourceLocation.parse(obj.get("id").getAsString());
                PostChainManager.INSTANCE.addLayer(id);

                JsonObject values = obj.getAsJsonObject("params");
                PostChainManager.INSTANCE.getLayer(id).ifPresent(layer -> {
                    for (ShaderParamEntry<?> param : layer.getParams()) {
                        if (values != null && values.has(param.getUniform())) {
                            applyLoadedValue(param, values.get(param.getUniform()));
                        }
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void applyLoadedValue(ShaderParamEntry<?> param, JsonElement raw) {
        if (param instanceof FloatParamEntry f) {
            f.setValue(raw.getAsFloat());
        } else if (param instanceof IntParamEntry i) {
            i.setValue(raw.getAsInt());
        } else if (param instanceof BooleanParamEntry b) {
            b.setValue(raw.getAsBoolean());
        }
        // Vector/matrix params: left at their registered defaults for now.
    }
}