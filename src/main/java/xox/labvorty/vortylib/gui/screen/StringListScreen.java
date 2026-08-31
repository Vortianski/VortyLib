package xox.labvorty.vortylib.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import xox.labvorty.vortylib.configs.ClientConfig;
import xox.labvorty.vortylib.data.config.entries.StringListConfigEntry;
import xox.labvorty.vortylib.gui.theme.PanoramaTheme;
import xox.labvorty.vortylib.gui.theme.UiTheme;
import xox.labvorty.vortylib.gui.widget.ThemedButton;

public class StringListScreen extends Screen {
    private static final int ROW_HEIGHT = 24;
    private static final int MARGIN_X = 20;
    private static final int REMOVE_WIDTH = 56;

    private final StringListConfigEntry entry;
    private final Screen parentScreen;
    private CurrentStringList list;

    public StringListScreen(StringListConfigEntry entry) {
        super(Component.literal(entry.getLabel().getString()));
        this.entry = entry;
        this.parentScreen = Minecraft.getInstance().screen;
    }

    @Override
    protected void init() {
        int listTop = 40;
        int listHeight = this.height - 100;
        int listWidth = this.width - MARGIN_X * 2;

        this.list = new CurrentStringList(this.minecraft, listWidth, listHeight, listTop, ROW_HEIGHT);
        refreshList();
        this.addRenderableWidget(this.list);

        this.addRenderableWidget(ThemedButton.builder(Component.literal("+"), b -> openAddScreen())
                .bounds(this.width / 2 + 10, this.height - 36, 90, 20)
                .build());

        this.addRenderableWidget(ThemedButton.builder(CommonComponents.GUI_DONE, b -> onClose())
                .bounds(this.width / 2 - 100, this.height - 36, 90, 20)
                .build());
    }

    private void refreshList() {
        list.clear();
        for (String value : entry.getValue()) {
            list.add(new CurrentStringRow(value));
        }

        if (!list.children().isEmpty()) {
            list.setScrollAmount(list.getMaxScroll());
        }
    }

    private void openAddScreen() {
        if (minecraft != null) {
            this.minecraft.setScreen(new StringEntryScreen(this, entry));
        }
    }

    public void onEntryAdded() {
        refreshList();
    }

    public void onEntryRemoved() {
        refreshList();
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        VortyLibBackground.render(guiGraphics, this.width, this.height, partialTick,
                () -> super.renderBackground(guiGraphics, mouseX, mouseY, partialTick));
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, Component.literal(entry.getValue().size() + " entries"), this.width / 2, 26, 0xAAAAAA);
    }

    @Override
    public void onClose() {
        if (minecraft != null) {
            this.minecraft.setScreen(parentScreen);
        }
    }

    private class CurrentStringList extends ObjectSelectionList<CurrentStringRow> {
        CurrentStringList(Minecraft mc, int width, int height, int top, int itemHeight) {
            super(mc, width, height, top, itemHeight);
            this.setX(MARGIN_X);
        }

        @Override
        public int getRowWidth() {
            return this.width - 12;
        }

        @Override
        public int getScrollbarPosition() {
            return this.getX() + this.width - 6;
        }

        void add(CurrentStringRow row) {
            this.addEntry(row);
        }

        void clear() {
            this.clearEntries();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            CurrentStringRow clicked = this.getEntryAtPosition(mouseX, mouseY);
            if (clicked != null && clicked.removeButton.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    private class CurrentStringRow extends ObjectSelectionList.Entry<CurrentStringRow> {
        private final String value;
        private final ThemedButton removeButton;

        CurrentStringRow(String value) {
            this.value = value;
            this.removeButton = ThemedButton.builder(Component.literal("Remove"), b -> {
                        entry.removeEntry(value);
                        onEntryRemoved();
                    })
                    .size(REMOVE_WIDTH, 20)
                    .build();
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            Component text = Component.literal(value);
            int maxWidth = width - REMOVE_WIDTH - 16;
            int textWidth = Minecraft.getInstance().font.width(text);
            if (textWidth > maxWidth) {
                text = Component.literal(Minecraft.getInstance().font.plainSubstrByWidth(value, maxWidth) + "...");
            }

            guiGraphics.drawString(Minecraft.getInstance().font, text, left + 6, top + (height - 8) / 2, 0xFFFFFF);

            int removeX = left + width - REMOVE_WIDTH - 6;
            int removeY = top + (height - 20) / 2;
            removeButton.setPosition(removeX, removeY);
            removeButton.render(guiGraphics, mouseX, mouseY, partialTick);

            boolean overRemove = removeButton.isMouseOver(mouseX, mouseY);
            if (hovering && !overRemove) {
                guiGraphics.fill(left, top, left + width, top + height, 0x20FFFFFF);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return false;
        }

        @Override
        public @NotNull Component getNarration() {
            return Component.literal(value);
        }
    }

    @Override
    public void tick() {
        super.tick();

        UiTheme.setCurrent(ClientConfig.UI_THEME.get());
        PanoramaTheme.setCurrent(ClientConfig.PANORAMA_THEME.get());
    }
}