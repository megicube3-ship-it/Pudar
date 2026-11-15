package id.stev.stevmenu;

import com.mojang.brigadier.Command;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionResult;

import java.util.Set;

public class StevMenuMod implements ClientModInitializer {
    private static volatile boolean openRequested = false;

    /** Item yang diblok oleh Torch Lock (sama dengan mod aslinya). */
    private static final Set<String> LOCKED = Set.of(
            "minecraft:torch", "minecraft:soul_torch", "minecraft:redstone_torch", "minecraft:copper_torch",
            "minecraft:lantern", "minecraft:soul_lantern", "minecraft:copper_lantern",
            "minecraft:exposed_copper_lantern", "minecraft:weathered_copper_lantern",
            "minecraft:oxidized_copper_lantern", "minecraft:waxed_copper_lantern",
            "minecraft:waxed_exposed_copper_lantern", "minecraft:waxed_weathered_copper_lantern",
            "minecraft:waxed_oxidized_copper_lantern");

    @Override
    public void onInitializeClient() {
        StevConfig.load();
        LavaFix.applyConfig();

        // /stevmenu -> buka menu (dibuka tick berikutnya supaya chat sempat tertutup)
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal("stevmenu").executes(ctx -> {
                    openRequested = true;
                    return Command.SINGLE_SUCCESS;
                })));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (openRequested && mc.screen == null) {
                openRequested = false;
                mc.setScreen(new StevMenuScreen(null));
            }
        });

        // Torch lock: blok pasang torch/lantern kalau aktif
        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            if (level.isClientSide() && StevConfig.get().torchLock) {
                var stack = player.getItemInHand(hand);
                if (!stack.isEmpty() && LOCKED.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString())) {
                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.PASS;
        });

        // Simpan lagi saat game ditutup (jaga-jaga)
        ClientLifecycleEvents.CLIENT_STOPPING.register(mc -> StevConfig.save());
    }
}
