package cyberslas.pathundergates;

import cyberslas.pathundergates.util.ParsedConfig;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

@Mod(PathUnderGates.MODID)
public class PathUnderGates {
    public static final String MODID = "@modid@";

    public static Logger logger = LogManager.getLogger();

    public PathUnderGates() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processComms);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.CONFIG_SPEC);
    }

    private void processComms(InterModProcessEvent event) {
        Map<String, ImmutablePair<Block, BlockState>> messageMap = new HashMap<>();

        InterModComms.getMessages(MODID).forEach(message -> {
            String messageKey = message.method();
            Object messageData = message.messageSupplier().get();

            ImmutablePair<Block, BlockState> pair = messageMap.computeIfAbsent(messageKey, (whatever) -> ImmutablePair.nullPair());

            if (messageData instanceof Block) {
                messageMap.put(messageKey, ImmutablePair.of((Block)messageData, pair.getRight()));
            } else if (messageData instanceof BlockState) {
                messageMap.put(messageKey, ImmutablePair.of(pair.getLeft(), (BlockState)messageData));
            }
        });

        messageMap.values().forEach(pair -> {
            Block block = pair.getLeft();
            BlockState blockState = pair.getRight();

            if (block != null && blockState != null) {
                ParsedConfig.addBlockPathMapping(block, blockState);
            }
        });
    }
}