package cyberslas.pathundergates.util;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

public class Util {
    public static boolean blockAllowsPathBelow(LevelReader worldIn, BlockPos pos) {
        BlockState blockstate = worldIn.getBlockState(pos);
        return !ParsedConfig.matchesBlockBlacklist(worldIn, pos) && (!blockstate.getMaterial().isSolid() || blockstate.getBlock() instanceof FenceGateBlock || ParsedConfig.matchesBlockWhitelist(worldIn, pos));
    }
}
