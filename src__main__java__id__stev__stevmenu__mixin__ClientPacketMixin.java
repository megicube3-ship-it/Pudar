package id.stev.stevmenu.mixin;

import id.stev.stevmenu.LavaFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.class_634")
public class ClientPacketMixin {
    @Inject(method = "method_38543(IILnet/minecraft/class_6606;Z)V", at = @At("TAIL"), remap = false, require = 0)
    private void stev$afterLight(int cx, int cz, @Coerce Object data, boolean unused, CallbackInfo ci) {
        LavaFix.queue(cx, cz);
    }
}
