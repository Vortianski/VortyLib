package xox.labvorty.vortylib.data.config.entries;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public abstract class ConfigEntry<T> {
    protected final Component label;
    protected final T defaultValue;
    @Nullable protected Component comment;

    protected ConfigEntry(Component label, T defaultValue) {
        this.label = label;
        this.defaultValue = defaultValue;
    }

    public Component getLabel() {
        return label;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    @Nullable
    public Component getComment() {
        return comment;
    }

    public boolean hasComment() {
        return comment != null;
    }

    public ConfigEntry<T> comment(@Nullable Component comment) {
        this.comment = comment;
        return this;
    }

    public abstract T getValue();

    public abstract void setValue(T value);

    public abstract boolean isDefault();

    public void resetToDefault() {
        setValue(defaultValue);
    }

    public abstract AbstractWidget createWidget(int x, int y, int width, int height);
}