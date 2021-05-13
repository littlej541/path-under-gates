package cyberslas.pathundergates.proxy;

import cyberslas.pathundergates.event.EventHandler;
import cyberslas.pathundergates.PUGConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(PUGConfig.class);
        MinecraftForge.EVENT_BUS.register(EventHandler.class);
    }
}
