package xox.labvorty.vortylib.data.config.entries;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;
import xox.labvorty.vortylib.gui.screen.EntityListScreen;
import xox.labvorty.vortylib.gui.widget.ThemedButton;

import java.util.ArrayList;
import java.util.List;

public class EntityListConfigEntry extends ConfigEntry<List<String>> {
    private final ModConfigSpec.ConfigValue<List<? extends String>> configValue;
    private ThemedButton widgetButton;

    public EntityListConfigEntry(Component label, ModConfigSpec.ConfigValue<List<? extends String>> configValue, List<String> defaultValue) {
        super(label, defaultValue);
        this.configValue = configValue;
    }

    @Override
    public List<String> getValue() {
        return new ArrayList<>(configValue.get());
    }

    @Override
    public void setValue(List<String> value) {
        configValue.set(value);
        configValue.save();
        updateButtonText();
    }

    @Override
    public boolean isDefault() {
        return configValue.get().equals(defaultValue);
    }

    @Override
    public AbstractWidget createWidget(int x, int y, int width, int height) {
        widgetButton = ThemedButton.builder(Component.literal("Edit"), b ->
                        Minecraft.getInstance().setScreen(new EntityListScreen(this)))
                .bounds(x, y, width, height)
                .build();
        updateButtonText();
        return widgetButton;
    }

    private void updateButtonText() {
        if (widgetButton != null) {
            widgetButton.setMessage(Component.literal("Edit (" + getValue().size() + ")"));
        }
    }

    public void addItem(String entityId) {
        List<String> current = getValue();
        if (!current.contains(entityId)) {
            current.add(entityId);
            setValue(current);
        }
    }

    public void removeItem(String entityId) {
        List<String> current = getValue();
        current.remove(entityId);
        setValue(current);
    }
}