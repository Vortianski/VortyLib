package xox.labvorty.vortylib.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import xox.labvorty.vortylib.gui.theme.UiTheme;
import xox.labvorty.vortylib.gui.widget.backported.Border;
import xox.labvorty.vortylib.gui.widget.backported.NineSliceSprite;
import xox.labvorty.vortylib.gui.widget.backported.WidgetSprite;

public class ThemedToggleButton extends AbstractButton {
    public interface OnToggle {
        void onToggle(boolean newValue);
    }

    private boolean value;
    private final OnToggle onToggle;

    public ThemedToggleButton(int x, int y, int width, int height, boolean initialValue, OnToggle onToggle) {
        super(x, y, width, height, Component.empty());
        this.value = initialValue;
        this.onToggle = onToggle;
        updateMessage();
    }

    public void setValue(boolean value) {
        this.value = value;
        updateMessage();
    }

    public boolean getValue() {
        return value;
    }

    private void updateMessage() {
        setMessage(value ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
    }

    @Override
    public void onPress() {
        value = !value;
        updateMessage();
        onToggle.onToggle(value);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        WidgetSprite widgetSprite = UiTheme.current().buttonSprites();
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

        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        int color = this.getFGColor();
        this.renderString(guiGraphics, minecraft.font, color | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}