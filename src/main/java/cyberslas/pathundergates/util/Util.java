package cyberslas.pathundergates.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Util {
    public static boolean blockAllowsPathBelow(World worldIn, BlockPos pos) {
        IBlockState blockstate = worldIn.getBlockState(pos);
        return !MappedBlocklists.matchesBlockBlacklist(worldIn, pos) && (!blockstate.getMaterial().isSolid() || blockstate.getBlock() instanceof BlockFenceGate || MappedBlocklists.matchesBlockWhitelist(worldIn, pos));
    }
}