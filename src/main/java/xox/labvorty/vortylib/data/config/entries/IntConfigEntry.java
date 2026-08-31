package xox.labvorty.vortylib.data.config.entries;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;
import xox.labvorty.vortylib.gui.widget.ThemedSliderBase;

public class IntConfigEntry extends ConfigEntry<Integer> {
    private final ModConfigSpec.ConfigValue<Integer> configValue;
    private final int min;
    private final int max;
    private IntSlider widget;

    public IntConfigEntry(Component label, ModConfigSpec.ConfigValue<Integer> configValue, int defaultValue, int min, int max) {
        super(label, defaultValue);
        this.configValue = configValue;
        this.min = min;
        this.max = max;
    }

    @Override
    public Integer getValue() {
        return configValue.get();
    }

    @Override
    public void setValue(Integer value) {
        int clamped = Math.max(min, Math.min(max, value));
        configValue.set(clamped);
        configValue.save();
        if (widget != null) {
            widget.updateValue(clamped);
        }
    }

    @Override
    public boolean isDefault() {
        return configValue.get().equals(defaultValue);
    }

    @Override
    public AbstractWidget createWidget(int x, int y, int width, int height) {
        widget = new IntSlider(x, y, width, height, configValue.get());
        return widget;
    }

    private class IntSlider extends ThemedSliderBase {
        IntSlider(int x, int y, int width, int height, int initial) {
            super(x, y, width, height, (double) (initial - min) / (max - min));
        }

        @Override
        protected void applyValue() {
            int currentValue = min + (int) Math.round(this.value * (max - min));
            configValue.set(currentValue);
            configValue.save();
        }

        void updateValue(int newValue) {
            this.value = (double) (newValue - min) / (max - min);
            updateMessage();
        }
    }
}