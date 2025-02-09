package com.skizzium.projectapple.block;

import com.skizzium.projectapple.init.PA_Tags;
import com.skizzium.projectapple.init.block.PA_Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class CandyCane extends SugarCaneBlock {
    public CandyCane(Properties properties) {
        super(properties);
    }
    
    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockState blockstate = world.getBlockState(pos.below());
        if (blockstate.getBlock() == this) {
            return true;
        }
        else {
            if (blockstate.is(PA_Blocks.CANDY_NYLIUM.get()) || blockstate.is(PA_Blocks.CANDYRACK.get())) {
                BlockPos blockpos = pos.below();

                for(Direction direction : Direction.Plane.HORIZONTAL) {
                    FluidState fluidstate = world.getFluidState(blockpos.relative(direction));
                    if (fluidstate.is(PA_Tags.Fluids.SKIZZIK_CANDY_FLUIDS)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
