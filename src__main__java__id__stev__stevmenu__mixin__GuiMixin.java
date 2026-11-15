package id.stev.stevmenu.mixin;

import id.stev.stevmenu.StevConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Mixin(targets = "net.minecraft.class_329")
public class GuiMixin {
    // renderHotbarItem: icon + jumlah item offhand
    @Inject(method = "method_1762", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void stev$offhand(@Coerce Object g, int x, int y, @Coerce Object dt, Player player, ItemStack stack, int seed, CallbackInfo ci) {
        if (StevConfig.get().hideOffhandSlot && player != null && stack == player.getOffhandItem()) ci.cancel();
    }

    // renderScoreboardSidebar
    @Inject(method = "method_1757", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void stev$scoreboard(@Coerce Object g, @Coerce Object objective, CallbackInfo ci) {
        if (StevConfig.get().hideScoreboard) ci.cancel();
    }
}
