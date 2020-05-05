package cyberslas.pathundergates;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
}
