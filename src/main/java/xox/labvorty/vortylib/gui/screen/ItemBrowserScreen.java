package xox.labvorty.vortylib.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import xox.labvorty.vortylib.configs.ClientConfig;
import xox.labvorty.vortylib.data.config.entries.ListConfigEntry;
import xox.labvorty.vortylib.gui.theme.PanoramaTheme;
import xox.labvorty.vortylib.gui.theme.UiTheme;
import xox.labvorty.vortylib.gui.widget.ThemedButton;
import xox.labvorty.vortylib.gui.widget.ThemedEditBox;

import java.util.ArrayList;
import java.util.List;

public class ItemBrowserScreen extends Screen {
    private static final int ROW_HEIGHT = 28;
    private static final int MARGIN_X = 20;

    private final ItemListScreen parentScreen;
    private final ListConfigEntry entry;
    private ItemList list;
    private List<Item> allItems;

    public ItemBrowserScreen(ItemListScreen parentScreen, ListConfigEntry entry) {
        super(Component.translatable("vortylib.item_browser.add_item"));
        this.parentScreen = parentScreen;
        this.entry = entry;
    }

    @Override
    protected void init() {
        List<String> currentList = entry.getValue();

        this.allItems = new ArrayList<>();
        for (ResourceLocation id : BuiltInRegistries.ITEM.keySet()) {
            if (!currentList.contains(id.toString())) {
                allItems.add(BuiltInRegistries.ITEM.get(id));
            }
        }
        allItems.sort((a, b) -> {
            String nameA = Component.translatable(a.getDescriptionId()).getString();
            String nameB = Component.translatable(b.getDescriptionId()).getString();
            return nameA.compareToIgnoreCase(nameB);
        });

        ThemedEditBox searchBox = new ThemedEditBox(this.font, MARGIN_X, 30, this.width - MARGIN_X * 2, 20, Component.literal("Search"));
        searchBox.setResponder(this::filterItems);
        this.addRenderableWidget(searchBox);

        int listWidth = this.width - MARGIN_X * 2;
        this.list = new ItemList(this.minecraft, listWidth, this.height - 110, 60, ROW_HEIGHT);
        filterItems("");
        this.addRenderableWidget(this.list);

        this.addRenderableWidget(ThemedButton.builder(CommonComponents.GUI_DONE, b -> onClose())
                .bounds(this.width / 2 - 100, this.height - 30, 200, 20)
                .build());
    }

    private void filterItems(String query) {
        double previousScroll = list.getScrollAmount();
        list.clear();
        String lower = query.toLowerCase();
        for (Item item : allItems) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            String name = Component.translatable(item.getDescriptionId()).getString().toLowerCase();
            if (id.toString().contains(lower) || name.contains(lower)) {
                list.add(new ItemRow(item, id));
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

        parentScreen.onItemAdded();
    }

    private void addItem(ResourceLocation itemId) {
        entry.addItem(itemId.toString());
        onClose();
    }

    private class ItemList extends net.minecraft.client.gui.components.ObjectSelectionList<ItemRow> {
        ItemList(Minecraft mc, int width, int height, int top, int itemHeight) {
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

        void add(ItemRow row) {
            this.addEntry(row);
        }

        void clear() {
            this.clearEntries();
        }
    }

    private class ItemRow extends net.minecraft.client.gui.components.ObjectSelectionList.Entry<ItemRow> {
        private final Item item;
        private final ResourceLocation id;

        ItemRow(Item item, ResourceLocation id) {
            this.item = item;
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
            guiGraphics.renderItem(new ItemStack(item), left + 6, top + 4);

            Component name = Component.translatable(item.getDescriptionId());
            guiGraphics.drawString(Minecraft.getInstance().font, name, left + 32, top + 4, 0xFFFFFF);

            Component idText = Component.literal(id.toString()).withStyle(net.minecraft.ChatFormatting.DARK_GRAY);
            guiGraphics.drawString(Minecraft.getInstance().font, idText, left + 32, top + 16, 0x888888);

            if (hovering) {
                guiGraphics.fill(left, top, left + width, top + height, 0x80FFFFFF);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button != 0) {
                return false;
            }
            addItem(id);
            return true;
        }

        @Override
        public @NotNull Component getNarration() {
            return Component.translatable(item.getDescriptionId());
        }
    }

    @Override
    public void tick() {
        super.tick();

        UiTheme.setCurrent(ClientConfig.UI_THEME.get());
        PanoramaTheme.setCurrent(ClientConfig.PANORAMA_THEME.get());
    }
}