package cyberslas.pathundergates;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class PUGConfig {
    public static final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    public static final Config CONFIG = new Config(builder);
    public static final ForgeConfigSpec CONFIG_SPEC = builder.build();

    public static class Config {
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> blocksWhitelist;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> blocksBlacklist;

        public Config(ForgeConfigSpec.Builder builder) {
            blocksWhitelist = builder
                    .comment("A list of blocks that allow paths under them. Format: modid:name:propertyname1=propertyvalue1,propertyname2=propertyvalue2,...",
                            "Examples: minecraft:glass, minecraft:*, minecraft:stone:variant=granite, minecraft:oak_stairs:facing=east, minecraft:oak_stairs:half=top, minecraft:oak_stairs:facing=east,half=top")
                    .translation(PathUnderGates.MODID + ".config.blocksWhitelist")
                    .defineList("blocksWhitelist", new ArrayList<String>(), __ -> true);

            blocksBlacklist = builder
                    .comment("A list of blocks that disallow paths under them. Format: modid:name:propertyname1=propertyvalue1,propertyname2=propertyvalue2,...",
                            "Examples: minecraft:glass, minecraft:*, minecraft:stone:variant=granite, minecraft:oak_stairs:facing=east, minecraft:oak_stairs:half=top, minecraft:oak_stairs:facing=east,half=top")
                    .translation(PathUnderGates.MODID + ".config.blocksBlacklist")
                    .defineList("blocksBlacklist", new ArrayList<String>(), __ -> true);
        }
    }
}
