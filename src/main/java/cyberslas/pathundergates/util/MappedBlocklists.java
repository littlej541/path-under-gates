package cyberslas.pathundergates.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.tuple.Pair;
import cyberslas.pathundergates.Config;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.tags.BlockTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.stream.Collectors;

public class MappedBlocklists {
    private static Multimap<DomainNamePair, List<String>> whitelistMap = HashMultimap.create();
    private static Multimap<DomainNamePair, List<String>> blacklistMap = HashMultimap.create();

    public static void parseConfig() {
        whitelistMap = ConfigParser.processListIntoMap(Config.SERVER.blocksWhitelist.get());
        blacklistMap = ConfigParser.processListIntoMap(Config.SERVER.blocksBlacklist.get());
    }

    public static boolean matchesBlockWhitelist(LevelReader worldIn, BlockPos pos) {
        return matchesBlockMap(worldIn, pos, MappedBlocklists.whitelistMap);
    }

    public static boolean matchesBlockBlacklist(LevelReader worldIn, BlockPos pos) {
        return matchesBlockMap(worldIn, pos, MappedBlocklists.blacklistMap);
    }

    private static boolean matchesBlockMap(LevelReader worldIn, BlockPos pos, Multimap<DomainNamePair, List<String>> map) {
        BlockState blockState = worldIn.getBlockState(pos);
        DomainNamePair blockDomainNamePair = new DomainNamePair(blockState.getBlock().getRegistryName());

        if (!map.containsKey(blockDomainNamePair)) {
            blockDomainNamePair = new DomainNamePair(blockDomainNamePair.getDomain(), ConfigParser.WILDCARD);
            if (!map.containsKey(blockDomainNamePair)) {
                return false;
            } else {
                return true;
            }
        }

        for (List<String> propertyList : map.get(blockDomainNamePair)) {
            if (!propertyList.get(0).contains(ConfigParser.PROPERTYKEYVALUESEPARATOR)) {
                if (propertyList.get(0).equals(ConfigParser.WILDCARD)) {
                    return true;
                }
            } else {
                Map<String, String> propertyMap = propertyList.stream().map(input -> input.split(ConfigParser.PROPERTYKEYVALUESEPARATOR)).collect(Collectors.toMap(v -> v[0], v -> v[1]));

                for (Property<?> blockStateProperty : blockState.getProperties()) {
                    if (propertyMap.containsKey(blockStateProperty.getName())) {
                        if (!propertyMap.get(blockStateProperty.getName()).equals(blockState.getValue(blockStateProperty).toString())) {
                            return false;
                        }
                    }
                }

                return true;
            }
        }

        return false;
    }

    private static class ConfigParser {
        private static final String WILDCARD = "*";
        private static final String TAGSTART = "#";
        private static final String OREDOMAIN = "ore";
        private static final List<String> DEFAULTTAGDOMAINS = Arrays.asList("minecraft", "forge");
        private static final String DOMAINSEPARATOR = ":";
        private static final String PROPERTYSEPARATOR = ",";
        private static final String PROPERTYKEYVALUESEPARATOR = "=";

        private static Multimap<DomainNamePair, List<String>> processListIntoMap(List<? extends String> list) {
            Multimap<DomainNamePair, List<String>> multimap = HashMultimap.create();

            for(String registryName : list) {
                Pair<String, String> domainAndBlockState = splitAtDomainSeparator(registryName);
                Pair<String, String> blockNameAndState = splitAtDomainSeparator(domainAndBlockState.getRight());
                String domain = domainAndBlockState.getLeft();
                String name = blockNameAndState.getLeft();
                String state = blockNameAndState.getRight();
                boolean tagHint = false;

                if (domain.startsWith(TAGSTART)) {
                    domain = domain.substring(TAGSTART.length());
                    tagHint = true;
                }

                if (state.equals(WILDCARD)) {
                    List<String> domainsToCheck;
                    Set<Block> blockSet = new HashSet();

                    if (domain.equals(OREDOMAIN)) {
                        domainsToCheck = DEFAULTTAGDOMAINS;
                        tagHint = true;
                    } else {
                        domainsToCheck = Arrays.asList(domain);
                    }

                    domainsToCheck
                            .stream()
                            .map(domainToCheck -> ForgeRegistries.BLOCKS.tags().getTag(BlockTags.create(new ResourceLocation(domainToCheck, name))))
                            .forEach(tag -> blockSet.addAll(tag.stream().toList()));

                    if (blockSet.size() > 0) {
                        for (Block block : blockSet) {
                            multimap.put(new DomainNamePair(block.getRegistryName()), Collections.singletonList(state));
                        }
                    } else if (!tagHint) {
                        multimap.put(new DomainNamePair(domain, name), Collections.singletonList(state));
                    }
                } else {
                    DomainNamePair domainNamePair = new DomainNamePair(domain, name);
                    List<String> propertiesList = processStateIntoPropertiesList(state);

                    multimap.put(domainNamePair, propertiesList);
                }
            }

            return multimap;
        }

        private static Pair<String, String> splitAtDomainSeparator(String entry) {
            String[] splitPair = entry.split(DOMAINSEPARATOR, 2);

            if (splitPair.length == 1) {
                return Pair.of(splitPair[0], WILDCARD);
            }

            return Pair.of(splitPair[0], splitPair[1]);
        }

        private static List<String> processStateIntoPropertiesList(String properties) {
            return Arrays.asList(properties.split(PROPERTYSEPARATOR));
        }
    }
}
