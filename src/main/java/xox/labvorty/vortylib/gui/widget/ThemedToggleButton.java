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
        guiGraphics.blitSprite(
                UiTheme.current().buttonSprites().get(this.active, this.isHoveredOrFocused()),
                this.getX(), this.getY(), this.getWidth(), this.getHeight()
        );
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        int color = this.getFGColor();
        this.renderString(guiGraphics, minecraft.font, color | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}