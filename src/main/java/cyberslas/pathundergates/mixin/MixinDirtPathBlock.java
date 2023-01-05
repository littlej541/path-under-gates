package cyberslas.pathundergates.mixin;

import cyberslas.pathundergates.util.Util;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.DirtPathBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(DirtPathBlock.class)
public abstract class MixinDirtPathBlock {
    @Inject(at = @At("HEAD"), method = "canSurvive", cancellable = true)
    private void onCanSurvive(BlockState state, LevelReader worldIn, BlockPos pos, CallbackInfoReturnable<Boolean> callback) {
        if (Util.blockAllowsPathBelow(worldIn, pos.above())) {
            callback.setReturnValue(true);
        }
    }

    @Inject(at = @At("HEAD"), method = "tick", cancellable = true)
    private void onTick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand, CallbackInfo callback) {
        if (this.canSurvive(state, worldIn, pos)) {
            callback.cancel();
        }
    }

    @Shadow
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        return false;
    }
}
