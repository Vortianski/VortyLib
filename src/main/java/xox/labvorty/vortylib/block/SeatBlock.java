package xox.labvorty.vortylib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import xox.labvorty.vortylib.entity.SeatEntity;

public class SeatBlock extends Block {
    public SeatBlock(Properties properties) {
        super(properties);
    }

    public Vector3f getDismountLocation(Level level, BlockState blockState, BlockPos blockPos) {
        return Vec3.atCenterOf(blockPos).toVector3f();
    }

    public Vector3f getSeatLocation(Level level, BlockState blockState, BlockPos blockPos) {
        return Vec3.atCenterOf(blockPos).toVector3f();
    }

    public boolean isOccupied(Level level, Vector3f vector3f) {
        return !level.getEntitiesOfClass(SeatEntity.class, new AABB(new BlockPos((int)vector3f.x, (int)vector3f.y, (int)vector3f.z))).isEmpty();
    }

    public Direction getDirection(BlockState blockState) {
        return Direction.NORTH;
    }

    public Pair<Float, Float> getMaxAngle(BlockState blockState) {
        return Pair.of(-90f, 90f);
    }

    public boolean faceForward(BlockState blockState) {
        return false;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Player player, @NotNull BlockHitResult blockHitResult) {
        Vector3f seatLocation = getSeatLocation(level, blockState, blockPos);
        Vector3f dismountLocation = getDismountLocation(level, blockState, blockPos);

        if (!isOccupied(level, seatLocation)) {
            SeatEntity seatEntity = new SeatEntity(level);

            seatEntity.setPos(new Vec3(seatLocation.x, seatLocation.y, seatLocation.z));
            seatEntity.setLocation(seatLocation);
            seatEntity.setSeatLocation(blockPos);
            seatEntity.setDismountLocation(dismountLocation);
            Pair<Float, Float> lookAngleRange = getMaxAngle(blockState);
            seatEntity.setLookAngleRange(lookAngleRange.getKey(), lookAngleRange.getValue());
            seatEntity.setDirection(getDirection(blockState));
            seatEntity.setFaceForward(faceForward(blockState));

            level.addFreshEntity(seatEntity);
            player.startRiding(seatEntity);
        }

        return super.useWithoutItem(blockState, level, blockPos, player, blockHitResult);
    }
}
