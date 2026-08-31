package xox.labvorty.vortylib.data.config.entries;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;
import xox.labvorty.vortylib.gui.widget.ThemedSliderBase;

public class DoubleConfigEntry extends ConfigEntry<Double> {
    private final ModConfigSpec.ConfigValue<Double> configValue;
    private final double min;
    private final double max;
    private final int decimals;
    private DoubleSlider widget;

    public DoubleConfigEntry(Component label, ModConfigSpec.ConfigValue<Double> configValue, double defaultValue, double min, double max, int decimals) {
        super(label, defaultValue);
        this.configValue = configValue;
        this.min = min;
        this.max = max;
        this.decimals = decimals;
    }

    @Override
    public Double getValue() {
        return configValue.get();
    }

    @Override
    public void setValue(Double value) {
        double clamped = Math.max(min, Math.min(max, value));
        configValue.set(clamped);
        configValue.save();
        if (widget != null) {
            widget.updateValue(clamped);
        }
    }

    @Override
    public boolean isDefault() {
        return Double.compare(configValue.get(), defaultValue) == 0;
    }

    @Override
    public AbstractWidget createWidget(int x, int y, int width, int height) {
        widget = new DoubleSlider(x, y, width, height, configValue.get());
        return widget;
    }

    private class DoubleSlider extends ThemedSliderBase {
        DoubleSlider(int x, int y, int width, int height, double initial) {
            super(x, y, width, height, (initial - min) / (max - min));
        }

        @Override
        protected void applyValue() {
            double raw = min + this.value * (max - min);
            double currentValue = round(raw);
            configValue.set(currentValue);
            configValue.save();
        }

        void updateValue(double newValue) {
            this.value = (round(newValue) - min) / (max - min);
            updateMessage();
        }

        private double round(double v) {
            double factor = Math.pow(10, decimals);
            return Math.round(v * factor) / factor;
        }
    }
}