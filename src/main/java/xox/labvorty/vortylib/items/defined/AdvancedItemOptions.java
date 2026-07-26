package xox.labvorty.vortylib.items.defined;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface AdvancedItemOptions {
    /**
     * Whether this item should use expandable rendering
     *
     * @param itemStack - ItemStack
     */
    default boolean useExpander(ItemStack itemStack) {
        return true;
    }

    /**
     * Which keys player can hold to call trigger method
     */
    default List<Integer> getKeysToHold() {
        return List.of();
    }

    /**
     * What tooltip should be displayed above progress-bar
     */
    default Component getKeysTooltip() {
        return Component.empty();
    }

    /**
     * Called on the client tooltip rendering only. Implementations should be annotated with {@code @OnlyIn(Dist.CLIENT)}.
     *
     * @param lastScreen the screen that was open before this one was triggered
     */
    void trigger(Screen lastScreen);

    /**
     * Whether item should have item decorator that renders white overlay for cooldown
     *
     * @param itemStack - ItemStack
     */
    default boolean shouldRenderCooldown(ItemStack itemStack) {
        return true;
    }
}
