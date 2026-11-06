package xox.labvorty.vortylib.compat.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EmiEntrypoint
public class EmiCompat implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        Set<Item> registeredItems = new HashSet<>();

        for (ItemStack itemStack : EmiHelper.items) {
            Item item = itemStack.getItem();

            if (registeredItems.add(item)) {
                registry.addEmiStack(EmiStack.of(item.getDefaultInstance()));
            }
        }
    }
}
