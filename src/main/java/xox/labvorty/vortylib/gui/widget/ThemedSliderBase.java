package xox.labvorty.vortylib.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import xox.labvorty.vortylib.gui.theme.UiTheme;
import xox.labvorty.vortylib.gui.widget.backported.Border;
import xox.labvorty.vortylib.gui.widget.backported.NineSliceSprite;
import xox.labvorty.vortylib.gui.widget.backported.WidgetSprite;

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

    public boolean isDragging() {
        return draggingThumb;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        WidgetSprite widgetSprite = UiTheme.current().sliderTrackSprites();
        NineSliceSprite nineSliceSprite = this.active
                ? this.isHoveredOrFocused()
                ? widgetSprite.enabledFocused()
                : widgetSprite.enabled()
                : widgetSprite.disabled();
        Border border = nineSliceSprite.border();
        if (border instanceof Border.All all) {
            guiGraphics.blitNineSliced(
                    nineSliceSprite.resourceLocation(),
                    this.getX(),
                    this.getY(),
                    this.getWidth(),
                    this.getHeight(),
                    all.getLeft(),
                    all.getTop(),
                    all.getRight(),
                    all.getBottom(),
                    nineSliceSprite.uvWidth(),
                    nineSliceSprite.uvHeight(),
                    nineSliceSprite.u(),
                    nineSliceSprite.v()
            );
        } else if (border instanceof Border.Single single) {
            guiGraphics.blitNineSliced(
                    nineSliceSprite.resourceLocation(),
                    this.getX(),
                    this.getY(),
                    this.getWidth(),
                    this.getHeight(),
                    single.getSize(),
                    nineSliceSprite.uvWidth(),
                    nineSliceSprite.uvHeight(),
                    nineSliceSprite.u(),
                    nineSliceSprite.v()
            );
        }

        WidgetSprite sliderHandleSprite = UiTheme.current().sliderHandleSprites();
        NineSliceSprite nineSliceSliderHandleSprite = this.active
                ? this.isHoveredOrFocused()
                ? sliderHandleSprite.enabledFocused()
                : sliderHandleSprite.enabled()
                : sliderHandleSprite.disabled();
        Border sliderBorder = nineSliceSliderHandleSprite.border();
        if (sliderBorder instanceof Border.All all) {
            guiGraphics.blitNineSliced(
                    nineSliceSliderHandleSprite.resourceLocation(),
                    this.getX() + (int)(this.value * (double)(this.width - 8)),
                    this.getY(),
                    8,
                    this.getHeight(),
                    all.getLeft(),
                    all.getTop(),
                    all.getRight(),
                    all.getBottom(),
                    nineSliceSliderHandleSprite.uvWidth(),
                    nineSliceSliderHandleSprite.uvHeight(),
                    nineSliceSliderHandleSprite.u(),
                    nineSliceSliderHandleSprite.v()
            );
        } else if (sliderBorder instanceof Border.Single single) {
            guiGraphics.blitNineSliced(
                    nineSliceSliderHandleSprite.resourceLocation(),
                    this.getX() + (int)(this.value * (double)(this.width - 8)),
                    this.getY(),
                    8,
                    this.getHeight(),
                    single.getSize(),
                    nineSliceSliderHandleSprite.uvWidth(),
                    nineSliceSliderHandleSprite.uvHeight(),
                    nineSliceSliderHandleSprite.u(),
                    nineSliceSliderHandleSprite.v()
            );
        }
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