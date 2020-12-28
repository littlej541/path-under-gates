package cyberslas.pathundergates.mixin;

import cyberslas.pathundergates.util.Util;
import net.minecraft.block.BlockState;
import net.minecraft.block.GrassPathBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(GrassPathBlock.class)
public abstract class MixinGrassPathBlock {
    @Inject(at = @At("HEAD"), method = "isValidPosition", cancellable = true)
    private void onIsValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos, CallbackInfoReturnable<Boolean> callback) {
        if (Util.blockAllowsPathBelow(worldIn, pos.up())) {
            callback.setReturnValue(true);
        }
    }

    @Inject(at = @At("HEAD"), method = "tick", cancellable = true)
    private void onTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand, CallbackInfo callback) {
        if (this.isValidPosition(state, worldIn, pos)) {
            callback.cancel();
        }
    }

    @Shadow
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return false;
    }
}
