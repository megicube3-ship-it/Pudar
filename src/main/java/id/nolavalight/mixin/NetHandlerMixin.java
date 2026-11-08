package id.nolavalight.mixin;

import id.nolavalight.LavaLightFix;
import net.minecraft.network.packet.s2c.play.LightData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.class_634")
public class NetHandlerMixin {
    @Inject(method = "method_38543(IILnet/minecraft/class_6606;Z)V", at = @At("TAIL"), remap = false)
    private void nolavalight$afterLightData(int chunkX, int chunkZ, LightData data, boolean unused, CallbackInfo ci) {
        LavaLightFix.queue(chunkX, chunkZ);
    }
}
