package xox.labvorty.vortylib.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import xox.labvorty.vortylib.data.properties.FourDirectionProperty;

public class ThreeAxisRotatedBlock extends Block {
    public static final EnumProperty<FourDirectionProperty> YAW = EnumProperty.create("yaw", FourDirectionProperty.class);
    public static final EnumProperty<FourDirectionProperty> PITCH = EnumProperty.create("pitch", FourDirectionProperty.class);
    public static final EnumProperty<FourDirectionProperty> ROLL = EnumProperty.create("roll", FourDirectionProperty.class);

    public ThreeAxisRotatedBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(
                this.getStateDefinition().any()
                        .setValue(YAW, FourDirectionProperty.N)
                        .setValue(PITCH, FourDirectionProperty.N)
                        .setValue(ROLL, FourDirectionProperty.N)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(YAW, PITCH, ROLL);
    }
}
