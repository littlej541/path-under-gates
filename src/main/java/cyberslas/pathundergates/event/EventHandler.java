package cyberslas.pathundergates.event;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockGrassPath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class EventHandler {
    @SubscribeEvent
    public static void grassPathUpdatedByNeighbor(BlockEvent.NeighborNotifyEvent event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        Block block = world.getBlockState(event.getPos()).getBlock();
        ArrayList<BlockPos> surrounding_block_pos = new ArrayList<BlockPos>(Arrays.asList(
                pos.west(),
                pos.east(),
                pos.down(),
                pos.up(),
                pos.north(),
                pos.south()
        ));

        if (world.getBlockState(pos.down()).getBlock() instanceof BlockGrassPath && block instanceof BlockFenceGate) {
            event.setCanceled(true);
        }

        Iterator<BlockPos> iter = surrounding_block_pos.iterator();
        while (iter.hasNext()) {
            BlockPos current = iter.next();
            if (world.getBlockState(current).getBlock() instanceof BlockGrassPath && world.getBlockState(current.up()).getBlock() instanceof BlockFenceGate) {
                iter.remove();
                event.setCanceled(true);
            }
        }

        if (event.isCanceled()) {
            for (BlockPos current : surrounding_block_pos) {
                world.neighborChanged(current, block, pos);
            }

            if (event.getForceRedstoneUpdate()) {
                world.updateObservingBlocksAt(pos, block);
            }
        }
    }
}