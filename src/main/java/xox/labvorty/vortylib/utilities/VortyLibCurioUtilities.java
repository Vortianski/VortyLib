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
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.ArrayList;
import java.util.List;
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

    public record CurioMatch<T>(ItemStack stack, T value) {}

    @Nullable
    public static <T> CurioMatch<T> findFirstCurioOfType(LivingEntity livingEntity, Class<T> type) {
        Optional<ICuriosItemHandler> optionalHandler = CuriosApi.getCuriosInventory(livingEntity);
        if (optionalHandler.isEmpty()) {
            return null;
        }

        for (ICurioStacksHandler stacksHandler : optionalHandler.get().getCurios().values()) {
            IDynamicStackHandler stacks = stacksHandler.getStacks();

            for (int i = 0; i < stacks.getSlots(); i++) {
                ItemStack stack = stacks.getStackInSlot(i);

                if (!stack.isEmpty() && type.isInstance(stack.getItem())) {
                    return new CurioMatch<>(stack, type.cast(stack.getItem()));
                }
            }
        }

        return null;
    }

    @Nullable
    public static <T> CurioMatch<T> findFirstCurioOfType(LivingEntity livingEntity, Class<T> type, String slotId) {
        Optional<ICuriosItemHandler> optionalHandler = CuriosApi.getCuriosInventory(livingEntity);
        if (optionalHandler.isEmpty()) {
            return null;
        }

        ICurioStacksHandler stacksHandler = optionalHandler.get().getCurios().get(slotId);
        if (stacksHandler == null) {
            return null;
        }

        IDynamicStackHandler stacks = stacksHandler.getStacks();
        for (int i = 0; i < stacks.getSlots(); i++) {
            ItemStack stack = stacks.getStackInSlot(i);

            if (!stack.isEmpty() && type.isInstance(stack.getItem())) {
                return new CurioMatch<>(stack, type.cast(stack.getItem()));
            }
        }

        return null;
    }

    public static <T> List<CurioMatch<T>> findAllCuriosOfType(LivingEntity livingEntity, Class<T> type) {
        List<CurioMatch<T>> results = new ArrayList<>();

        Optional<ICuriosItemHandler> optionalHandler = CuriosApi.getCuriosInventory(livingEntity);
        if (optionalHandler.isEmpty()) {
            return results;
        }

        for (ICurioStacksHandler stacksHandler : optionalHandler.get().getCurios().values()) {
            IDynamicStackHandler stacks = stacksHandler.getStacks();

            for (int i = 0; i < stacks.getSlots(); i++) {
                ItemStack stack = stacks.getStackInSlot(i);

                if (!stack.isEmpty() && type.isInstance(stack.getItem())) {
                    results.add(new CurioMatch<>(stack, type.cast(stack.getItem())));
                }
            }
        }

        return results;
    }
}
