package cyberslas.pathundergates.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class Util {
    public static boolean blockAllowsPathBelow(IWorldReader worldIn, BlockPos pos) {
        BlockState blockstate = worldIn.getBlockState(pos);
        return !MappedBlocklists.matchesBlockBlacklist(worldIn, pos) && (!blockstate.getMaterial().isSolid() || blockstate.getBlock() instanceof FenceGateBlock || MappedBlocklists.matchesBlockWhitelist(worldIn, pos));
    }
}
