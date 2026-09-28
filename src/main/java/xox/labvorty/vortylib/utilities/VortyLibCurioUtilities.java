package xox.labvorty.vortylib.utilities;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class VortyLibCurioUtilities {
    public static boolean hasCurio(LivingEntity livingEntity, ItemStack itemStack) {
        return hasCurio(livingEntity, itemStack.getItem());
    }

    public static boolean hasCurio(LivingEntity livingEntity, Item item) {
        LazyOptional<ICuriosItemHandler> itemHandlerLazyOptional = CuriosApi.getCuriosInventory(livingEntity);
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        atomicBoolean.set(false);
        itemHandlerLazyOptional.ifPresent(itemHandler -> {
            atomicBoolean.set(itemHandler.isEquipped(item));
        });

        return atomicBoolean.get();
    }

    @Nullable
    public static ItemStack getCurio(LivingEntity livingEntity, Item item) {
        LazyOptional<ICuriosItemHandler> optionalCuriosItemHandler = CuriosApi.getCuriosInventory(livingEntity);
        AtomicReference<ItemStack> itemStack = new AtomicReference<>();

        optionalCuriosItemHandler.ifPresent((itemHandler) -> {
            Optional<SlotResult> optionalSlotResult = itemHandler.findFirstCurio(item);

            optionalSlotResult.ifPresent((slotResult) -> {
                itemStack.set(slotResult.stack());
            });
        });

        return itemStack.get();
    }

    public record CurioMatch<T>(ItemStack stack, T value) {}

    @Nullable
    public static <T> CurioMatch<T> findFirstCurioOfType(LivingEntity livingEntity, Class<T> type) {
        LazyOptional<ICuriosItemHandler> optionalHandler = CuriosApi.getCuriosInventory(livingEntity);
        AtomicReference<CurioMatch<T>> atomicReference = new AtomicReference<>();

        optionalHandler.ifPresent(itemHandler -> {
            for (ICurioStacksHandler stacksHandler : itemHandler.getCurios().values()) {
                IDynamicStackHandler stacks = stacksHandler.getStacks();

                for (int i = 0; i < stacks.getSlots(); i++) {
                    ItemStack stack = stacks.getStackInSlot(i);

                    if (!stack.isEmpty() && type.isInstance(stack.getItem())) {
                        CurioMatch<T> curioMatch = new CurioMatch<>(stack, type.cast(stack.getItem()));
                        atomicReference.set(curioMatch);
                    }
                }
            }
        });

        return atomicReference.get();
    }
}
