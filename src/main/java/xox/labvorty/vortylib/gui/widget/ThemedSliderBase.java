package xox.labvorty.vortylib.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import xox.labvorty.vortylib.gui.theme.UiTheme;

public abstract class ThemedSliderBase extends AbstractSliderButton {
    private static final int HANDLE_WIDTH = 8;

    protected boolean draggingThumb = false;
    protected double dragOffset = 0;

    protected ThemedSliderBase(int x, int y, int width, int height, double initialProgress) {
        super(x, y, width, height, Component.empty(), initialProgress);
    }

    @Override
    protected void updateMessage() {
        setMessage(Component.empty());
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        boolean focused = this.isHoveredOrFocused();

        guiGraphics.blitSprite(
                UiTheme.current().sliderTrackSprites().get(this.active, focused),
                this.getX(), this.getY(), this.getWidth(), this.getHeight()
        );

        int handleX = this.getX() + (int) (this.value * (this.width - HANDLE_WIDTH));
        guiGraphics.blitSprite(
                UiTheme.current().sliderHandleSprites().get(this.active, focused),
                handleX, this.getY(), HANDLE_WIDTH, this.getHeight()
        );
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isMouseOver(mouseX, mouseY)) return false;
        if (!this.active || !this.visible) return false;
        if (button != 0) return false;

        int thumbX = getX() + (int) (this.value * (this.width - HANDLE_WIDTH));

        if (mouseX >= thumbX && mouseX <= thumbX + HANDLE_WIDTH) {
            draggingThumb = true;
            dragOffset = mouseX - (thumbX + HANDLE_WIDTH / 2.0);

            return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!draggingThumb) return false;
        if (button != 0) return false;

        double relativeX = mouseX - dragOffset - getX() - (HANDLE_WIDTH / 2.0);
        this.value = Math.max(0.0, Math.min(1.0, relativeX / (this.width - HANDLE_WIDTH)));
        updateMessage();
        applyValue();
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean wasDragging = draggingThumb;
        draggingThumb = false;
        dragOffset = 0;
        return wasDragging;
    }
}