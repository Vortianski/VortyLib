package xox.labvorty.vortylib.data.config.entries;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import xox.labvorty.vortylib.gui.widget.ThemedEditBox;

public class StringConfigEntry extends ConfigEntry<String> {
    private final ForgeConfigSpec.ConfigValue<String> configValue;
    private ThemedEditBox widget;

    public StringConfigEntry(Component label, ForgeConfigSpec.ConfigValue<String> configValue, String defaultValue) {
        super(label, defaultValue);
        this.configValue = configValue;
    }

    @Override
    public String getValue() {
        return configValue.get();
    }

    @Override
    public void setValue(String value) {
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
        widget = new ThemedEditBox(Minecraft.getInstance().font, x, y, width, height, label);
        widget.setValue(configValue.get());
        widget.setResponder(this::setValue);

        return widget;
    }
}