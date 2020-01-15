package cyberslas.pathundergates.proxy;

import cyberslas.pathundergates.block.ModBlocks;
import cyberslas.pathundergates.event.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ServerProxy {
    public void preInit(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(EventHandler.class);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(ModBlocks.GRASS_PATH);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlock(ModBlocks.GRASS_PATH).setRegistryName(ModBlocks.GRASS_PATH.getRegistryName()));
    }
}