package xox.labvorty.vortylib.block.entity;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xox.labvorty.vortylib.utilities.VortyLibUtilities;

import java.util.stream.IntStream;

public class ItemDisplayBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {
    private NonNullList<ItemStack> stacks;

    protected ItemDisplayBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        this(type, pos, state, 1);
    }

    protected ItemDisplayBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int slots) {
        super(type, pos, state);

        this.stacks = NonNullList.withSize(slots, ItemStack.EMPTY);
    }

    @Override
    public void setChanged() {
        if (this.level == null || level.isClientSide) return;

        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_CLIENTS);

        super.setChanged();
    }

    public ItemInteractionResult interaction(Player player, InteractionHand interactionHand, ItemStack itemStack) {
        return interaction(player, interactionHand, itemStack, 0);
    }

    public ItemInteractionResult interaction(Player player, InteractionHand interactionHand, ItemStack itemStack, int slot) {
        if (this.level == null) return ItemInteractionResult.FAIL;

        if (interactionHand == InteractionHand.MAIN_HAND) {
            if (itemStack.isEmpty()) {
                ItemStack itemStackCopy = this.removeItemNoUpdate(slot);

                if (!itemStackCopy.isEmpty()) {
                    onItemRemoved(player, itemStack, slot);

                    if (!this.level.isClientSide) {
                        VortyLibUtilities.tryInsertOrDrop(player, itemStackCopy);
                    }

                    return ItemInteractionResult.sidedSuccess(this.level.isClientSide);
                }
            } else if (canPlaceItem(slot, itemStack)) {
                ItemStack itemStackCopy = itemStack.copy();

                setItem(slot, itemStackCopy);
                itemStack.consume(1, player);
                onItemAdded(player, itemStack, slot);

                if (!this.level.isClientSide) {
                    this.level.playSound(
                            null,
                            worldPosition,
                            getAddItemSound(),
                            SoundSource.BLOCKS,
                            1.0f,
                            this.level.random.nextFloat() * 0.10F + 0.95F
                    );
                }

                return ItemInteractionResult.sidedSuccess(this.level.isClientSide);
            }
        }

        return ItemInteractionResult.FAIL;
    }

    public void onItemRemoved(Player player, ItemStack itemStack, int slot) {
        if (this.level != null) {
            this.level.gameEvent(GameEvent.BLOCK_CHANGE, worldPosition, GameEvent.Context.of(player, getBlockState()));
        }
    }

    public void onItemAdded(Player player, ItemStack stack, int slot) {
        if (this.level != null) {
            this.level.gameEvent(GameEvent.BLOCK_CHANGE, worldPosition, GameEvent.Context.of(player, getBlockState()));
        }

        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, worldPosition, stack);
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
        }
    }

    public SoundEvent getAddItemSound(ItemStack itemStack) {
        return SoundEvents.ITEM_FRAME_ADD_ITEM;
    }

    public SoundEvent getAddItemSound() {
        return getAddItemSound(ItemStack.EMPTY);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(compoundTag, provider);

        if (!this.tryLoadLootTable(compoundTag)) {
            this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        }

        ContainerHelper.loadAllItems(compoundTag, this.stacks, provider);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(compoundTag, provider);

        if (!this.trySaveLootTable(compoundTag)) {
            ContainerHelper.saveAllItems(compoundTag, this.stacks, provider);
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider provider) {
        return this.saveWithoutMetadata(provider);
    }

    @Override
    public int getContainerSize() {
        return stacks.size();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory) {
        return null;
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return super.createMenu(i, inventory, player);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return this.getBlockState().getBlock().getName();
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return this.stacks;
    }

    @Override
    public void setItems(@NotNull NonNullList<ItemStack> itemStacks) {
        this.stacks = itemStacks;
    }

    @Override
    public boolean canPlaceItem(int index, @NotNull ItemStack itemStack) {
        return this.isEmpty();
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, @NotNull ItemStack itemStack, @Nullable Direction direction) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, @NotNull ItemStack itemStack, @NotNull Direction direction) {
        return false;
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction direction) {
        return IntStream.range(0, this.getContainerSize()).toArray();
    }

}
