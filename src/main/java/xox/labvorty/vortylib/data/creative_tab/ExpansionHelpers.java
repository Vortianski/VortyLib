package xox.labvorty.vortylib.data.creative_tab;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class ExpansionHelpers {
    public static boolean isExpanded(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getOrCreateTag();

        return compoundTag.getBoolean("vorty_lib_expanded");
    }

    public static void toggleExpanded(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getOrCreateTag();

        itemStack.getOrCreateTag().putBoolean("vorty_lib_expanded", !compoundTag.getBoolean("vorty_lib_expanded"));
    }

    public static String getGroupID(ItemStack itemStack) {
        return itemStack.getOrCreateTag().getString("vorty_lib_group_id");
    }
}
