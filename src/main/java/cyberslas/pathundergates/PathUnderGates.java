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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(PathUnderGates.MODID)
public class PathUnderGates {
    public static final String MODID = "@modid@";

    public static Logger logger = LogManager.getLogger();

    public PathUnderGates() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processComms);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.CONFIG_SPEC);
    }

    private void processComms(InterModProcessEvent event) {
        for(InterModComms.IMCMessage message : InterModComms.getMessages(MODID).toList()) {
            if (message.method().equals("registerpath")) {
                Object rawMessageData = message.messageSupplier().get();
                if (rawMessageData instanceof Object[]) {
                    Object[] arrayData = (Object[])rawMessageData;

                    if (arrayData.length > 1 && arrayData[0] instanceof Block && arrayData[1] instanceof BlockState) {
                        ParsedConfig.addBlockPathMapping((Block)arrayData[0], (BlockState)arrayData[1]);
                    }
                }
            }
        }
    }
}