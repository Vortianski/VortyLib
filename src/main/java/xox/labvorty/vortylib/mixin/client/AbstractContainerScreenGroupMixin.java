package xox.labvorty.vortylib.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xox.labvorty.vortylib.data.creative_tab.ExpandableCreativeTab;
import xox.labvorty.vortylib.data.creative_tab.ExpansionHelpers;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenGroupMixin {
    @Shadow
    @Final
    protected AbstractContainerMenu menu;

    @Shadow
    protected Slot hoveredSlot;

    @Inject(method = "renderSlot", at = @At("TAIL"))
    private void vortylib$drawGroupMarker(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci) {
        CreativeModeTab tab = CreativeModeInventoryScreenAccessor.vortylib$getSelectedTab();
        if (!(tab instanceof ExpandableCreativeTab expandableCreativeTab)) {
            return;
        }

        ItemStack itemStack = slot.getItem();
        String groupId = ExpansionHelpers.getGroupID(itemStack);

        if (groupId == null) {
            return;
        }

        boolean expanded = ExpansionHelpers.isExpanded(itemStack);
        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();

        poseStack.translate(slot.x + 10, slot.y + 1, 320);

        guiGraphics.drawString(
                Minecraft.getInstance().font,
                expanded ? "-" : "+",
                0,
                0,
                0xFFFFFF,
                true
        );

        poseStack.popPose();
    }

    @Inject(
            method = "renderTooltip",
            at = @At("HEAD"),
            cancellable = true
    )
    private void vortylib$replaceTooltip(GuiGraphics guiGraphics, int x, int y, CallbackInfo ci) {
        CreativeModeTab tab = CreativeModeInventoryScreenAccessor.vortylib$getSelectedTab();
        if (!(tab instanceof ExpandableCreativeTab expandableCreativeTab)) {
            return;
        }

        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            ItemStack itemStack = hoveredSlot.getItem();
            String groupId = ExpansionHelpers.getGroupID(itemStack);

            if (groupId == null) {
                return;
            }

            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable("vortylib.tab." + groupId), x, y);
            ci.cancel();
        }
    }
}