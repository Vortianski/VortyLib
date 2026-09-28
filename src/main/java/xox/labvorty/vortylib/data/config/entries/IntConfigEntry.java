package xox.labvorty.vortylib.data.config.entries;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.Nullable;
import xox.labvorty.vortylib.gui.widget.ThemedSliderBase;

import javax.tools.Tool;

public class IntConfigEntry extends ConfigEntry<Integer> {
    private final ForgeConfigSpec.ConfigValue<Integer> configValue;
    private final int min;
    private final int max;
    private IntSlider widget;

    public IntConfigEntry(Component label, ForgeConfigSpec.ConfigValue<Integer> configValue, int defaultValue, int min, int max) {
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
    public void save() {
        super.save();
        configValue.set(widget.pendingValue);
        configValue.save();
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

    public class IntSlider extends ThemedSliderBase {
        public int pendingValue;

        IntSlider(int x, int y, int width, int height, int initial) {
            super(x, y, width, height, (double) (initial - min) / (max - min));
            pendingValue = configValue.get();
        }

        @Override
        protected void applyValue() {
            int currentValue = min + (int) Math.round(this.value * (max - min));
            pendingValue = currentValue;
        }

        void updateValue(int newValue) {
            this.value = (double) (newValue - min) / (max - min);
            updateMessage();
        }

        public int getPendingValue() {
            return pendingValue;
        }
    }
}