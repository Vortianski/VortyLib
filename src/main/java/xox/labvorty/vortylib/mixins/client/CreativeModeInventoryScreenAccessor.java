package xox.labvorty.vortylib.mixins.client;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreativeModeInventoryScreen.class)
public interface CreativeModeInventoryScreenAccessor {
    @Accessor("selectedTab")
    static CreativeModeTab vortylib$getSelectedTab() {
        throw new AssertionError("mixin not applied");
    }

    @Accessor("scrollOffs")
    float vortylib$getScrollOffs();
}
