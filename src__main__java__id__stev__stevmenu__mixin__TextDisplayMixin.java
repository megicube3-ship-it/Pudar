package id.stev.stevmenu.mixin;

import id.stev.stevmenu.StevConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Text display entity (hologram modern)
@Mixin(targets = "net.minecraft.class_8138$class_8141")
public class TextDisplayMixin {
    @Inject(method = "method_49056(Lnet/minecraft/class_10071;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;IF)V",
            at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void stev$text(@Coerce Object state, @Coerce Object pose, @Coerce Object queue, int light, float delta, CallbackInfo ci) {
        if (StevConfig.get().hideHolograms) ci.cancel();
    }
}
