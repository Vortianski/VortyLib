package xox.labvorty.vortylib.mixins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xox.labvorty.vortylib.data.creative_tab.ExpandableCreativeTab;
import xox.labvorty.vortylib.data.creative_tab.ExpandableGroup;
import xox.labvorty.vortylib.data.creative_tab.ExpansionHelpers;

import java.util.HashMap;
import java.util.Map;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {
    @Unique
    private static final ResourceLocation VORTYLIB$GROUP_BORDERS = ResourceLocation.fromNamespaceAndPath("vortylib", "textures/gui/creative_group_borders.png");
    @Unique
    private static final int VORTYLIB$CELL_SIZE = 18;
    @Unique
    private static final int VORTYLIB$ATLAS_WIDTH = 18;
    @Unique
    private static final int VORTYLIB$ATLAS_HEIGHT = 22;

    @Shadow
    protected Slot hoveredSlot;

    @Inject(method = "renderSlot", at = @At("TAIL"))
    private void vortylib$drawGroupMarker(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci) {
        CreativeModeTab tab = CreativeModeInventoryScreenAccessor.vortylib$getSelectedTab();
        if (!(tab instanceof ExpandableCreativeTab expandableCreativeTab)) {
            return;
        }

        if (!(((AbstractContainerScreen<?>)(Object)this) instanceof CreativeModeInventoryScreen)) {
            return;
        }

        ItemStack itemStack = slot.getItem();
        String groupId = ExpansionHelpers.getGroupID(itemStack);

        if (groupId.isEmpty()) {
            return;
        }

        boolean expanded = ExpansionHelpers.isExpanded(itemStack);
        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();

        poseStack.translate(slot.x + 10, slot.y + 9, 320);

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

        if (!(((AbstractContainerScreen<?>)(Object)this) instanceof CreativeModeInventoryScreen)) {
            return;
        }

        if (((AbstractContainerScreen<?>)(Object)this).getMenu().getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            ItemStack itemStack = hoveredSlot.getItem();
            String groupId = ExpansionHelpers.getGroupID(itemStack);

            if (groupId.isEmpty()) {
                return;
            }

            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable("vortylib.tab." + groupId), x, y);
            ci.cancel();
        }
    }

    @Inject(method = "renderSlot", at = @At("HEAD"))
    private void vortylib$drawInlinedBackground(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci) {
        CreativeModeTab tab = CreativeModeInventoryScreenAccessor.vortylib$getSelectedTab();
        if (!(tab instanceof ExpandableCreativeTab expandableCreativeTab)) {
            return;
        }

        if (!(((AbstractContainerScreen<?>)(Object)this) instanceof CreativeModeInventoryScreen)) {
            return;
        }

        if (!vortylib$isCreativeGridSlot(slot)) {
            return;
        }

        String groupId = vortylib$getExpandedGroupId(expandableCreativeTab, slot.getItem());
        if (groupId == null) {
            return;
        }

        guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0x33000000);
    }

    @Inject(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderLabels(Lnet/minecraft/client/gui/GuiGraphics;II)V")
    )
    private void vortylib$drawAllGroupBorders(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!((Object) this instanceof CreativeModeInventoryScreenAccessor)) {
            return;
        }

        CreativeModeTab tab = CreativeModeInventoryScreenAccessor.vortylib$getSelectedTab();

        if (!(tab instanceof ExpandableCreativeTab expandableCreativeTab)) {
            return;
        }

        Map<Long, String> groupCells = vortylib$buildGroupCells(expandableCreativeTab, false);
        Map<Long, String> occupiedGroupCells = vortylib$buildGroupCells(expandableCreativeTab, true);
        int visibleSlots = Math.min(45, ((AbstractContainerScreen<?>)(Object)this).getMenu().slots.size());

        guiGraphics.flush();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 400.0F);

        for (int i = 0; i < visibleSlots; i++) {
            Slot slot = ((AbstractContainerScreen<?>)(Object)this).getMenu().slots.get(i);
            String groupId = groupCells.get(vortylib$cellKey(slot.x, slot.y));

            if (groupId != null) {
                vortylib$drawGroupBorderParts(guiGraphics, slot, groupId, groupCells, occupiedGroupCells, false);
            }
        }

        for (int i = 0; i < visibleSlots; i++) {
            Slot slot = ((AbstractContainerScreen<?>)(Object)this).getMenu().slots.get(i);
            String groupId = groupCells.get(vortylib$cellKey(slot.x, slot.y));

            if (groupId != null) {
                vortylib$drawGroupBorderParts(guiGraphics, slot, groupId, groupCells, occupiedGroupCells, true);
            }
        }

        guiGraphics.pose().popPose();
        guiGraphics.flush();
    }

    @Unique
    private String vortylib$getExpandedGroupId(ExpandableCreativeTab tab, ItemStack stack) {
        String iconGroupId = ExpansionHelpers.getGroupID(stack);

        if (!iconGroupId.isEmpty()) {
            ExpandableGroup iconGroup = tab.groups.get(iconGroupId);

            if (iconGroup != null) {
                return ExpansionHelpers.isExpanded(iconGroup.icon) ? iconGroupId : null;
            }

            return null;
        }

        for (Map.Entry<String, ExpandableGroup> entry : tab.groups.entrySet()) {
            ExpandableGroup group = entry.getValue();

            if (!ExpansionHelpers.isExpanded(group.icon)) {
                continue;
            }

            for (ItemStack member : group.items) {
                if (ItemStack.isSameItemSameTags(member, stack)) {
                    return entry.getKey();
                }
            }
        }

        return null;
    }

    @Unique
    private void vortylib$drawGroupBorderParts(GuiGraphics guiGraphics, Slot slot, String groupId, Map<Long, String> groupCells, Map<Long, String> occupiedGroupCells, boolean innerCorners) {
        int x = slot.x;
        int y = slot.y;
        int step = VORTYLIB$CELL_SIZE;
        int slotIndex = ((AbstractContainerScreen<?>)(Object)this).getMenu().slots.indexOf(slot);
        int col = slotIndex % 9;
        int row = slotIndex / 9;

        boolean north = vortylib$isSameGroup(groupCells, x, y - step, groupId);
        boolean east = vortylib$isSameGroup(groupCells, x + step, y, groupId);
        boolean south = vortylib$isSameGroup(groupCells, x, y + step, groupId);
        boolean west = vortylib$isSameGroup(groupCells, x - step, y, groupId);

        boolean northWest = vortylib$isSameGroup(groupCells, x - step, y - step, groupId);
        boolean northEast = vortylib$isSameGroup(groupCells, x + step, y - step, groupId);
        boolean southEast = vortylib$isSameGroup(groupCells, x + step, y + step, groupId);
        boolean southWest = vortylib$isSameGroup(groupCells, x - step, y + step, groupId);

        boolean drawNorth = !north;
        boolean drawEast = !east;
        boolean drawSouth = !south;
        boolean drawWest = !west;

        if (innerCorners) {
            if (north && west && !northWest) {
                vortylib$blitBorder(guiGraphics, x - 2, y - 2, 9, 9, 3, 3);
            }

            if (north && east && !northEast) {
                vortylib$blitBorder(guiGraphics, x + 15, y - 2, 4, 9, 3, 3);
            }

            if (south && east && !southEast) {
                vortylib$blitBorder(guiGraphics, x + 15, y + 15, 4, 4, 3, 3);
            }

            if (south && west && !southWest) {
                vortylib$blitBorder(guiGraphics, x - 2, y + 15, 9, 4, 3, 3);
            }

            return;
        }

        int horizontalLength = (!east || col == 8) ? 17 : 18;
        int verticalLength   = (!south || row == 4) ? 17 : 18;

        if (drawNorth) {
            vortylib$blitBorder(guiGraphics, x, y - 1, 0, 2, horizontalLength, 2);
        }

        if (drawSouth) {
            vortylib$blitBorder(guiGraphics, x, y + 15, 0, 0, horizontalLength, 2);
        }

        if (drawEast) {
            vortylib$blitBorder(guiGraphics, x + 15, y, 2, 4, 2, verticalLength);
        }

        if (drawWest) {
            vortylib$blitBorder(guiGraphics, x - 1, y, 0, 4, 2, verticalLength);
        }

        if (drawNorth && drawWest) vortylib$drawTopLeftCorner(guiGraphics, x, y);
        if (drawNorth && drawEast) vortylib$drawTopRightCorner(guiGraphics, x, y);
        if (drawSouth && drawEast) vortylib$drawBottomRightCorner(guiGraphics, x, y);
        if (drawSouth && drawWest) vortylib$drawBottomLeftCorner(guiGraphics, x, y);
    }

    @Unique
    private Map<Long, String> vortylib$buildGroupCells(ExpandableCreativeTab tab, boolean includeCollapsedIcons) {
        Map<Long, String> groupCells = new HashMap<>();

        if (((AbstractContainerScreen<?>)(Object)this).getMenu() instanceof CreativeModeInventoryScreen.ItemPickerMenu itemPickerMenu && (Object) this instanceof CreativeModeInventoryScreenAccessor screenAccessor && !((AbstractContainerScreen<?>)(Object)this).getMenu().slots.isEmpty()) {
            int scrollableRows = Mth.positiveCeilDiv(itemPickerMenu.items.size(), 9) - 5;
            int scrollRow = Math.max((int) (screenAccessor.vortylib$getScrollOffs() * scrollableRows + 0.5F), 0);
            int originX = ((AbstractContainerScreen<?>)(Object)this).getMenu().slots.get(0).x;
            int originY = ((AbstractContainerScreen<?>)(Object)this).getMenu().slots.get(0).y;

            for (int i = 0; i < itemPickerMenu.items.size(); i++) {
                ItemStack itemStack = itemPickerMenu.items.get(i);
                String itemGroupId = vortylib$getExpandedGroupId(tab, itemStack);

                if (includeCollapsedIcons && itemGroupId == null) {
                    itemGroupId = ExpansionHelpers.getGroupID(itemStack);
                }

                if (itemGroupId == null) {
                    continue;
                }

                int x = originX + i % 9 * VORTYLIB$CELL_SIZE;
                int y = originY + (i / 9 - scrollRow) * VORTYLIB$CELL_SIZE;
                groupCells.put(vortylib$cellKey(x, y), itemGroupId);
            }

            return groupCells;
        }

        for (int i = 0; i < Math.min(45, ((AbstractContainerScreen<?>)(Object)this).getMenu().slots.size()); i++) {
            Slot visibleSlot = ((AbstractContainerScreen<?>)(Object)this).getMenu().slots.get(i);
            ItemStack itemStack = visibleSlot.getItem();
            String visibleGroupId = vortylib$getExpandedGroupId(tab, itemStack);

            if (includeCollapsedIcons && visibleGroupId == null) {
                visibleGroupId = ExpansionHelpers.getGroupID(itemStack);
            }

            if (visibleGroupId != null) {
                groupCells.put(vortylib$cellKey(visibleSlot.x, visibleSlot.y), visibleGroupId);
            }
        }

        return groupCells;
    }

    @Unique
    private static void vortylib$drawTopLeftCorner(GuiGraphics guiGraphics, int x, int y) {
        vortylib$blitBorder(guiGraphics, x - 1, y - 1, 4, 12, 3, 3);
    }

    @Unique
    private static void vortylib$drawTopRightCorner(GuiGraphics guiGraphics, int x, int y) {
        vortylib$blitBorder(guiGraphics, x + 14, y - 1, 9, 12, 3, 3);
    }

    @Unique
    private static void vortylib$drawBottomRightCorner(GuiGraphics guiGraphics, int x, int y) {
        vortylib$blitBorder(guiGraphics, x + 14, y + 14, 9, 17, 3, 3);
    }

    @Unique
    private static void vortylib$drawBottomLeftCorner(GuiGraphics guiGraphics, int x, int y) {
        vortylib$blitBorder(guiGraphics, x - 1, y + 14, 4, 17, 3, 3);
    }

    @Unique
    private static void vortylib$blitBorder(GuiGraphics guiGraphics, int x, int y, int atlasX, int atlasY, int width, int height) {
        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();

        poseStack.translate(x, y, 0);

        poseStack.scale(1, 1, 1);

        guiGraphics.blit(
                VORTYLIB$GROUP_BORDERS,
                0,
                0,
                atlasX,
                atlasY,
                width,
                height,
                VORTYLIB$ATLAS_WIDTH,
                VORTYLIB$ATLAS_HEIGHT
        );

        poseStack.popPose();
    }

    @Unique
    private static boolean vortylib$isSameGroup(Map<Long, String> groupCells, int x, int y, String groupId) {
        return groupId.equals(groupCells.get(vortylib$cellKey(x, y)));
    }

    @Unique
    private static long vortylib$cellKey(int x, int y) {
        return ((long) x << 32) ^ (y & 0xffffffffL);
    }

    @Unique
    private boolean vortylib$isCreativeGridSlot(Slot slot) {
        int slotIndex = ((AbstractContainerScreen<?>)(Object)this).getMenu().slots.indexOf(slot);
        return slotIndex >= 0 && slotIndex < 45;
    }
}