package xox.labvorty.vortylib.data.config.entries;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;
import xox.labvorty.vortylib.gui.widget.ThemedEditBox;

public class IntFieldConfigEntry extends ConfigEntry<Integer> {
    private final ModConfigSpec.ConfigValue<Integer> configValue;
    private final int min;
    private final int max;
    private ThemedEditBox widget;
    private boolean updatingProgrammatically = false;

    public IntFieldConfigEntry(Component label, ModConfigSpec.ConfigValue<Integer> configValue, int defaultValue, int min, int max) {
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
        updateWidgetText(clamped);
    }

    @Override
    public boolean isDefault() {
        return configValue.get().equals(defaultValue);
    }

    @Override
    public AbstractWidget createWidget(int x, int y, int width, int height) {
        widget = new ThemedEditBox(Minecraft.getInstance().font, x, y, width, height, getLabel());
        widget.setMaxLength(11); // covers min/max int plus sign
        widget.setValue(String.valueOf(configValue.get()));

        boolean allowNegative = min < 0;
        widget.setFilter(s -> {
            if (s.isEmpty()) return true;
            if (allowNegative && s.equals("-")) return true;
            int start = (allowNegative && s.startsWith("-")) ? 1 : 0;
            if (start >= s.length()) return false;
            for (int i = start; i < s.length(); i++) {
                if (!Character.isDigit(s.charAt(i))) return false;
            }
            return true;
        });

        widget.setResponder(this::onTextChanged);
        return widget;
    }

    private void onTextChanged(String text) {
        if (updatingProgrammatically) {
            return;
        }
        try {
            int parsed = Integer.parseInt(text);
            int clamped = Math.max(min, Math.min(max, parsed));
            configValue.set(clamped);
            configValue.save();
            if (clamped != parsed) {
                updateWidgetText(clamped);
            }
        } catch (NumberFormatException ignored) {

        }
    }

    private void updateWidgetText(int value) {
        if (widget != null) {
            updatingProgrammatically = true;
            widget.setValue(String.valueOf(value));
            updatingProgrammatically = false;
        }
    }
}