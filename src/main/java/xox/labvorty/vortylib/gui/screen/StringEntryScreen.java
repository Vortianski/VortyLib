package xox.labvorty.vortylib.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import xox.labvorty.vortylib.configs.ClientConfig;
import xox.labvorty.vortylib.data.config.entries.StringListConfigEntry;
import xox.labvorty.vortylib.gui.theme.PanoramaTheme;
import xox.labvorty.vortylib.gui.theme.UiTheme;
import xox.labvorty.vortylib.gui.widget.ThemedButton;
import xox.labvorty.vortylib.gui.widget.ThemedEditBox;

public class StringEntryScreen extends Screen {
    private final StringListScreen parentScreen;
    private final StringListConfigEntry entry;
    private ThemedEditBox inputBox;
    private ThemedButton addButton;

    public StringEntryScreen(StringListScreen parentScreen, StringListConfigEntry entry) {
        super(Component.translatable("vortylib.string_entry.add_entry"));
        this.parentScreen = parentScreen;
        this.entry = entry;
    }

    @Override
    protected void init() {
        this.inputBox = new ThemedEditBox(this.font, this.width / 2 - 100, this.height / 2 - 30, 200, 20, Component.literal("Value"));
        this.inputBox.setResponder(s -> updateAddButtonState());
        this.addRenderableWidget(this.inputBox);
        this.setInitialFocus(this.inputBox);

        this.addButton = ThemedButton.builder(Component.literal("Add"), b -> addEntry())
                .bounds(this.width / 2 - 100, this.height / 2, 200, 20)
                .build();
        this.addRenderableWidget(this.addButton);

        this.addRenderableWidget(ThemedButton.builder(CommonComponents.GUI_CANCEL, b -> onClose())
                .bounds(this.width / 2 - 100, this.height / 2 + 26, 200, 20)
                .build());

        updateAddButtonState();
    }

    private void updateAddButtonState() {
        if (addButton != null) {
            addButton.active = !inputBox.getValue().isBlank();
        }
    }

    private void addEntry() {
        String value = inputBox.getValue().trim();
        if (!value.isBlank()) {
            entry.addEntry(value);
            parentScreen.onEntryAdded();
            onClose();
        }
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
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 50, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        if (minecraft != null) {
            this.minecraft.setScreen(parentScreen);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 || keyCode == 335) { // Enter / numpad Enter
            if (addButton.active) {
                addEntry();
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void tick() {
        super.tick();

        UiTheme.setCurrent(ClientConfig.UI_THEME.get());
        PanoramaTheme.setCurrent(ClientConfig.PANORAMA_THEME.get());
    }
}