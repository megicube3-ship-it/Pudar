package id.nolavalight.mixin;

import id.nolavalight.LavaLightFix;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings", remap = false)
public class IrisWorldSettingsMixin {
    @Inject(method = "setBlockStateIds", at = @At("HEAD"), remap = false, require = 0)
    private void nolavalight$strip(Object2IntMap<BlockState> ids, CallbackInfo ci) {
        LavaLightFix.stripFromShaderMap(ids);
    }
}
