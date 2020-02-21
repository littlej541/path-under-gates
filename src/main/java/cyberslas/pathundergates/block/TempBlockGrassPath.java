package cyberslas.pathundergates.block;

import cyberslas.pathundergates.PathUnderGates;
import cyberslas.pathundergates.event.EventHandler;
import net.minecraft.block.BlockGrassPath;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TempBlockGrassPath extends BlockGrassPath {
    public TempBlockGrassPath() {
        this.setRegistryName(PathUnderGates.MODID,"temp_grass_path");
        this.disableStats();
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        IBlockState iblockstate = Blocks.GRASS_PATH.getDefaultState();

        try {
            EventHandler.GrassPathBlockStateUpdateHandler.handleBlockStateUpdate(worldIn, pos, iblockstate, 11);
        } catch(Exception e) {
            PathUnderGates.logger.error("Grass path block update failed!", e);
            worldIn.setBlockState(pos, iblockstate, 11);
        }
    }
}
