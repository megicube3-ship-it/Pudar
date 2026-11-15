package id.stev.stevmenu.mixin;

import id.stev.stevmenu.StevConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// LevelRenderer.renderHitOutline / block outline
@Mixin(targets = "net.minecraft.class_761")
public class OutlineMixin {
    @Inject(method = "method_62210(Lnet/minecraft/class_4597$class_4598;Lnet/minecraft/class_4587;ZLnet/minecraft/class_11658;)V",
            at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void stev$outline(@Coerce Object buf, @Coerce Object pose, boolean translucent, @Coerce Object state, CallbackInfo ci) {
        if (StevConfig.get().hideBlockOutline) ci.cancel();
    }
}
