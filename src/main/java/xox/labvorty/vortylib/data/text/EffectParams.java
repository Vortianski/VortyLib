package xox.labvorty.vortylib.data.text;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class EffectParams {
    public static final EffectParams EMPTY = new EffectParams(Map.of());
    private final Map<String, String> values;

    private EffectParams(Map<String, String> values) {
        this.values = values;
    }

    public static EffectParams of(String... keysAndValues) {
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i + 1 < keysAndValues.length; i += 2) {
            map.put(keysAndValues[i], keysAndValues[i + 1]);
        }
        return map.isEmpty() ? EMPTY : new EffectParams(Collections.unmodifiableMap(map));
    }

    public static EffectParams parse(String text) {
        Map<String, String> map = new LinkedHashMap<>();
        for (String token : text.trim().split("\\s+")) {
            if (token.isEmpty()) continue;
            int eq = token.indexOf('=');
            if (eq > 0) {
                map.put(token.substring(0, eq), token.substring(eq + 1));
            } else {
                map.put(token, "true");
            }
        }
        return map.isEmpty() ? EMPTY : new EffectParams(Collections.unmodifiableMap(map));
    }

    public float f(String key, float def) {
        String v = values.get(key);
        if (v == null) return def;
        try {
            float parsed = Float.parseFloat(v);
            return Float.isFinite(parsed) ? parsed : def;
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public boolean bool(String key, boolean def) {
        String v = values.get(key);
        return v == null ? def : Boolean.parseBoolean(v);
    }

    public int color(String key, int def) {
        String v = values.get(key);
        if (v == null) return def;
        if (v.startsWith("#")) v = v.substring(1);
        try {
            return Integer.parseInt(v, 16) & 0xFFFFFF;
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public String string(String key, String def) {
        return values.getOrDefault(key, def);
    }

    public String serialize() {
        if (values.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        values.forEach((k, v) -> sb.append(' ').append(k).append('=').append(v));
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof EffectParams other && values.equals(other.values);
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public String toString() {
        return serialize().trim();
    }
}