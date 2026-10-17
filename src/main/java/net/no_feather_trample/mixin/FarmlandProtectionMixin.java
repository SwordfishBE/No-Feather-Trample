package net.no_feather_trample.mixin;

import net.no_feather_trample.FarmlandProtection;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FarmlandBlock.class)
abstract class FarmlandProtectionMixin {
    // Intercept only the conversion: vanilla still applies fall damage and its other fall logic.
    // Environmental conversions have no entity and must remain possible.
    @Inject(method = "turnToBaseBlock", at = @At("HEAD"), cancellable = true)
    private void no_feather_trample$protectSoil(Entity cause, BlockState state, Level level,
                                             BlockPos pos, CallbackInfo callback) {
        if (FarmlandProtection.protects(cause)) callback.cancel();
    }
}
