package xox.labvorty.vortylib.data.config;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;
import xox.labvorty.vortylib.data.config.entries.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfigHolder {
    private final ModConfigSpec spec;
    private final List<ConfigEntry<?>> entries;

    private ConfigHolder(ModConfigSpec spec, List<ConfigEntry<?>> entries) {
        this.spec = spec;
        this.entries = entries;
    }

    public ModConfigSpec getSpec() {
        return spec;
    }

    public List<ConfigEntry<?>> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public static Builder builder(ModConfigSpec spec) {
        return new Builder(spec);
    }

    public static class Builder {
        private final ModConfigSpec spec;
        private final List<ConfigEntry<?>> entries = new ArrayList<>();

        private Builder(ModConfigSpec spec) {
            this.spec = spec;
        }

        public Builder addBoolean(Component label, ModConfigSpec.ConfigValue<Boolean> value, boolean defaultValue) {
            return addBoolean(label, null, value, defaultValue);
        }

        public Builder addBoolean(Component label, @Nullable Component comment, ModConfigSpec.ConfigValue<Boolean> value, boolean defaultValue) {
            entries.add(new BooleanConfigEntry(label, value, defaultValue).comment(comment));
            return this;
        }

        public Builder addString(Component label, ModConfigSpec.ConfigValue<String> value, String defaultValue) {
            return addString(label, null, value, defaultValue);
        }

        public Builder addString(Component label, @Nullable Component comment, ModConfigSpec.ConfigValue<String> value, String defaultValue) {
            entries.add(new StringConfigEntry(label, value, defaultValue).comment(comment));
            return this;
        }

        public Builder addInt(Component label, ModConfigSpec.ConfigValue<Integer> value, int defaultValue, int min, int max) {
            return addInt(label, null, value, defaultValue, min, max);
        }

        public Builder addInt(Component label, @Nullable Component comment, ModConfigSpec.ConfigValue<Integer> value, int defaultValue, int min, int max) {
            entries.add(new IntConfigEntry(label, value, defaultValue, min, max).comment(comment));
            return this;
        }

        public Builder addIntField(Component label, ModConfigSpec.ConfigValue<Integer> value, int defaultValue, int min, int max) {
            return addIntField(label, null, value, defaultValue, min, max);
        }

        public Builder addIntField(Component label, @Nullable Component comment, ModConfigSpec.ConfigValue<Integer> value, int defaultValue, int min, int max) {
            entries.add(new IntFieldConfigEntry(label, value, defaultValue, min, max).comment(comment));
            return this;
        }

        public Builder addDouble(Component label, ModConfigSpec.ConfigValue<Double> value, double defaultValue, double min, double max, int decimals) {
            return addDouble(label, null, value, defaultValue, min, max, decimals);
        }

        public Builder addDouble(Component label, @Nullable Component comment, ModConfigSpec.ConfigValue<Double> value, double defaultValue, double min, double max, int decimals) {
            entries.add(new DoubleConfigEntry(label, value, defaultValue, min, max, decimals).comment(comment));
            return this;
        }

        public Builder addItemList(Component label, ModConfigSpec.ConfigValue<List<? extends String>> value, List<String> defaultValue) {
            return addItemList(label, null, value, defaultValue);
        }

        public Builder addItemList(Component label, @Nullable Component comment, ModConfigSpec.ConfigValue<List<? extends String>> value, List<String> defaultValue) {
            entries.add(new ListConfigEntry(label, value, defaultValue).comment(comment));
            return this;
        }

        public Builder addStringList(Component label, ModConfigSpec.ConfigValue<List<? extends String>> value, List<String> defaultValue) {
            return addStringList(label, null, value, defaultValue);
        }

        public Builder addStringList(Component label, @Nullable Component comment, ModConfigSpec.ConfigValue<List<? extends String>> value, List<String> defaultValue) {
            entries.add(new StringListConfigEntry(label, value, defaultValue).comment(comment));
            return this;
        }

        public Builder addEntityList(Component label, ModConfigSpec.ConfigValue<List<? extends String>> value, List<String> defaultValue) {
            return addEntityList(label, null, value, defaultValue);
        }

        public Builder addEntityList(Component label, @Nullable Component comment, ModConfigSpec.ConfigValue<List<? extends String>> value, List<String> defaultValue) {
            entries.add(new EntityListConfigEntry(label, value, defaultValue).comment(comment));
            return this;
        }

        public ConfigHolder build() {
            return new ConfigHolder(spec, entries);
        }
    }
}