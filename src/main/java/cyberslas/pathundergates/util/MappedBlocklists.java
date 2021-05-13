package cyberslas.pathundergates.util;

import cyberslas.pathundergates.PathUnderGates;
import cyberslas.pathundergates.PUGConfig;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;
import java.util.stream.Collectors;

public class MappedBlocklists {
    public static Multimap<ResourceLocation, List<String>> whitelistMap = HashMultimap.create();
    public static Multimap<ResourceLocation, List<String>> backlistMap = HashMultimap.create();

    public static final String WILDCARD = "*";
    public static final String OREDICTDOMAIN = "ore";
    public static final String DOMAINSEPARATOR = ":";
    public static final String PROPERTYSEPARATOR = ",";
    public static final String PROPERTYKEYVALUESEPARATOR = "=";
    public static final ResourceLocation DUMMYMAPKEY = new ResourceLocation(PathUnderGates.MODID, "DUMMY");

    public static void processListsIntoMaps() {
        whitelistMap = processListIntoMap(PUGConfig.blocksWhitelist);
        backlistMap = processListIntoMap(PUGConfig.blocksBlacklist);
    }

    private static Multimap<ResourceLocation, List<String>> processListIntoMap(String[] list) {
        Multimap<ResourceLocation, List<String>> multimap = HashMultimap.create();

        for(String registryName : list) {
            String[] splitRegistryName = registryName.split(DOMAINSEPARATOR);

            if (splitRegistryName.length == 1) {
                multimap.put(new ResourceLocation(splitRegistryName[0], WILDCARD), Collections.singletonList(WILDCARD));
            } else if (splitRegistryName.length == 2) {
                if (splitRegistryName[0].equals(OREDICTDOMAIN)) {
                    for(ItemStack oreDictEntry : OreDictionary.getOres(splitRegistryName[1])) {
                        List<String> propertiesList = new ArrayList<>();

                        if (oreDictEntry.getMetadata() == OreDictionary.WILDCARD_VALUE) {
                            propertiesList.add(WILDCARD);
                        } else {
                            propertiesList.add(String.valueOf(oreDictEntry.getMetadata()));
                        }

                        multimap.put(oreDictEntry.getItem().getRegistryName(), propertiesList);
                    }
                } else {
                    multimap.put(new ResourceLocation(registryName), Collections.singletonList(WILDCARD));
                }
            } else if (splitRegistryName.length == 3) {
                ResourceLocation resourceLocation = new ResourceLocation(splitRegistryName[0], splitRegistryName[1]);
                List<String> propertiesList = Arrays.asList(splitRegistryName[2].split(PROPERTYSEPARATOR));

                multimap.put(resourceLocation, propertiesList);
            }
        }

        return multimap;
    }

    public static boolean matchesBlockWhitelist(World worldIn, BlockPos pos) {
        return matchesBlockMap(worldIn, pos, MappedBlocklists.whitelistMap);
    }

    public static boolean matchesBlockBlacklist(World worldIn, BlockPos pos) {
        return matchesBlockMap(worldIn, pos, MappedBlocklists.backlistMap);
    }

    private static boolean matchesBlockMap(World worldIn, BlockPos pos, Multimap<ResourceLocation, List<String>> map) {
        IBlockState iBlockState = worldIn.getBlockState(pos);
        ResourceLocation blockRegistryName = iBlockState.getBlock().getRegistryName();

        ResourceLocation key = map.containsKey(blockRegistryName) ? blockRegistryName : map.containsKey(new ResourceLocation(blockRegistryName.getResourceDomain(), MappedBlocklists.WILDCARD)) ? new ResourceLocation(blockRegistryName.getResourceDomain(), MappedBlocklists.WILDCARD) : MappedBlocklists.DUMMYMAPKEY;

        for (List<String> propertyList : map.get(key)) {
            if (!propertyList.get(0).contains(MappedBlocklists.PROPERTYKEYVALUESEPARATOR)) {
                if (propertyList.get(0).equals(MappedBlocklists.WILDCARD) || propertyList.get(0).equals(String.valueOf(iBlockState.getBlock().getMetaFromState(iBlockState)))) {
                    return true;
                }
            } else {
                boolean blocksMatch = true;
                Map<String, String> propertyMap = propertyList.stream().map(input -> input.split(MappedBlocklists.PROPERTYKEYVALUESEPARATOR)).collect(Collectors.toMap(v -> v[0], v -> v[1]));

                for (Map.Entry<IProperty<?>, Comparable<?>> blockStateProperty : iBlockState.getProperties().entrySet()) {
                    if (propertyMap.containsKey(blockStateProperty.getKey().getName())) {
                        if (!propertyMap.get(blockStateProperty.getKey().getName()).equals(blockStateProperty.getValue().toString())) {
                            blocksMatch = false;
                            break;
                        }
                    }
                }

                if (blocksMatch) {
                    return true;
                }
            }
        }

        return false;
    }
}
