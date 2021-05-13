package cyberslas.pathundergates.proxy;

import cyberslas.pathundergates.PUGConfig;
import cyberslas.pathundergates.event.EventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod.EventBusSubscriber
public class ServerProxy extends CommonProxy {
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        MinecraftForge.EVENT_BUS.register(PUGConfig.class);
    }
}