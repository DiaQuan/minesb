package com.destroyerr.minesb.mixin;

import com.destroyerr.minesb.module.XRay;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockState.class)
public abstract class BlockStateMixin {

    @Inject(method = "isOpaque", at = @At("HEAD"), cancellable = true)
    private void minesb$isOpaque(CallbackInfoReturnable<Boolean> cir) {
        if (XRay.enabled) {
            BlockState self = (BlockState) (Object) this;
            boolean visible = XRay.VISIBLE.contains(self.getBlock());
            cir.setReturnValue(visible && self.getBlock() != Blocks.AIR);
        }
    }

    @Inject(method = "isOpaqueFullCube", at = @At("HEAD"), cancellable = true)
    private void minesb$isOpaqueFullCube(CallbackInfoReturnable<Boolean> cir) {
        if (XRay.enabled) {
            BlockState self = (BlockState) (Object) this;
            cir.setReturnValue(XRay.VISIBLE.contains(self.getBlock()));
        }
    }
}
