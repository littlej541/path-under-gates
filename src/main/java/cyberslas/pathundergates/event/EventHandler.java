package cyberslas.pathundergates.event;

import cyberslas.pathundergates.util.Util;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

public class EventHandler  {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void rightClickWithShovel(PlayerInteractEvent.RightClickBlock event) {
        if (event.getItemStack().getItem().getToolClasses(event.getItemStack()).contains(("shovel"))) {
            EntityPlayer player = event.getEntityPlayer();
            BlockPos pos = event.getPos();
            EnumFacing facing = event.getFace();
            World worldIn = event.getWorld();

            ItemStack itemstack = event.getItemStack();

            if (player.canPlayerEdit(pos.offset(facing), facing, itemstack)) {
                if (facing != EnumFacing.DOWN && Util.blockAllowsPathBelow(worldIn, pos.up()) && worldIn.getBlockState(pos).getBlock() == Blocks.GRASS) {
                    IBlockState iblockstate1 = Blocks.GRASS_PATH.getDefaultState();
                    worldIn.playSound(player, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    player.swingArm(event.getHand());

                    if (!worldIn.isRemote) {
                        itemstack.damageItem(1, player);
                    }
                }
            }
        }
    }
}
