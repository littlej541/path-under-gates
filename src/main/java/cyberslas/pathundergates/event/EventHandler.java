package cyberslas.pathundergates.event;

import cyberslas.pathundergates.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandler
{
    @SubscribeEvent
    public static void rightClickWithShovel(PlayerInteractEvent.RightClickBlock event) {
        if (event.getItemStack().getItem() instanceof ItemSpade) {
            EntityPlayer player = event.getEntityPlayer();
            EnumHand hand = event.getHand();
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
                    IBlockState iblockstate1 = ModBlocks.GRASS_PATH.getDefaultState();
                    worldIn.playSound(player, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);

                    if (!worldIn.isRemote) {
                        worldIn.setBlockState(pos, iblockstate1, 11);
                        itemstack.damageItem(1, player);
                    }

                    event.setCanceled(true);
                } else {
                    event.setUseItem(Event.Result.DENY);
                }
            }
        }
    }
}
