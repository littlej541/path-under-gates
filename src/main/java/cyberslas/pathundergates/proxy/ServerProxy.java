package cyberslas.pathundergates.proxy;

import cyberslas.pathundergates.PUGConfig;
import cyberslas.pathundergates.block.ModBlocks;
import cyberslas.pathundergates.event.EventHandler;
import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ServerProxy {
    public void preInit(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(PUGConfig.class);
        MinecraftForge.EVENT_BUS.register(EventHandler.class);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(ModBlocks.TEMP_GRASS_PATH);
    }
}