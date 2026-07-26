package xox.labvorty.vortylib.mixin_helpers;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface AbstractContainerScreenAccessor {
    Slot vortylib$getHoveredSlot();
    List<Component> vortylib$getTooltipFromContainerItem(ItemStack itemStack);
}
