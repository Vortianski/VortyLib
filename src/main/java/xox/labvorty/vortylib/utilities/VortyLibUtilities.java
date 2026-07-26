package xox.labvorty.vortylib.utilities;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class VortyLibUtilities {
    public static void tryInsertOrDrop(Player player, ItemStack stack) {
        if (stack.isEmpty()) return;

        ItemStack stackCopy = stack.copy();
        int totalCountBefore = player.getInventory().items.stream()
                .filter(s -> !s.isEmpty())
                .mapToInt(ItemStack::getCount)
                .sum();

        if (!player.getInventory().add(stackCopy)) {
            player.drop(stack, false);
        } else {
            int totalCountAfter = player.getInventory().items.stream()
                    .filter(s -> !s.isEmpty())
                    .mapToInt(ItemStack::getCount)
                    .sum();

            if (totalCountBefore == totalCountAfter) {
                player.drop(stack, false);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean isKeyOfKeysDown(List<Integer> keys) {
        for (Integer key : keys) {
            if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isSimpleBlockItem(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return false;
        }

        Block block = blockItem.getBlock();
        BlockState defaultState = block.defaultBlockState();

        if (block instanceof StairBlock || block instanceof SlabBlock) {
            return true;
        }

        return isFullCube(defaultState);
    }

    private static boolean isFullCube(BlockState state) {
        VoxelShape shape = state.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO, CollisionContext.empty());

        return Block.isShapeFullBlock(shape);
    }

    public static Component createHoldBar(int ticks, int maxTicks) {
        int bars = 40;
        int filled = Math.min(bars, (ticks * bars) / maxTicks);

        MutableComponent component = Component.empty();

        component.append(Component.literal("[").withStyle(ChatFormatting.DARK_GRAY));

        for (int i = 0; i < bars; i++) {
            if (i < filled) {
                component.append(Component.literal("|")
                        .withStyle(ChatFormatting.GRAY));
            } else {
                component.append(Component.literal("|")
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
        }

        component.append(Component.literal("]").withStyle(ChatFormatting.DARK_GRAY));

        return component;
    }
}
