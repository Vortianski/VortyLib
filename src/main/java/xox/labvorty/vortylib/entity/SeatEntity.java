package xox.labvorty.vortylib.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import xox.labvorty.vortylib.block.SeatBlock;
import xox.labvorty.vortylib.init.VortyLibEntities;

public class SeatEntity extends Entity {
    private static final EntityDataAccessor<BlockPos> SEAT_LOCATION = SynchedEntityData.defineId(SeatEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Vector3f> LOCATION = SynchedEntityData.defineId(SeatEntity.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Vector3f> DISMOUNT_LOCATION = SynchedEntityData.defineId(SeatEntity.class, EntityDataSerializers.VECTOR3);

    public SeatEntity(Level level) {
        this(VortyLibEntities.SEAT_ENTITY.get(), level);
    }

    public SeatEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public void setLocation(Vector3f pos) {
        this.entityData.set(LOCATION, pos);
    }

    public void setDismountLocation(Vector3f pos) {
        this.entityData.set(DISMOUNT_LOCATION, pos);
    }

    public void setSeatLocation(BlockPos blockPos) {
        this.entityData.set(SEAT_LOCATION, blockPos);
    }

    @Override
    public void tick() {
        if (this.level().isClientSide) return;

        BlockPos blockPos = this.entityData.get(SEAT_LOCATION);

        if (level().getBlockState(blockPos).getBlock() instanceof SeatBlock seatBlock) {
            if (this.getPassengers().isEmpty()) {
                this.discard();
            }
        } else {
            this.discard();
        }
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    @Override
    protected boolean canRide(@NotNull Entity entity) {
        return true;
    }

    @Override
    public @NotNull Vec3 getDismountLocationForPassenger(@NotNull LivingEntity livingEntity) {
        Vector3f dismountLocation = this.entityData.get(DISMOUNT_LOCATION);
        BlockPos seatLocation = this.getOnPos();

        if (dismountLocation.distance(new Vector3f(seatLocation.getX(), seatLocation.getY(), seatLocation.getZ())) > 16) {
            return super.getDismountLocationForPassenger(livingEntity);
        }

        return new Vec3(dismountLocation.x, dismountLocation.y, dismountLocation.z);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SEAT_LOCATION, new BlockPos(0, 0, 0));
        builder.define(LOCATION, new Vector3f(0, 0, 0));
        builder.define(DISMOUNT_LOCATION, new Vector3f(0, 0, 0));
    }

    @Override
    protected void removePassenger(@NotNull Entity entity) {
        super.removePassenger(entity);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {

    }
}
