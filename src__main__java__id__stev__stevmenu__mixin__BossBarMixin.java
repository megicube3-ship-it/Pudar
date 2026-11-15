package id.stev.stevmenu.mixin;

import id.stev.stevmenu.StevConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.class_337")
public class BossBarMixin {
    @Inject(method = "method_1796", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void stev$bossbar(@Coerce Object g, CallbackInfo ci) {
        if (StevConfig.get().hideBossBar) ci.cancel();
    }
}
