package xox.labvorty.vortylib.data.config.entries;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import xox.labvorty.vortylib.gui.widget.ThemedToggleButton;

public class BooleanConfigEntry extends ConfigEntry<Boolean> {
    private final ForgeConfigSpec.ConfigValue<Boolean> configValue;
    private ThemedToggleButton widget;

    public BooleanConfigEntry(Component label, ForgeConfigSpec.ConfigValue<Boolean> configValue, boolean defaultValue) {
        super(label, defaultValue);
        this.configValue = configValue;
    }

    @Override
    public Boolean getValue() {
        return configValue.get();
    }

    @Override
    public void setValue(Boolean value) {
        configValue.set(value);
        configValue.save();
        if (widget != null) {
            widget.setValue(value);
        }
    }

    @Override
    public boolean isDefault() {
        return configValue.get().equals(defaultValue);
    }

    @Override
    public AbstractWidget createWidget(int x, int y, int width, int height) {
        widget = new ThemedToggleButton(x, y, width, height, configValue.get(), onPress -> {
            setValue(onPress);
        });

        return widget;
    }
}