package cyberslas.pathundergates.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import cpw.mods.modlauncher.api.INameMappingService;
import cyberslas.pathundergates.PathUnderGates;
import net.minecraft.world.item.ShovelItem;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import cyberslas.pathundergates.Config;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.tags.BlockTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class ParsedConfig {
    private static Multimap<DomainNamePair, List<String>> whitelistMap = HashMultimap.create();
    private static Multimap<DomainNamePair, List<String>> blacklistMap = HashMultimap.create();
    private static final Map<Block, BlockState> modAddedBlockPathMap = new HashMap<>();
    private static final String FLATTENABLESFIELDSRG = "f_43110_";

    private final static Map<Block, BlockState> CACHEDFLATTENABLES;
    private final static Map<Block, BlockState> FLATTENABLESFIELD;
    static {
        Field field = ObfuscationReflectionHelper.findField(ShovelItem.class, FLATTENABLESFIELDSRG);
        Map<Block, BlockState> temp = new HashMap<>();
        try {
            temp = (Map<Block, BlockState>)field.get(null);
        } catch(Exception e) {
            PathUnderGates.logger.error("Failed to reflect field " + ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, FLATTENABLESFIELDSRG) + ", options defined in blockPathPairList will not work");
        }
        FLATTENABLESFIELD = temp;
        CACHEDFLATTENABLES = new HashMap<>(FLATTENABLESFIELD);
    }

    public static void addBlockPathMapping(Block block, BlockState blockState) {
        modAddedBlockPathMap.put(block, blockState);
    }

    public static void parseConfig() {
        FLATTENABLESFIELD.clear();
        FLATTENABLESFIELD.putAll(CACHEDFLATTENABLES);

        whitelistMap = ConfigParser.processListIntoMap(Config.SERVER.blocksWhitelist.get());
        blacklistMap = ConfigParser.processListIntoMap(Config.SERVER.blocksBlacklist.get());
        Map<Block, BlockState> blockPathMap = ConfigParser.processBlockPathPairListIntoMap(Config.SERVER.blockPathList.get());
        blockPathMap.putAll(modAddedBlockPathMap);
        FLATTENABLESFIELD.putAll(blockPathMap);
    }

    public static boolean matchesBlockWhitelist(BlockState blockState) {
        return matchesBlockMap(blockState, whitelistMap);
    }

    public static boolean matchesBlockBlacklist(BlockState blockState) {
        return matchesBlockMap(blockState, blacklistMap);
    }

    private static boolean matchesBlockMap(BlockState blockState, Multimap<DomainNamePair, List<String>> map) {
        DomainNamePair blockDomainNamePair = new DomainNamePair(blockState.getBlock().getRegistryName());

        if (!map.containsKey(blockDomainNamePair)) {
            blockDomainNamePair = new DomainNamePair(blockDomainNamePair.getDomain(), ConfigParser.WILDCARD);
            return map.containsKey(blockDomainNamePair);
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
        private static final String PAIRSEPARATOR = "\\|";
        private static final Block BADBLOCK = Blocks.AIR;
        private static final BlockState BADBLOCKSTATE = BADBLOCK.defaultBlockState();

        private static Multimap<DomainNamePair, List<String>> processListIntoMap(List<? extends String> list) {
            Multimap<DomainNamePair, List<String>> multimap = HashMultimap.create();

            for(String registryName : list) {
                ImmutablePair<String, String> domainAndBlockState = splitAtDomainSeparator(registryName);
                ImmutablePair<String, String> blockNameAndState = splitAtDomainSeparator(domainAndBlockState.getRight());
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
                    Set<Block> blockSet = new HashSet<>();

                    if (domain.equals(OREDOMAIN)) {
                        domainsToCheck = DEFAULTTAGDOMAINS;
                        tagHint = true;
                    } else {
                        domainsToCheck = Collections.singletonList(domain);
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

        private static Map<Block, BlockState> processBlockPathPairListIntoMap(List<? extends String> list) {
            Map<Block, BlockState> map = new HashMap<>();

            for(String pair : list) {
                String[] splitPair = pair.split(PAIRSEPARATOR);

                Block block = blockStateEntryToBlock(splitAtDomainSeparator(splitPair[0]));
                if (block.equals(BADBLOCK)) {
                    continue;
                }
                BlockState blockState = blockStateEntryToBlockstate(splitAtDomainSeparator(splitPair[1]));
                if (blockState.equals(BADBLOCKSTATE)) {
                    continue;
                }

                map.put(block, blockState);
            }

            return map;
        }

        private static Block blockStateEntryToBlock(ImmutablePair<String, String> entry) {
            ImmutablePair<String, String> blockNameAndState = splitAtDomainSeparator(entry.getRight());
            String domain = entry.getLeft();
            String name = blockNameAndState.getLeft();

            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(domain, name));
            if (block == null) {
                block = BADBLOCK;
            }

            return  block;
        }

        private static BlockState blockStateEntryToBlockstate(ImmutablePair<String, String> entry) {
            Block block = blockStateEntryToBlock(entry);

            if (block.equals(BADBLOCK)) {
                return BADBLOCKSTATE;
            }

            String state = splitAtDomainSeparator(entry.getRight()).getRight();
            List<String> propertiesList = processStateIntoPropertiesList(state);

            BlockState blockState = block.defaultBlockState();
            if (!propertiesList.get(0).contains(PROPERTYKEYVALUESEPARATOR)) {
                return blockState;
            } else {
                Map<String, String> propertyMap = propertiesList.stream().map(input -> input.split(PROPERTYKEYVALUESEPARATOR)).collect(Collectors.toMap(v -> v[0], v -> v[1]));

                for (Property<?> blockStateProperty : blockState.getProperties()) {
                    if (propertyMap.containsKey(blockStateProperty.getName())) {
                        blockState = newBlockStateWithProperty(blockState, blockStateProperty, propertyMap.get(blockStateProperty.getName()));
                    }
                }
            }

            return blockState;
        }

        private static <T extends Comparable<T>> BlockState newBlockStateWithProperty(BlockState state, Property<T> property, String stringValue) {
            Optional<T> value = property.getValue(stringValue);
            return value.map(t -> state.setValue(property, t)).orElse(state);

        }

        private static ImmutablePair<String, String> splitAtDomainSeparator(String entry) {
            String[] splitPair = entry.split(DOMAINSEPARATOR, 2);

            if (splitPair.length == 1) {
                return ImmutablePair.of(splitPair[0], WILDCARD);
            }

            return ImmutablePair.of(splitPair[0], splitPair[1]);
        }

        private static List<String> processStateIntoPropertiesList(String properties) {
            return Arrays.asList(properties.split(PROPERTYSEPARATOR));
        }
    }
}
