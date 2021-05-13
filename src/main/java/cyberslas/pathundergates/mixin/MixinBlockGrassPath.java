package cyberslas.pathundergates.mixin;

import cyberslas.pathundergates.util.Util;
import net.minecraft.block.BlockGrassPath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(BlockGrassPath.class)
public abstract class MixinBlockGrassPath {
    @Inject(at = @At("HEAD"), method = "updateBlockState", cancellable = true)
    private void OnUpdateBlockState(World worldIn, BlockPos pos, CallbackInfo callback) {
        if (Util.blockAllowsPathBelow(worldIn, pos.up())) {
            callback.cancel();
        }
    }
}
