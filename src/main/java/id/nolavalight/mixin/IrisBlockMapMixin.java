package id.nolavalight.mixin;

import id.nolavalight.LavaLightFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "net.irisshaders.iris.shaderpack.materialmap.BlockMaterialMapping", remap = false)
public class IrisBlockMapMixin {
    @Inject(method = "createBlockStateIdMap", at = @At("RETURN"), remap = false, require = 0)
    private static void nolavalight$strip(CallbackInfoReturnable<Object> cir) {
        LavaLightFix.stripFromShaderMap(cir.getReturnValue());
    }
}
