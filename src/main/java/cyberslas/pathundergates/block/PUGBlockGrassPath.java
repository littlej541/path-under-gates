package cyberslas.pathundergates.block;

import cyberslas.pathundergates.PathUnderGates;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockGrassPath;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PUGBlockGrassPath extends BlockGrassPath
{
    private static final AxisAlignedBB field_194405_c = new AxisAlignedBB(0.0D, 0.9375D, 0.0D, 1.0D, 1.0D, 1.0D);

    protected PUGBlockGrassPath()
    {
        this.setHardness(0.65F);
        this.setSoundType(SoundType.PLANT);
        this.setRegistryName("minecraft","grass_path");
        this.setUnlocalizedName("grassPath");
        this.disableStats();
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        //super.onBlockAdded(worldIn, pos, state);
        this.updateBlockState(worldIn, pos);
    }

    private void updateBlockState(World worldIn, BlockPos pos)
    {
        if (worldIn.getBlockState(pos.up()).getMaterial().isSolid() && worldIn.getBlockState(pos.up()).getBlock() instanceof BlockFenceGate != true)
        {
            PUGBlockGrassPath.turnToDirt(worldIn, pos);
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        //super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        this.updateBlockState(worldIn, pos);
    }

    private static void turnToDirt(World p_190970_0_, BlockPos worldIn)
    {
        p_190970_0_.setBlockState(worldIn, Blocks.DIRT.getDefaultState());
        AxisAlignedBB axisalignedbb = field_194405_c.offset(worldIn);

        for (Entity entity : p_190970_0_.getEntitiesWithinAABBExcludingEntity((Entity)null, axisalignedbb))
        {
            double d0 = Math.min(axisalignedbb.maxY - axisalignedbb.minY, axisalignedbb.maxY - entity.getEntityBoundingBox().minY);
            entity.setPositionAndUpdate(entity.posX, entity.posY + d0 + 0.001D, entity.posZ);
        }
    }
}