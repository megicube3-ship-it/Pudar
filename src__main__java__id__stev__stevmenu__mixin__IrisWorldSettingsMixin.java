package id.stev.stevmenu.mixin;

import id.stev.stevmenu.LavaFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings", remap = false)
public class IrisWorldSettingsMixin {
    @Inject(method = "setBlockStateIds", at = @At("HEAD"), remap = false, require = 0)
    private void stev$strip(@Coerce Object map, CallbackInfo ci) {
        LavaFix.stripFromShaderMap(map);
    }
}
