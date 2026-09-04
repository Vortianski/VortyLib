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
import xox.labvorty.vortylib.gui.widget.ThemedEditBox;

import java.util.ArrayList;
import java.util.List;

public class EntityBrowserScreen extends Screen {
    private static final int ROW_HEIGHT = 28;
    private static final int MARGIN_X = 20;

    private final EntityListScreen parentScreen;
    private final EntityListConfigEntry entry;
    private EntityList list;
    private List<EntityType<?>> allEntities;

    public EntityBrowserScreen(EntityListScreen parentScreen, EntityListConfigEntry entry) {
        super(Component.translatable("vortylib.entity_browser.add_entity"));
        this.parentScreen = parentScreen;
        this.entry = entry;
    }

    @Override
    protected void init() {
        List<String> currentList = entry.getValue();

        this.allEntities = new ArrayList<>();
        for (ResourceLocation id : BuiltInRegistries.ENTITY_TYPE.keySet()) {
            if (!currentList.contains(id.toString())) {
                allEntities.add(BuiltInRegistries.ENTITY_TYPE.get(id));
            }
        }
        allEntities.sort((a, b) -> a.getDescription().getString().compareToIgnoreCase(b.getDescription().getString()));

        ThemedEditBox searchBox = new ThemedEditBox(this.font, MARGIN_X, 30, this.width - MARGIN_X * 2, 20, Component.literal("Search"));
        searchBox.setResponder(this::filterEntities);
        this.addRenderableWidget(searchBox);

        int listWidth = this.width - MARGIN_X * 2;
        this.list = new EntityList(this.minecraft, listWidth, this.height - 110, 60, ROW_HEIGHT);
        filterEntities("");
        this.addRenderableWidget(this.list);

        this.addRenderableWidget(ThemedButton.builder(CommonComponents.GUI_DONE, b -> onClose())
                .bounds(this.width / 2 - 100, this.height - 30, 200, 20)
                .build());
    }

    private void filterEntities(String query) {
        double previousScroll = list.getScrollAmount();
        list.clear();
        String lower = query.toLowerCase();
        for (EntityType<?> type : allEntities) {
            ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
            String name = type.getDescription().getString().toLowerCase();
            if (id.toString().contains(lower) || name.contains(lower)) {
                list.add(new EntityRow(type, id));
            }
        }
        list.setScrollAmount(Math.min(previousScroll, list.getMaxScroll()));
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
    }

    @Override
    public void onClose() {
        if (minecraft != null) {
            this.minecraft.setScreen(parentScreen);
        }

        parentScreen.onEntityAdded();
    }

    private void addEntity(ResourceLocation entityId) {
        entry.addItem(entityId.toString());
        onClose();
    }

    private class EntityList extends ObjectSelectionList<EntityRow> {
        EntityList(Minecraft mc, int width, int height, int top, int itemHeight) {
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

        void add(EntityRow row) {
            this.addEntry(row);
        }

        void clear() {
            this.clearEntries();
        }
    }

    private class EntityRow extends ObjectSelectionList.Entry<EntityRow> {
        private final EntityType<?> type;
        private final ResourceLocation id;

        EntityRow(EntityType<?> type, ResourceLocation id) {
            this.type = type;
            this.id = id;
        }

        @Override
        public void render(
                GuiGraphics guiGraphics,
                int index,
                int top,
                int left,
                int width,
                int height,
                int mouseX,
                int mouseY,
                boolean hovering,
                float partialTick
        ) {
            guiGraphics.drawString(Minecraft.getInstance().font, type.getDescription(), left + 6, top + 4, 0xFFFFFF);

            Component idText = Component.literal(id.toString()).withStyle(ChatFormatting.DARK_GRAY);
            guiGraphics.drawString(Minecraft.getInstance().font, idText, left + 6, top + 16, 0x888888);

            if (hovering) {
                guiGraphics.fill(left, top, left + width, top + height, 0x80FFFFFF);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button != 0) {
                return false;
            }
            addEntity(id);
            return true;
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