package id.stev.stevmenu.mixin;

import id.stev.stevmenu.StevConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.biome.Biome;

// WeatherEffectRenderer.getPrecipitationAt: paksa salju / hujan
@Mixin(targets = "net.minecraft.class_9976")
public class WeatherRenderMixin {
    @Inject(method = "method_62317", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void stev$precip(@Coerce Object level, @Coerce Object pos, CallbackInfoReturnable<Object> cir) {
        StevConfig.WeatherMode m = StevConfig.get().weatherMode;
        if (m == StevConfig.WeatherMode.SNOW) cir.setReturnValue(Biome.Precipitation.SNOW);
        else if (m == StevConfig.WeatherMode.RAIN || m == StevConfig.WeatherMode.STORM) cir.setReturnValue(Biome.Precipitation.RAIN);
    }
}
