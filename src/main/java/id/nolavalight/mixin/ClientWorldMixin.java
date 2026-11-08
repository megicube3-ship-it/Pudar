package id.nolavalight.mixin;

import java.util.function.BooleanSupplier;

import id.nolavalight.LavaLightFix;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.class_638")
public class ClientWorldMixin {
    @Inject(method = "method_8441(Ljava/util/function/BooleanSupplier;)V", at = @At("TAIL"), remap = false)
    private void nolavalight$tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        LavaLightFix.tick((ClientWorld) (Object) this);
    }
}
