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

        public Server(ForgeConfigSpec.Builder builder) {
            blocksWhitelist = builder
                    .comment("A list of blocks that allow paths under them. Format: modid:name:propertyname1=propertyvalue1,propertyname2=propertyvalue2,...",
                            "Examples: \"ore:glass\", \"minecraft:*\", \"minecraft:stone\", \"minecraft:oak_stairs:facing=east\", \"minecraft:oak_stairs:half=top\", \"minecraft:oak_stairs:facing=east,half=top\"",
                            "Each entry must be surrounded by double quotes")
                    .translation(PathUnderGates.MODID + ".config.blocksWhitelist")
                    .defineList("blocksWhitelist", new ArrayList<String>(), __ -> true);

            blocksBlacklist = builder
                    .comment("A list of blocks that disallow paths under them. Format: modid:name:propertyname1=propertyvalue1,propertyname2=propertyvalue2,...",
                            "Examples: \"ore:glass\", \"minecraft:*\", \"minecraft:stone\", \"minecraft:oak_stairs:facing=east\", \"minecraft:oak_stairs:half=top\", \"minecraft:oak_stairs:facing=east,half=top\"",
                            "Each entry must be surrounded by double quotes")
                    .translation(PathUnderGates.MODID + ".config.blocksBlacklist")
                    .defineList("blocksBlacklist", new ArrayList<String>(), __ -> true);
        }
    }
}
