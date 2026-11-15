package id.stev.stevmenu.mixin;

import id.stev.stevmenu.StevConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.multiplayer.ClientLevel;

// Level: cuaca (client only) + waktu
@Mixin(targets = "net.minecraft.class_1937")
public class LevelMixin {
    @Inject(method = "method_8419", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void stev$isRaining(CallbackInfoReturnable<Boolean> cir) {
        if (!((Object) this instanceof ClientLevel)) return;
        StevConfig.WeatherMode m = StevConfig.get().weatherMode;
        if (m != StevConfig.WeatherMode.SERVER) cir.setReturnValue(m.raining());
    }

    @Inject(method = "method_8546", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void stev$isThundering(CallbackInfoReturnable<Boolean> cir) {
        if (!((Object) this instanceof ClientLevel)) return;
        StevConfig.WeatherMode m = StevConfig.get().weatherMode;
        if (m != StevConfig.WeatherMode.SERVER) cir.setReturnValue(m.thundering());
    }

    @Inject(method = "method_8430", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void stev$rainLevel(float delta, CallbackInfoReturnable<Float> cir) {
        if (!((Object) this instanceof ClientLevel)) return;
        StevConfig.WeatherMode m = StevConfig.get().weatherMode;
        if (m != StevConfig.WeatherMode.SERVER) cir.setReturnValue(m.raining() ? 1.0F : 0.0F);
    }

    @Inject(method = "method_8478", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void stev$thunderLevel(float delta, CallbackInfoReturnable<Float> cir) {
        if (!((Object) this instanceof ClientLevel)) return;
        StevConfig.WeatherMode m = StevConfig.get().weatherMode;
        if (m != StevConfig.WeatherMode.SERVER) cir.setReturnValue(m.thundering() ? 1.0F : 0.0F);
    }

    // getDayTime
    @Inject(method = "method_8532", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private void stev$time(CallbackInfoReturnable<Long> cir) {
        if (!((Object) this instanceof ClientLevel)) return;
        StevConfig c = StevConfig.get();
        if (!c.timeOverride) return;
        long orig = cir.getReturnValue();
        cir.setReturnValue(Math.floorDiv(orig, 24000L) * 24000L + c.timeTicks);
    }
}
