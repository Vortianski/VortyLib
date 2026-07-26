package xox.labvorty.vortylib.utilities;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class VortyLibCurioUtilities {
    public static boolean hasCurio(LivingEntity livingEntity, ItemStack itemStack) {
        return hasCurio(livingEntity, itemStack.getItem());
    }

    public static boolean hasCurio(LivingEntity livingEntity, Item item) {
        boolean equipped = false;

        if (CuriosApi.getCuriosInventory(livingEntity).isPresent()) {
            equipped = CuriosApi.getCuriosInventory(livingEntity).get().isEquipped(item);
        }

        return equipped;
    }

    @Nullable
    public static ItemStack getCurio(LivingEntity livingEntity, Item item) {
        Optional<ICuriosItemHandler> optionalCuriosItemHandler = CuriosApi.getCuriosInventory(livingEntity);
        AtomicReference<ItemStack> itemStack = new AtomicReference<>();

        optionalCuriosItemHandler.ifPresent((itemHandler) -> {
            Optional<SlotResult> optionalSlotResult = itemHandler.findFirstCurio(item);

            optionalSlotResult.ifPresent((slotResult) -> {
                itemStack.set(slotResult.stack());
            });
        });

        return itemStack.get();
    }

    public static void updateCurioData(LivingEntity livingEntity, Item item, Function<CompoundTag, Boolean> function) {
        Optional<ICuriosItemHandler> optionalCuriosItemHandler = CuriosApi.getCuriosInventory(livingEntity);

        optionalCuriosItemHandler.ifPresent((itemHandler) -> {
            if (itemHandler.isEquipped(item)) {
                Optional<SlotResult> optionalSlotResult = itemHandler.findFirstCurio(item);

                optionalSlotResult.ifPresent((slotResult) -> {
                    SlotContext slotContext = slotResult.slotContext();
                    ItemStack itemStack = slotResult.stack();

                    CustomData.update(DataComponents.CUSTOM_DATA, itemStack, function::apply);

                    itemHandler.setEquippedCurio(slotContext.identifier(), slotContext.index(), itemStack);
                });
            }
        });
    }
}
