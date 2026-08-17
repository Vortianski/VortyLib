package xox.labvorty.vortylib.mixin.compat.curios;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.MutablePair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.client.gui.CuriosScreen;
import xox.labvorty.vortylib.data.creative_tab.ExpansionHelpers;
import xox.labvorty.vortylib.items.defined.AdvancedItemOptions;
import xox.labvorty.vortylib.mixin_helpers.AbstractContainerScreenAccessor;
import xox.labvorty.vortylib.utilities.VortyLibUtilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(CuriosScreen.class)
public class CuriosScreenMixin {
    @Unique
    protected List<MutablePair<Integer, Item>> vortylib$itemHold = new ArrayList<>();

    @Inject(method = "renderTooltip", at = @At("HEAD"), cancellable = true)
    private void vortylib$advancedTooltipItem(GuiGraphics guiGraphics, int x, int y, CallbackInfo ci) {
        Slot hoveredSlot = ((AbstractContainerScreenAccessor)(Object)this).vortylib$getHoveredSlot();

        if (hoveredSlot != null && !hoveredSlot.getItem().isEmpty() && (ExpansionHelpers.getGroupID(hoveredSlot.getItem()) == null || ExpansionHelpers.getGroupID(hoveredSlot.getItem()).isEmpty())) {
            ItemStack itemStack = hoveredSlot.getItem();

            if (itemStack.getItem() instanceof AdvancedItemOptions advancedItemOptions && advancedItemOptions.useExpander(itemStack)) {
                Item item = itemStack.getItem();

                MutablePair<Integer, Item> pair = vortylib$itemHold.stream()
                        .filter(p -> p.getValue().equals(item))
                        .findFirst()
                        .orElse(null);

                int ticks = pair != null ? pair.getLeft() : 0;

                List<Component> tooltipList = ((AbstractContainerScreenAccessor)(Object)this).vortylib$getTooltipFromContainerItem(itemStack);
                tooltipList.add(tooltipList.indexOf(advancedItemOptions.getKeysTooltip()) + 1, VortyLibUtilities.createHoldBar(ticks, 25));

                guiGraphics.renderTooltip(
                        Minecraft.getInstance().font,
                        tooltipList,
                        itemStack.getTooltipImage(),
                        itemStack,
                        x,
                        y
                );
                ci.cancel();
            }
        }
    }

    @Inject(method = "containerTick", at = @At("HEAD"))
    private void vortylib$tick(CallbackInfo ci) {
        Slot hoveredSlot = ((AbstractContainerScreenAccessor)(Object)this).vortylib$getHoveredSlot();

        if (hoveredSlot == null || hoveredSlot.getItem().isEmpty()) {
            vortylib$fadeHold();
            return;
        }

        ItemStack itemStack = hoveredSlot.getItem();

        if (!(ExpansionHelpers.getGroupID(hoveredSlot.getItem()) == null || ExpansionHelpers.getGroupID(hoveredSlot.getItem()).isEmpty())) {
            vortylib$fadeHold();
            return;
        }

        if (!(itemStack.getItem() instanceof AdvancedItemOptions advancedItemOptions)) {
            vortylib$fadeHold();
            return;
        }

        if (!advancedItemOptions.useExpander(itemStack)) {
            vortylib$fadeHold();
            return;
        }

        Item item = itemStack.getItem();

        MutablePair<Integer, Item> pair = vortylib$itemHold.stream()
                .filter(p -> p.getValue().equals(item))
                .findFirst()
                .orElse(null);

        if (VortyLibUtilities.isKeyOfKeysDown(advancedItemOptions.getKeysToHold())) {
            if (pair != null) {
                pair.setLeft(Math.min(Math.clamp(pair.getLeft() + 1, 0, 26), 100));

                if (pair.getLeft() >= 25) {
                    advancedItemOptions.trigger(((AbstractContainerScreen<?>)(Object)this), itemStack);
                }
            } else {
                vortylib$itemHold.add(new MutablePair<>(1, item));
            }
        } else {
            if (pair != null) {
                int ticks = pair.getLeft();

                if (ticks > 0) {
                    pair.setLeft(ticks - 1);
                }

                if (pair.getLeft() <= 0) {
                    vortylib$itemHold.remove(pair);
                }
            }
        }
    }

    @Unique
    private void vortylib$fadeHold() {
        Iterator<MutablePair<Integer, Item>> iterator = vortylib$itemHold.iterator();

        while (iterator.hasNext()) {
            MutablePair<Integer, Item> pair = iterator.next();

            int ticks = pair.getLeft();

            if (ticks <= 0) {
                iterator.remove();
            } else {
                pair.setLeft(ticks - 1);
            }
        }
    }
}
