package id.stev.stevmenu.mixin;

import id.stev.stevmenu.StevConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.class_338")
public class ChatMixin {
    @Inject(method = "method_75804(Lnet/minecraft/class_332;Lnet/minecraft/class_327;IIIZZ)V",
            at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void stev$chat(@Coerce Object g, @Coerce Object font, int a, int b, int c, boolean d, boolean e, CallbackInfo ci) {
        if (StevConfig.get().hideChat) ci.cancel();
    }
}
