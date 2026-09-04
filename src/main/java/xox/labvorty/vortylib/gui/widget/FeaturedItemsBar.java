package xox.labvorty.vortylib.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FeaturedItemsBar extends AbstractWidget {
    private static final int EDGE_FADE_WIDTH = 30;

    private final List<Item> items;
    private final int stepWidth;
    private final int contentWidth;
    private double scrollOffset = 0;
    private double scrollVelocity = 0;
    private long lastManualScrollTime = -1;
    private long lastTickTime = -1;

    public FeaturedItemsBar(int x, int y, int width, int height, List<Item> items) {
        super(x, y, width, height, Component.empty());
        this.items = items;
        this.stepWidth = 26;
        this.contentWidth = items.size() * stepWidth;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (items.isEmpty() || contentWidth <= 0) {
            return;
        }

        advanceScroll();

        int left = getX();
        int top = getY();
        int right = left + width;
        int bottom = top + height;

        renderBarBackground(guiGraphics, left, top, right, bottom);

        int itemBoxSize = Math.max(8, Math.min(24, height - 4));
        float itemScale = itemBoxSize / (float)16;
        int itemY = top + (height - itemBoxSize) / 2;

        Item hoveredItem = null;

        guiGraphics.enableScissor(left, top, right, bottom);

        double x = left - (scrollOffset % contentWidth);
        int index = 0;
        int guard = items.size() * 3 + 16;
        while (x < right && guard-- > 0) {
            Item item = items.get(index % items.size());
            if (x + itemBoxSize >= left) {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate((float) x, itemY, 0);
                guiGraphics.pose().scale(itemScale, itemScale, 1f);
                guiGraphics.renderItem(new ItemStack(item), 0, 0);
                guiGraphics.pose().popPose();

                int hitX = (int) Math.round(x);
                if (mouseX >= hitX && mouseX < hitX + itemBoxSize && mouseY >= itemY && mouseY < itemY + itemBoxSize) {
                    hoveredItem = item;
                }
            }
            x += stepWidth;
            index++;
        }

        guiGraphics.disableScissor();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 200);
        renderEdgeFade(guiGraphics, left, top, bottom, true);
        renderEdgeFade(guiGraphics, right - EDGE_FADE_WIDTH, top, bottom, false);
        guiGraphics.pose().popPose();

        if (hoveredItem != null) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable(hoveredItem.getDescriptionId()), mouseX, mouseY);
        }
    }

    private void renderBarBackground(GuiGraphics guiGraphics, int left, int top, int right, int bottom) {
        guiGraphics.fill(left, top, right, bottom, 0xB08B8B8B);
        guiGraphics.fill(
                left + 1,
                top + 1,
                right - 1,
                bottom - 1,
                0xB0373737
        );
    }

    private void renderEdgeFade(GuiGraphics guiGraphics, int fadeLeft, int top, int bottom, boolean fadingTowardLeftEdge) {
        for (int i = 0; i < EDGE_FADE_WIDTH; i++) {
            float t = fadingTowardLeftEdge
                    ? i / (float) EDGE_FADE_WIDTH
                    : (EDGE_FADE_WIDTH - i) / (float) EDGE_FADE_WIDTH;
            int alpha = (int) (255 * (1f - t));
            int argb = (alpha << 24) | 0x101010;
            int columnX = fadeLeft + i;
            guiGraphics.fill(columnX, top, columnX + 1, bottom, argb);
        }
    }

    private void advanceScroll() {
        long now = System.currentTimeMillis();
        if (lastTickTime < 0) {
            lastTickTime = now;
            return;
        }
        double deltaSeconds = (now - lastTickTime) / 1000.0;
        lastTickTime = now;

        if (scrollVelocity != 0) {
            scrollOffset += scrollVelocity * deltaSeconds;
            scrollVelocity *= Math.exp(-5.0 * deltaSeconds);
            if (Math.abs(scrollVelocity) < 1.0) {
                scrollVelocity = 0;
            }
        }

        boolean paused = lastManualScrollTime >= 0 && (now - lastManualScrollTime) < 3000;
        if (!paused) {
            scrollOffset += 14.0f * deltaSeconds;
        }

        scrollOffset = ((scrollOffset % contentWidth) + contentWidth) % contentWidth;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!isMouseOver(mouseX, mouseY) || contentWidth <= 0) {
            return false;
        }

        scrollVelocity -= scrollY * 220.0;
        lastManualScrollTime = System.currentTimeMillis();

        return true;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {}
}