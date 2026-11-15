package id.stev.stevmenu.mixin;

import id.stev.stevmenu.StevConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// ItemInHandRenderer.renderArmWithItem (dipanggil per tangan)
@Mixin(targets = "net.minecraft.class_759")
public class HandMixin {
    @Inject(method = "method_3228", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void stev$hideHand(CallbackInfo ci) {
        if (StevConfig.get().hideHands) ci.cancel();
    }
}
