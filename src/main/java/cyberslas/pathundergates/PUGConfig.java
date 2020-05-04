package cyberslas.pathundergates;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = PathUnderGates.MODID)
public class PUGConfig {
    @Comment({"A list of blocks that allow paths under them. Format: modid:name:propertyname1=propertyvalue1,propertyname2=propertyvalue2,...",
            "Examples: minecraft:glass, minecraft:*, minecraft:stone:variant=granite, minecraft:oak_stairs:facing=east, minecraft:oak_stairs:half=top, minecraft:oak_stairs:facing=east,half=top"})
    public static String[] blocksWhitelist = {};

    @Comment({"A list of blocks that disallow paths under them. Format: modid:name:propertyname1=propertyvalue1,propertyname2=propertyvalue2,...",
            "Examples: minecraft:glass, minecraft:*, minecraft:stone:variant=granite, minecraft:oak_stairs:facing=east, minecraft:oak_stairs:half=top, minecraft:oak_stairs:facing=east,half=top"})
    public static String[] blocksBlacklist = {};

    @SubscribeEvent
    public static void onConfigChanged(OnConfigChangedEvent event) {
        if (event.getModID().equals(PathUnderGates.MODID)) {
            ConfigManager.sync(PathUnderGates.MODID, Config.Type.INSTANCE);
        }
    }
}
