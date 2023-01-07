package cyberslas.pathundergates.event;

import cyberslas.pathundergates.util.ParsedConfig;
import cyberslas.pathundergates.PathUnderGates;
import cyberslas.pathundergates.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=PathUnderGates.MODID)
public class EventHandler  {
    @SubscribeEvent
    public static void serverStarting(ServerStartingEvent event) {
        ParsedConfig.parseConfig();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void makePath(BlockEvent.BlockToolModificationEvent event) {
        if (event.getToolAction().equals(ToolActions.SHOVEL_FLATTEN) && !event.isSimulated()) {
            Player player = event.getPlayer();
            BlockPos blockpos = event.getPos();
            UseOnContext context = event.getContext();
            Direction clickedFace = context.getClickedFace();
            Level level = context.getLevel();
            InteractionHand hand = context.getHand();
            ItemStack itemstack = event.getHeldItemStack();

            BlockState blockstate = level.getBlockState(blockpos);
            BlockState blockstate1 = blockstate.getBlock().getToolModifiedState(blockstate, context, net.minecraftforge.common.ToolActions.SHOVEL_FLATTEN, true);
            if (blockstate1 != null && clickedFace != Direction.DOWN && Util.blockAllowsPathBelow(level, blockpos.above())) {

                if (!level.isClientSide) {
                    level.setBlock(blockpos, blockstate1, 11);
                    blockstate1.updateNeighbourShapes(level, blockpos, 11 & -34, 511);

                    player.swing(hand, true);
                    level.playSound(player, blockpos.getX(), blockpos.getY(), blockpos.getZ(), SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);

                    if (player != null) {
                        itemstack.hurtAndBreak(1, player, (p_43122_) -> {
                            p_43122_.broadcastBreakEvent(hand);
                        });
                    }
                }
            }
        }
    }
}
