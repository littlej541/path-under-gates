package cyberslas.pathundergates.event;

import cyberslas.pathundergates.util.MappedBlocklists;
import cyberslas.pathundergates.PathUnderGates;
import cyberslas.pathundergates.block.ModBlocks;
import cyberslas.pathundergates.block.PUGGrassPathBlock;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.thread.EffectiveSide;

@Mod.EventBusSubscriber(modid=PathUnderGates.MODID)
public class EventHandler  {
    @SubscribeEvent
    public static void tagsLoaded(TagsUpdatedEvent event) {
        if (EffectiveSide.get().isServer()) {
            MappedBlocklists.processListsIntoMaps();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void rightClickWithShovel(PlayerInteractEvent.RightClickBlock event) {
        if (event.getItemStack().getItem().getToolTypes(event.getItemStack()).contains(ToolType.SHOVEL)) {
            PlayerEntity player = event.getPlayer();
            BlockPos pos = event.getPos();
            Direction facing = event.getFace();
            World worldIn = event.getWorld();

            ItemStack itemstack = event.getItemStack();

            if (player.canPlayerEdit(pos.offset(facing), facing, itemstack)) {
                if (facing != Direction.DOWN && PUGGrassPathBlock.blockAllowsPathBelow(worldIn, pos.up()) && worldIn.getBlockState(pos).getBlock() == Blocks.GRASS_BLOCK) {
                    BlockState blockstate1 = ModBlocks.PUG_GRASS_PATH.get().getDefaultState();
                    worldIn.playSound(player, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    player.swingArm(event.getHand());

                    if (!worldIn.isRemote) {
                        worldIn.setBlockState(pos, blockstate1, 11);
                        itemstack.damageItem(1, player, (p_220041_1_) -> {
                            Vector3d playerLookVec = player.getLookVec();
                            p_220041_1_.sendBreakAnimation(new ItemUseContext(player, event.getHand(), new BlockRayTraceResult(player.getPositionVec(), Direction.getFacingFromVector(playerLookVec.x, playerLookVec.y, playerLookVec.z), new BlockPos(player.getPosX(), player.getPosY() + player.getStandingEyeHeight(player.getPose(), player.getSize(player.getPose())), player.getPosZ()), false)).getHand());
                        });
                    }
                }
            }
        }
    }
}
