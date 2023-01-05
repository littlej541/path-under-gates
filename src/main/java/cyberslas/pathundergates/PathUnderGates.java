package cyberslas.pathundergates;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(PathUnderGates.MODID)
public class PathUnderGates {
    public static final String MODID = "@modid@";

    public static Logger logger = LogManager.getLogger();

    public PathUnderGates() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, PUGConfig.CONFIG_SPEC);
    }
}