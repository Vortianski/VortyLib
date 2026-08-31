package xox.labvorty.vortylib.gui.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import xox.labvorty.vortylib.configs.ClientConfig;
import xox.labvorty.vortylib.data.config.entries.EntityListConfigEntry;
import xox.labvorty.vortylib.gui.theme.PanoramaTheme;
import xox.labvorty.vortylib.gui.theme.UiTheme;
import xox.labvorty.vortylib.gui.widget.ThemedButton;

public class EntityListScreen extends Screen {
    private static final int ROW_HEIGHT = 28;
    private static final int MARGIN_X = 20;
    private static final int REMOVE_WIDTH = 56;

    private final EntityListConfigEntry entry;
    private final Screen parentScreen;
    private CurrentEntityList list;

    public EntityListScreen(EntityListConfigEntry entry) {
        super(Component.literal(entry.getLabel().getString()));
        this.entry = entry;
        this.parentScreen = Minecraft.getInstance().screen;
    }

    @Override
    protected void init() {
        int listTop = 40;
        int listHeight = this.height - 100;
        int listWidth = this.width - MARGIN_X * 2;

        this.list = new CurrentEntityList(this.minecraft, listWidth, listHeight, listTop, ROW_HEIGHT);
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
        for (String entityId : entry.getValue()) {
            ResourceLocation id = ResourceLocation.parse(entityId);
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(id);
            if (type != null) {
                list.add(new CurrentEntityRow(type, id));
            }
        }

        if (!list.children().isEmpty()) {
            list.setScrollAmount(list.getMaxScroll());
        }
    }

    private void openAddScreen() {
        if (minecraft != null) {
            this.minecraft.setScreen(new EntityBrowserScreen(this, entry));
        }
    }

    public void onEntityAdded() {
        refreshList();
    }

    public void onEntityRemoved() {
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
        guiGraphics.drawCenteredString(this.font, Component.literal(entry.getValue().size() + " entities"), this.width / 2, 26, 0xAAAAAA);
    }

    @Override
    public void onClose() {
        if (minecraft != null) {
            this.minecraft.setScreen(parentScreen);
        }
    }

    private class CurrentEntityList extends ObjectSelectionList<CurrentEntityRow> {
        CurrentEntityList(Minecraft mc, int width, int height, int top, int itemHeight) {
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

        void add(CurrentEntityRow row) {
            this.addEntry(row);
        }

        void clear() {
            this.clearEntries();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            CurrentEntityRow clicked = this.getEntryAtPosition(mouseX, mouseY);
            if (clicked != null && clicked.removeButton.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    private class CurrentEntityRow extends ObjectSelectionList.Entry<CurrentEntityRow> {
        private final EntityType<?> type;
        private final ResourceLocation id;
        private final ThemedButton removeButton;

        CurrentEntityRow(EntityType<?> type, ResourceLocation id) {
            this.type = type;
            this.id = id;
            this.removeButton = ThemedButton.builder(Component.literal("Remove"), b -> {
                        entry.removeItem(id.toString());
                        onEntityRemoved();
                    })
                    .size(REMOVE_WIDTH, 20)
                    .build();
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            Component name = type.getDescription();
            int maxWidth = width - REMOVE_WIDTH - 16;
            int nameWidth = Minecraft.getInstance().font.width(name);
            if (nameWidth > maxWidth) {
                name = Component.literal(Minecraft.getInstance().font.plainSubstrByWidth(name.getString(), maxWidth) + "...");
            }
            guiGraphics.drawString(Minecraft.getInstance().font, name, left + 6, top + 4, 0xFFFFFF);

            Component idText = Component.literal(id.toString()).withStyle(ChatFormatting.GRAY);
            int idWidth = Minecraft.getInstance().font.width(idText);
            if (idWidth > maxWidth) {
                idText = Component.literal(Minecraft.getInstance().font.plainSubstrByWidth(idText.getString(), maxWidth) + "...");
            }
            guiGraphics.drawString(Minecraft.getInstance().font, idText, left + 6, top + 16, 0x888888);

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
            return type.getDescription();
        }
    }

    @Override
    public void tick() {
        super.tick();

        UiTheme.setCurrent(ClientConfig.UI_THEME.get());
        PanoramaTheme.setCurrent(ClientConfig.PANORAMA_THEME.get());
    }
}