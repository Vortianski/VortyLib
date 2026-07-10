package xox.labvorty.vortylib.mixin.client;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xox.labvorty.vortylib.data.creative_tab.ExpandableCreativeTab;
import xox.labvorty.vortylib.data.creative_tab.ExpandableGroup;
import xox.labvorty.vortylib.data.creative_tab.ExpansionHelpers;

import java.util.Collection;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeScreenGroupMixin {
    @Shadow
    private static CreativeModeTab selectedTab;

    @Shadow
    protected abstract void refreshCurrentTabContents(Collection<ItemStack> items);

    @Inject(
        method = "slotClicked",
        at = @At("HEAD"),
        cancellable = true
    )
    private void vortylib$onSlotClicked(Slot slot, int slotId, int mouseButton, net.minecraft.world.inventory.ClickType clickType, CallbackInfo ci) {
        if (!(selectedTab instanceof ExpandableCreativeTab expandableCreativeTab)) {
            return;
        }

        if (slot == null) {
            return;
        }

        ItemStack itemStack = slot.getItem();
        String groupId = ExpansionHelpers.getGroupID(itemStack);
        ExpandableGroup expandableGroup = expandableCreativeTab.groups.get(groupId);

        if (expandableGroup != null) {
            ExpansionHelpers.toggleExpanded(expandableGroup.icon);
            expandableCreativeTab.groups.replace(groupId, expandableGroup);
            refreshCurrentTabContents(expandableCreativeTab.getDisplayItems());
            ci.cancel();
        }
    }
}