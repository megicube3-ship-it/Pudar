package id.stev.stevmenu.mixin;

import id.stev.stevmenu.LavaFix;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.class_638")
public class ClientLevelMixin {
    @Inject(method = "method_8441", at = @At("TAIL"), remap = false, require = 0)
    private void stev$tick(CallbackInfo ci) {
        LavaFix.tick((ClientLevel) (Object) this);
    }
}
