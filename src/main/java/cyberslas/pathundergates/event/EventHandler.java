package cyberslas.pathundergates.event;

import cyberslas.pathundergates.PathUnderGates;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockGrassPath;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class EventHandler  {
    @SubscribeEvent
    public static void rightClickWithShovel(PlayerInteractEvent.RightClickBlock event) {
        if (event.getItemStack().getItem().getToolClasses(event.getItemStack()).contains(("shovel"))) {
            EntityPlayer player = event.getEntityPlayer();
            BlockPos pos = event.getPos();
            EnumFacing facing = event.getFace();
            World worldIn = event.getWorld();

            ItemStack itemstack = event.getItemStack();

            if (!player.canPlayerEdit(pos.offset(facing), facing, itemstack)) {
                event.setUseItem(Event.Result.DENY);
            } else {
                IBlockState iblockstate = worldIn.getBlockState(pos);
                Block block = iblockstate.getBlock();

                if (facing != EnumFacing.DOWN && (worldIn.getBlockState(pos.up()).getMaterial() == Material.AIR || worldIn.getBlockState(pos.up()).getBlock() instanceof BlockFenceGate) && block == Blocks.GRASS) {
                    IBlockState iblockstate1 = Blocks.GRASS_PATH.getDefaultState();
                    worldIn.playSound(player, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);

                    if (!worldIn.isRemote) {
                        try {
                            GrassPathBlockStateUpdateHandler.handleBlockStateUpdate(worldIn, pos, iblockstate1, 11);
                        } catch(Exception e) {
                            PathUnderGates.logger.error(e.getStackTrace());
                            worldIn.setBlockState(pos, iblockstate1, 11);
                        }
                        itemstack.damageItem(1, player);
                    }

                    event.setCanceled(true);
                } else {
                    event.setUseItem(Event.Result.DENY);
                }
            }
        }
    }

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

    private static class GrassPathBlockStateUpdateHandler {
        private static void handleBlockStateUpdate(World worldIn, BlockPos pos, IBlockState iblockstate1, int flags) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
            WorldBlockStateHandler.setBlockState(worldIn, pos, iblockstate1, flags);
        }

        private static class WorldBlockStateHandler {
            private static boolean setBlockState(World world, BlockPos pos, IBlockState newState, int flags) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
                Field worldInfoField = World.class.getDeclaredField("worldInfo");
                worldInfoField.setAccessible(true);
                WorldInfo worldInfo = (WorldInfo) worldInfoField.get(world);

                if (world.isOutsideBuildHeight(pos)) {
                    return false;
                } else if (!world.isRemote && worldInfo.getTerrainType() == WorldType.DEBUG_ALL_BLOCK_STATES) {
                    return false;
                } else {
                    Chunk chunk = world.getChunkFromBlockCoords(pos);

                    pos = pos.toImmutable(); // Forge - prevent mutable BlockPos leaks
                    net.minecraftforge.common.util.BlockSnapshot blockSnapshot = null;
                    if (world.captureBlockSnapshots && !world.isRemote) {
                        blockSnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(world, pos, flags);
                        world.capturedBlockSnapshots.add(blockSnapshot);
                    }
                    IBlockState oldState = getBlockState(world, pos);
                    int oldLight = oldState.getLightValue(world, pos);
                    int oldOpacity = oldState.getLightOpacity(world, pos);

                    IBlockState iblockstate = ChunkBlockStateHandler.setBlockState(world, chunk, pos, newState);

                    if (iblockstate == null) {
                        if (blockSnapshot != null) world.capturedBlockSnapshots.remove(blockSnapshot);
                        return false;
                    } else {
                        if (newState.getLightOpacity(world, pos) != oldOpacity || newState.getLightValue(world, pos) != oldLight) {
                            world.profiler.startSection("checkLight");
                            world.checkLight(pos);
                            world.profiler.endSection();
                        }

                        if (blockSnapshot == null) // Don't notify clients or update physics while capturing blockstates
                        {
                            world.markAndNotifyBlock(pos, chunk, iblockstate, newState, flags);
                        }
                        return true;
                    }
                }
            }

            private static IBlockState getBlockState(World world, BlockPos pos) {
                if (world.isOutsideBuildHeight(pos)) {
                    return Blocks.AIR.getDefaultState();
                } else {
                    Chunk chunk = world.getChunkFromBlockCoords(pos);
                    return chunk.getBlockState(pos);
                }
            }
        }

        private static class ChunkBlockStateHandler {
            @Nullable
            private static IBlockState setBlockState(World world, Chunk chunk, BlockPos pos, IBlockState state) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
                Method propagateSkylightOcclusionMethod = Chunk.class.getDeclaredMethod("propagateSkylightOcclusion", int.class, int.class);
                Method relightBlockMethod = Chunk.class.getDeclaredMethod("relightBlock", int.class, int.class, int.class);
                propagateSkylightOcclusionMethod.setAccessible((true));
                relightBlockMethod.setAccessible((true));

                Field chunkDirtyField = Chunk.class.getDeclaredField("dirty");
                chunkDirtyField.setAccessible(true);

                Field chunkPrecipitationHeightMapField = Chunk.class.getDeclaredField("precipitationHeightMap");
                chunkPrecipitationHeightMapField.setAccessible(true);
                int[] precipitationHeightMap = (int[]) chunkPrecipitationHeightMapField.get(chunk);

                Field chunkHeightMapField = Chunk.class.getDeclaredField("heightMap");
                chunkHeightMapField.setAccessible(true);
                int[] heightMap = (int[]) chunkHeightMapField.get(chunk);

                Field chunkStorageArraysField = Chunk.class.getDeclaredField("storageArrays");
                chunkStorageArraysField.setAccessible(true);
                ExtendedBlockStorage[] storageArrays = (ExtendedBlockStorage[]) chunkStorageArraysField.get(chunk);

                int i = pos.getX() & 15;
                int j = pos.getY();
                int k = pos.getZ() & 15;
                int l = k << 4 | i;

                if (j >= precipitationHeightMap[l] - 1) {
                    precipitationHeightMap[l] = -999;
                }

                int i1 = heightMap[l];
                IBlockState iblockstate = chunk.getBlockState(pos);

                if (iblockstate == state) {
                    return null;
                } else {
                    Block block = state.getBlock();
                    Block block1 = iblockstate.getBlock();
                    int k1 = iblockstate.getLightOpacity(world, pos); // Relocate old light value lookup here, so that it is called before TE is removed.
                    ExtendedBlockStorage extendedblockstorage = storageArrays[j >> 4];
                    boolean flag = false;

                    if (extendedblockstorage == Chunk.NULL_BLOCK_STORAGE) {
                        if (block == Blocks.AIR) {
                            return null;
                        }

                        extendedblockstorage = new ExtendedBlockStorage(j >> 4 << 4, world.provider.hasSkyLight());
                        storageArrays[j >> 4] = extendedblockstorage;
                        flag = j >= i1;
                    }

                    extendedblockstorage.set(i, j & 15, k, state);

                    //if (block1 != block)
                    {
                        if (!world.isRemote) {
                            if (block1 != block) //Only fire block breaks when the block changes.
                                block1.breakBlock(world, pos, iblockstate);
                            TileEntity te = chunk.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
                            if (te != null && te.shouldRefresh(world, pos, iblockstate, state))
                                world.removeTileEntity(pos);
                        } else if (block1.hasTileEntity(iblockstate)) {
                            TileEntity te = chunk.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
                            if (te != null && te.shouldRefresh(world, pos, iblockstate, state))
                                world.removeTileEntity(pos);
                        }
                    }

                    if (extendedblockstorage.get(i, j & 15, k).getBlock() != block) {
                        return null;
                    } else {
                        if (flag) {
                            chunk.generateSkylightMap();
                        } else {
                            int j1 = state.getLightOpacity(world, pos);

                            if (j1 > 0) {
                                if (j >= i1) {
                                    relightBlockMethod.invoke(chunk, i, j + 1, k);
                                }
                            } else if (j == i1 - 1) {
                                relightBlockMethod.invoke(chunk, i, j, k);
                            }

                            if (j1 != k1 && (j1 < k1 || chunk.getLightFor(EnumSkyBlock.SKY, pos) > 0 || chunk.getLightFor(EnumSkyBlock.BLOCK, pos) > 0)) {
                                propagateSkylightOcclusionMethod.invoke(chunk, i, k);
                            }
                        }

                        if (block.hasTileEntity(state)) {
                            TileEntity tileentity1 = chunk.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);

                            if (tileentity1 == null) {
                                tileentity1 = block.createTileEntity(world, state);
                                world.setTileEntity(pos, tileentity1);
                            }

                            if (tileentity1 != null) {
                                tileentity1.updateContainingBlockInfo();
                            }
                        }

                        chunkDirtyField.setBoolean(chunk, true);
                        chunkHeightMapField.set(chunk, (int[]) heightMap);
                        chunkPrecipitationHeightMapField.set(chunk, (int[]) precipitationHeightMap);
                        chunkStorageArraysField.set(chunk, (ExtendedBlockStorage[]) storageArrays);

                        return iblockstate;
                    }
                }
            }
        }
    }
}
