package cyberslas.pathundergates.block;

import cyberslas.pathundergates.util.MappedBlocklists;
import net.minecraft.block.*;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class PUGGrassPathBlock extends GrassPathBlock {
    public PUGGrassPathBlock(Block.Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return !this.getDefaultState().isValidPosition(context.getWorld(), context.getPos()) ? Block.nudgeEntitiesWithNewState(this.getDefaultState(), Blocks.DIRT.getDefaultState(), context.getWorld(), context.getPos()) : super.getStateForPlacement(context);
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        if (!isValidPosition(state, worldIn, pos)) {
            FarmlandBlock.turnToDirt(state, worldIn, pos);
        }
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return blockAllowsPathBelow(worldIn, pos.up());
    }

    public static boolean blockAllowsPathBelow(IWorldReader worldIn, BlockPos pos) {
        BlockState blockstate = worldIn.getBlockState(pos);
        return !MappedBlocklists.matchesBlockBlacklist(worldIn, pos) && (!blockstate.getMaterial().isSolid() || blockstate.getBlock() instanceof FenceGateBlock || MappedBlocklists.matchesBlockWhitelist(worldIn, pos));
    }
}
