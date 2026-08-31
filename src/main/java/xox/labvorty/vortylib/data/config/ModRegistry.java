package xox.labvorty.vortylib.data.config;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ModRegistry {

    private static final Map<String, ModEntry> ENTRIES = new LinkedHashMap<>();

    private ModRegistry() {}

    public static void register(ModEntry entry) {
        ENTRIES.put(entry.getModId(), entry);
    }

    public static Collection<ModEntry> getAll() {
        return ENTRIES.values();
    }
}