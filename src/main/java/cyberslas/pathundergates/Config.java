package cyberslas.pathundergates;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class Config {
    private static final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    public static final Server SERVER = new Server(builder);
    public static final ForgeConfigSpec CONFIG_SPEC = builder.build();

    public static class Server {
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> blocksWhitelist;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> blocksBlacklist;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> blockPathList;

        public Server(ForgeConfigSpec.Builder builder) {
            blocksWhitelist = builder
                    .comment("A list of blocks that allow paths under them. Format: modid:name:propertyname1=propertyvalue1,propertyname2=propertyvalue2,...",
                            "Examples: \"ore:glass\", \"minecraft:*\", \"minecraft:stone\", \"minecraft:oak_stairs:facing=east\", \"minecraft:oak_stairs:half=top\", \"minecraft:oak_stairs:facing=east,half=top\"",
                            "Each entry must be surrounded by double quotes")
                    .translation(PathUnderGates.MODID + ".config.blocksWhitelist")
                    .defineList("blocksWhitelist", new ArrayList<>(), __ -> true);

            blocksBlacklist = builder
                    .comment("A list of blocks that disallow paths under them. Format: modid:name:propertyname1=propertyvalue1,propertyname2=propertyvalue2,...",
                            "Examples: \"ore:glass\", \"minecraft:*\", \"minecraft:stone\", \"minecraft:oak_stairs:facing=east\", \"minecraft:oak_stairs:half=top\", \"minecraft:oak_stairs:facing=east,half=top\"",
                            "Each entry must be surrounded by double quotes")
                    .translation(PathUnderGates.MODID + ".config.blocksBlacklist")
                    .defineList("blocksBlacklist", new ArrayList<>(), __ -> true);
            blockPathList = builder
                    .comment("A list of block pairs where the first is the normal block and the second the block it is flattened into with the shovel.",
                            "The first block can only be a simple block, properties are ignored. The second can be a block with properties attached like the blacklist/whitelist.",
                            "Blocks pairs are separated by a | character. Cannot use tags or wildcards. Properties not defined are assumed default. Format: modid:name|modid:name:propertyname1=propertyvalue1,propertyname2=propertyvalue2,...",
                            "Intended for compatibility purposes, but nothing stopping you from going nuts and making shovels turn coal blocks into diamonds.",
                            "Examples: \"minecraft:snow_block|morepaths:snow_path\", \"undergarden:deepsoil|ugpaths:deepsoil_path\", \"minecraft:coal_block|minecraft:diamond_block\"",
                            "Each entry must be surrounded by double quotes")
                    .translation(PathUnderGates.MODID + ".config.blockPathPairList")
                    .defineList("blockPathPairList", new ArrayList<>(), __ -> true);
        }
    }
}
