package xox.labvorty.vortylib.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
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
    private static final EntityDataAccessor<Boolean> FACE_FORWARD = SynchedEntityData.defineId(SeatEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> SEAT_DIRECTION = SynchedEntityData.defineId(SeatEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> MIN_LOOK_ANGLE = SynchedEntityData.defineId(SeatEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> MAX_LOOK_ANGLE = SynchedEntityData.defineId(SeatEntity.class, EntityDataSerializers.FLOAT);

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

    public boolean getFaceForward() {
        return this.entityData.get(FACE_FORWARD);
    }

    public void setFaceForward(boolean faceForward) {
        this.entityData.set(FACE_FORWARD, faceForward);
    }

    public void setDirection(Direction direction) {
        if (direction.getAxis().isHorizontal()) {
            this.entityData.set(SEAT_DIRECTION, direction.get2DDataValue());
        }
    }

    public Direction getDirection() {
        return Direction.from2DDataValue(this.entityData.get(SEAT_DIRECTION));
    }

   public void setLookAngleRange(float minAngle, float maxAngle) {
        this.entityData.set(MIN_LOOK_ANGLE, minAngle);
        this.entityData.set(MAX_LOOK_ANGLE, maxAngle);
    }

    public float getMinLookAngle() {
        return this.entityData.get(MIN_LOOK_ANGLE);
    }

    public float getMaxLookAngle() {
        return this.entityData.get(MAX_LOOK_ANGLE);
    }

    public boolean hasLookAngleRestriction() {
        return getMinLookAngle() > -180f || getMaxLookAngle() < 180f;
    }

    public float clampLookYaw(float yaw) {
        float baseYaw = getDirection().toYRot();
        float relativeYaw = Mth.wrapDegrees(yaw - baseYaw);
        float clampedRelativeYaw = Mth.clamp(relativeYaw, getMinLookAngle(), getMaxLookAngle());
        return baseYaw + clampedRelativeYaw;
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
        builder.define(FACE_FORWARD, false);
        builder.define(SEAT_DIRECTION, Direction.SOUTH.get2DDataValue());
        builder.define(MIN_LOOK_ANGLE, -180f);
        builder.define(MAX_LOOK_ANGLE, 180f);
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