package cyberslas.pathundergates.item;

import cyberslas.pathundergates.block.ModBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    private static final DeferredRegister<Item> ITEMBLOCKS_OVERRIDE = new DeferredRegister<>(ForgeRegistries.ITEMS, Blocks.GRASS_PATH.getRegistryName().getNamespace());

    public static final RegistryObject<Item> PUG_GRASS_PATH = ITEMBLOCKS_OVERRIDE.register(Blocks.GRASS_PATH.getRegistryName().getPath(), () -> new BlockItem(ModBlocks.PUG_GRASS_PATH.get(), new Item.Properties().group(ItemGroup.DECORATIONS)));

    public ModItems() {
        ITEMBLOCKS_OVERRIDE.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
