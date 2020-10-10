package cyberslas.pathundergates;

import cyberslas.pathundergates.block.ModBlocks;
import cyberslas.pathundergates.item.ModItems;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(PathUnderGates.MODID)
public class PathUnderGates {
    public static final String MODID = "pathundergates";

    public static Logger logger = LogManager.getLogger();

    public PathUnderGates() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, PUGConfig.CONFIG_SPEC);

        new ModBlocks();
        new ModItems();
    }

    private void setup(final FMLCommonSetupEvent e) {
    }
}