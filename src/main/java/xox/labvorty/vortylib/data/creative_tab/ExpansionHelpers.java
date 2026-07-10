package xox.labvorty.vortylib.data.creative_tab;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import xox.labvorty.vortylib.init.VortyLibDataComponents;

public class ExpansionHelpers {
    public static boolean isExpanded(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

        return compoundTag.getBoolean("vorty_lib_expanded");
    }

    public static void toggleExpanded(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        compoundTag.putBoolean("vorty_lib_expanded", !compoundTag.getBoolean("vorty_lib_expanded"));
        CustomData.set(DataComponents.CUSTOM_DATA, itemStack, compoundTag);
    }

    public static String getGroupID(ItemStack itemStack) {
        return itemStack.get(VortyLibDataComponents.GROUP_COMPONENT);
    }
}
