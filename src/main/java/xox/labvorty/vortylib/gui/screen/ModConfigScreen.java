package xox.labvorty.vortylib.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import xox.labvorty.vortylib.configs.ClientConfig;
import xox.labvorty.vortylib.data.config.ConfigHolder;
import xox.labvorty.vortylib.data.config.ModEntry;
import xox.labvorty.vortylib.data.config.entries.ConfigEntry;
import xox.labvorty.vortylib.gui.theme.PanoramaTheme;
import xox.labvorty.vortylib.gui.theme.UiTheme;
import xox.labvorty.vortylib.gui.widget.ThemedButton;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModConfigScreen extends Screen {
    private static final int ROW_HEIGHT = 24;
    private static final int WIDGET_WIDTH = 110;
    private static final int RESET_WIDTH = 20;
    private static final int RESET_HEIGHT = 20;
    private static final int GAP = 4;
    private static final int MAX_TOOLTIP_WIDTH = 200;

    private final Screen parent;
    private final ModEntry modEntry;
    @Nullable private final ConfigHolder holder;

    private EntryList list;

    public ModConfigScreen(Screen parent, ModEntry modEntry, @Nullable ConfigHolder holder) {
        super(modEntry.getDisplayName());
        this.parent = parent;
        this.modEntry = modEntry;
        this.holder = holder;
    }

    @Override
    protected void init() {
        this.list = new EntryList(this.minecraft, this.width, this.height - 64, 32, ROW_HEIGHT);
        if (holder != null) {
            for (ConfigEntry<?> entry : holder.getEntries()) {
                this.list.add(new ConfigRow(entry));
            }
        }
        this.addRenderableWidget(this.list);

        this.addRenderableWidget(ThemedButton.builder(CommonComponents.GUI_DONE, b -> this.minecraft.setScreen(parent))
                .bounds(this.width / 2 - 100, this.height - 26, 200, 20)
                .build());
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        VortyLibBackground.render(guiGraphics, this.width, this.height, partialTick,
                () -> super.renderBackground(guiGraphics, mouseX, mouseY, partialTick));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);

        // Render tooltips after everything else
        ConfigRow hovered = list.getHoveredEntry(mouseX, mouseY);
        if (hovered != null) {
            hovered.renderTooltip(guiGraphics, mouseX, mouseY);
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.list.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.list.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.list.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    /**
     * Splits a comment on literal "\n" breaks, then greedily word-wraps each resulting
     * paragraph to MAX_TOOLTIP_WIDTH pixels, mirroring the plainSubstrByWidth-based
     * truncation already used in ItemListScreen/EntityListScreen row rendering.
     */
    private static List<Component> wrapTooltip(Component raw) {
        List<Component> result = new ArrayList<>();
        Font font = Minecraft.getInstance().font;

        for (String paragraph : raw.getString().split("\n", -1)) {
            if (paragraph.isEmpty()) {
                result.add(Component.empty());
                continue;
            }

            StringBuilder current = new StringBuilder();
            for (String word : paragraph.split(" ")) {
                String candidate = current.isEmpty() ? word : current + " " + word;
                if (!current.isEmpty() && font.width(candidate) > MAX_TOOLTIP_WIDTH) {
                    result.add(Component.literal(current.toString()).withStyle(raw.getStyle()));
                    current = new StringBuilder(word);
                } else {
                    current = new StringBuilder(candidate);
                }
            }
            if (!current.isEmpty()) {
                result.add(Component.literal(current.toString()).withStyle(raw.getStyle()));
            }
        }

        return result;
    }

    private static class EntryList extends ObjectSelectionList<ConfigRow> {
        private boolean scrollDragStarted = false;
        @Nullable private ConfigRow draggingRow;
        @Nullable private ConfigRow focusedRow;

        EntryList(Minecraft mc, int width, int height, int top, int itemHeight) {
            super(mc, width, height, top, itemHeight);
        }

        void add(ConfigRow row) {
            this.addEntry(row);
        }

        @Nullable
        public ConfigRow getHoveredEntry(double mouseX, double mouseY) {
            return this.getEntryAtPosition(mouseX, mouseY);
        }

        @Override
        public int getRowWidth() {
            return this.width - 16;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            scrollDragStarted = isOverScrollbar(mouseX, mouseY);

            if (scrollDragStarted) {
                draggingRow = null;
                return super.mouseClicked(mouseX, mouseY, button);
            }

            ConfigRow hovered = getEntryAtPosition(mouseX, mouseY);
            if (hovered != null && hovered.mouseClicked(mouseX, mouseY, button)) {
                draggingRow = hovered;
                if (focusedRow != null && focusedRow != hovered) {
                    focusedRow.clearFocus();
                }
                focusedRow = hovered;
                return true;
            }

            if (focusedRow != null) {
                focusedRow.clearFocus();
                focusedRow = null;
            }

            draggingRow = null;
            return false;
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
            if (scrollDragStarted) {
                return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
            }

            if (draggingRow != null) {
                return draggingRow.mouseDragged(mouseX, mouseY, button, dragX, dragY);
            }

            return false;
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            if (draggingRow != null) {
                draggingRow.mouseReleased(mouseX, mouseY, button);
            }
            scrollDragStarted = false;
            draggingRow = null;
            return super.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
            if (!this.isMouseOver(mouseX, mouseY)) {
                return false;
            }
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }

        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return focusedRow != null && focusedRow.keyPressed(keyCode, scanCode, modifiers);
        }

        public boolean charTyped(char codePoint, int modifiers) {
            return focusedRow != null && focusedRow.charTyped(codePoint, modifiers);
        }

        private boolean isOverScrollbar(double mouseX, double mouseY) {
            int scrollX = this.getScrollbarPosition();
            return mouseX >= scrollX && mouseX <= scrollX + 6
                    && mouseY >= getY() && mouseY <= getY() + getHeight();
        }
    }

    private static class ConfigRow extends ObjectSelectionList.Entry<ConfigRow> {
        private final ConfigEntry<?> configEntry;
        private final AbstractWidget widget;
        private final ThemedButton resetButton;

        private int labelAreaX, labelAreaY, labelAreaW, labelAreaH;
        private int sliderAreaX, sliderAreaY, sliderAreaW, sliderAreaH;

        ConfigRow(ConfigEntry<?> configEntry) {
            this.configEntry = configEntry;
            this.widget = configEntry.createWidget(0, 0, WIDGET_WIDTH, RESET_HEIGHT);
            this.resetButton = ThemedButton.builder(Component.literal("R"), b -> configEntry.resetToDefault())
                    .size(RESET_WIDTH, RESET_HEIGHT)
                    .build();
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            int rightEdge = left + width - 4;
            int resetX = rightEdge - RESET_WIDTH;
            int widgetX = resetX - GAP - WIDGET_WIDTH;

            String labelText = configEntry.getLabel().getString();
            Component label = Component.literal(labelText);
            int labelWidth = Minecraft.getInstance().font.width(label);
            int labelX = left + 2;

            guiGraphics.drawString(Minecraft.getInstance().font, label, labelX, top + height / 2 - 4, 0xFFFFFF);

            widget.setPosition(widgetX, top + (height - RESET_HEIGHT) / 2);
            widget.render(guiGraphics, mouseX, mouseY, partialTick);

            resetButton.setPosition(resetX, top + (height - RESET_HEIGHT) / 2);
            resetButton.active = !configEntry.isDefault();
            resetButton.render(guiGraphics, mouseX, mouseY, partialTick);

            labelAreaX = labelX;
            labelAreaY = top + height / 2 - 4;
            labelAreaW = labelWidth;
            labelAreaH = 8;

            sliderAreaX = widgetX;
            sliderAreaY = top + (height - RESET_HEIGHT) / 2;
            sliderAreaW = WIDGET_WIDTH;
            sliderAreaH = RESET_HEIGHT;
        }

        public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
            boolean overLabel = mouseX >= labelAreaX && mouseX <= labelAreaX + labelAreaW
                    && mouseY >= labelAreaY && mouseY <= labelAreaY + labelAreaH;
            boolean overSlider = mouseX >= sliderAreaX && mouseX <= sliderAreaX + sliderAreaW
                    && mouseY >= sliderAreaY && mouseY <= sliderAreaY + sliderAreaH;

            if (overLabel && configEntry.hasComment()) {
                guiGraphics.renderTooltip(
                        Minecraft.getInstance().font,
                        wrapTooltip(configEntry.getComment()),
                        Optional.empty(),
                        mouseX, mouseY
                );
            } else if (overSlider) {
                String valueStr = configEntry.getValue().toString();
                guiGraphics.renderTooltip(
                        Minecraft.getInstance().font,
                        Component.literal(valueStr),
                        mouseX, mouseY
                );
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (resetButton.mouseClicked(mouseX, mouseY, button)) {
                widget.setFocused(false);
                return true;
            }
            if (widget.mouseClicked(mouseX, mouseY, button)) {
                widget.setFocused(true);
                return true;
            }
            widget.setFocused(false);
            return false;
        }

        public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
            return widget.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }

        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return widget.mouseReleased(mouseX, mouseY, button);
        }

        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return widget.isFocused() && widget.keyPressed(keyCode, scanCode, modifiers);
        }

        public boolean charTyped(char codePoint, int modifiers) {
            return widget.isFocused() && widget.charTyped(codePoint, modifiers);
        }

        public void clearFocus() {
            widget.setFocused(false);
        }

        @Override
        public Component getNarration() {
            return configEntry.getLabel();
        }
    }

    @Override
    public void tick() {
        super.tick();

        UiTheme.setCurrent(ClientConfig.UI_THEME.get());
        PanoramaTheme.setCurrent(ClientConfig.PANORAMA_THEME.get());
    }
}