package cyberslas.pathundergates.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import cyberslas.pathundergates.PUGConfig;
import cyberslas.pathundergates.PathUnderGates;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.Property;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import java.util.*;
import java.util.stream.Collectors;

public class MappedBlocklists {
    public static Multimap<DomainNamePair, List<String>> whitelistMap = HashMultimap.create();
    public static Multimap<DomainNamePair, List<String>> blacklistMap = HashMultimap.create();

    public static final String WILDCARD = "*";
    public static final String TAGSTART = "#";
    public static final String OREDOMAIN = "ore";
    public static final List<String> DEFAULTTAGDOMAINS = Arrays.asList("minecraft", "forge");
    public static final String DOMAINSEPARATOR = ":";
    public static final String PROPERTYSEPARATOR = ",";
    public static final String PROPERTYKEYVALUESEPARATOR = "=";
    public static final DomainNamePair DUMMYMAPKEY = new DomainNamePair(PathUnderGates.MODID, "dummy");

    public static void processListsIntoMaps() {
        whitelistMap = processListIntoMap(PUGConfig.CONFIG.blocksWhitelist.get());
        blacklistMap = processListIntoMap(PUGConfig.CONFIG.blocksBlacklist.get());
    }

    private static Multimap<DomainNamePair, List<String>> processListIntoMap(List<? extends String> list) {
        Multimap<DomainNamePair, List<String>> multimap = HashMultimap.create();

        for(String registryName : list) {
            String[] splitRegistryName = registryName.split(DOMAINSEPARATOR);
            if (splitRegistryName[0].startsWith(TAGSTART)) {
                splitRegistryName[0] = splitRegistryName[0].substring(TAGSTART.length());
            }

            if (splitRegistryName.length == 1) {
                multimap.put(new DomainNamePair(splitRegistryName[0], WILDCARD), Collections.singletonList(WILDCARD));
            } else if (splitRegistryName.length == 2) {
                List<String> domainsToCheck;
                Collection<Block> blockSet = new HashSet();

                if (splitRegistryName[0].equals(OREDOMAIN)) {
                    domainsToCheck = DEFAULTTAGDOMAINS;
                } else {
                    domainsToCheck = Arrays.asList(splitRegistryName[0]);
                }

                domainsToCheck.stream().map(domain -> BlockTags.getCollection().get(new ResourceLocation(domain, splitRegistryName[1]))).filter(tag -> tag != null).forEach(tag -> blockSet.addAll(tag.getAllElements()));

                if (blockSet.size() > 0) {
                    for (Block block : blockSet) {
                        multimap.put(new DomainNamePair(block.getRegistryName()), Collections.singletonList(WILDCARD));
                    }
                } else {
                    multimap.put(new DomainNamePair(splitRegistryName[0], splitRegistryName[1]), Collections.singletonList(WILDCARD));
                }
            } else if (splitRegistryName.length == 3) {
                DomainNamePair domainNamePair = new DomainNamePair(splitRegistryName[0], splitRegistryName[1]);
                List<String> propertiesList = Arrays.asList(splitRegistryName[2].split(PROPERTYSEPARATOR));

                multimap.put(domainNamePair, propertiesList);
            }
        }

        return multimap;
    }

    public static boolean matchesBlockWhitelist(IWorldReader worldIn, BlockPos pos) {
        return matchesBlockMap(worldIn, pos, MappedBlocklists.whitelistMap);
    }

    public static boolean matchesBlockBlacklist(IWorldReader worldIn, BlockPos pos) {
        return matchesBlockMap(worldIn, pos, MappedBlocklists.blacklistMap);
    }

    private static boolean matchesBlockMap(IWorldReader worldIn, BlockPos pos, Multimap<DomainNamePair, List<String>> map) {
        BlockState blockState = worldIn.getBlockState(pos);
        DomainNamePair blockDomainNamePair = new DomainNamePair(blockState.getBlock().getRegistryName());

        DomainNamePair key = map.containsKey(blockDomainNamePair) ? blockDomainNamePair : map.containsKey(new DomainNamePair(blockDomainNamePair.domain, MappedBlocklists.WILDCARD)) ? new DomainNamePair(blockDomainNamePair.domain, MappedBlocklists.WILDCARD) : MappedBlocklists.DUMMYMAPKEY;

        for (List<String> propertyList : map.get(key)) {
            if (!propertyList.get(0).contains(MappedBlocklists.PROPERTYKEYVALUESEPARATOR)) {
                if (propertyList.get(0).equals(MappedBlocklists.WILDCARD)) {
                    return true;
                }
            } else {
                boolean blocksMatch = true;
                Map<String, String> propertyMap = propertyList.stream().map(input -> input.split(MappedBlocklists.PROPERTYKEYVALUESEPARATOR)).collect(Collectors.toMap(v -> v[0], v -> v[1]));

                for (Property<?> blockStateProperty : blockState.getProperties()) {
                    if (propertyMap.containsKey(blockStateProperty.getName())) {
                        if (!propertyMap.get(blockStateProperty.getName()).equals(blockState.get(blockStateProperty).toString())) {
                            blocksMatch = false;
                            break;
                        }
                    }
                }

                return blocksMatch;
            }
        }

        return false;
    }
}
