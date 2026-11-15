package id.stev.stevmenu.mixin;

import id.stev.stevmenu.StevConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Label (nametag) armor stand = hologram klasik
@Mixin(targets = "net.minecraft.class_897")
public class EntityLabelMixin {
    @Inject(method = "method_3926", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void stev$label(@Coerce Object state, @Coerce Object pose, @Coerce Object queue, @Coerce Object camera, CallbackInfo ci) {
        if (StevConfig.get().hideHolograms && state != null
                && state.getClass().getName().equals("net.minecraft.class_9998")) ci.cancel();
    }
}
