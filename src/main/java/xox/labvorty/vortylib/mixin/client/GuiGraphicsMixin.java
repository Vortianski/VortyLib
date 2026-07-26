package xox.labvorty.vortylib.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.ItemDecoratorHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xox.labvorty.vortylib.items.defined.AdvancedItemOptions;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {
    @Shadow
    @Final
    private PoseStack pose;

    @Shadow
    public abstract void fill(RenderType renderType, int minX, int minY, int maxX, int maxY, int color);

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    public abstract int drawString(Font font, @Nullable String text, int x, int y, int color, boolean dropShadow);

    @Inject(
            method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void vortylib$renderItemDecorations(Font font, ItemStack stack, int x, int y, String text, CallbackInfo ci) {
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof AdvancedItemOptions advancedItemOptions) {
                this.pose.pushPose();
                if (stack.getCount() != 1 || text != null) {
                    String s = text == null ? String.valueOf(stack.getCount()) : text;
                    this.pose.translate(0.0F, 0.0F, 200.0F);
                    this.drawString(font, s, x + 19 - 2 - font.width(s), y + 6 + 3, 16777215, true);
                }

                if (stack.isBarVisible()) {
                    int l = stack.getBarWidth();
                    int i = stack.getBarColor();
                    int j = x + 2;
                    int k = y + 13;
                    this.fill(RenderType.guiOverlay(), j, k, j + 13, k + 2, -16777216);
                    this.fill(RenderType.guiOverlay(), j, k, j + l, k + 1, i | -16777216);
                }

                if (advancedItemOptions.shouldRenderCooldown(stack)) {
                    LocalPlayer localplayer = this.minecraft.player;
                    float f = localplayer == null ? 0.0F : localplayer.getCooldowns().getCooldownPercent(stack.getItem(), this.minecraft.getTimer().getGameTimeDeltaPartialTick(true));
                    if (f > 0.0F) {
                        int i1 = y + Mth.floor(16.0F * (1.0F - f));
                        int j1 = i1 + Mth.ceil(16.0F * f);
                        this.fill(RenderType.guiOverlay(), x, i1, x + 16, j1, Integer.MAX_VALUE);
                    }
                }

                this.pose.popPose();
                ItemDecoratorHandler.of(stack).render((GuiGraphics)(Object)this, font, stack, x, y);

                ci.cancel();
            }
        }
    }
}
