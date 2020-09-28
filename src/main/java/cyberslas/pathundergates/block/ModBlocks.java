package cyberslas.pathundergates.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {
    private static final DeferredRegister<Block> BLOCKS_OVERRIDE = DeferredRegister.create(ForgeRegistries.BLOCKS, Blocks.GRASS_PATH.getRegistryName().getNamespace());

    public static final RegistryObject<Block> PUG_GRASS_PATH = BLOCKS_OVERRIDE.register(Blocks.GRASS_PATH.getRegistryName().getPath(), () -> new PUGGrassPathBlock(Block.Properties.from(Blocks.GRASS_PATH)));

    public ModBlocks() {
        BLOCKS_OVERRIDE.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
