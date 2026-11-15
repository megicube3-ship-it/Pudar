package id.stev.stevmenu;

import com.mojang.brigadier.Command;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionResult;

import java.util.Set;

public class StevMenuMod implements ClientModInitializer {
    private static volatile boolean openRequested = false;

    /** Keybind hide/show chat. Default F12, bisa diganti di Options > Controls > Key Binds. */
    private static KeyMapping toggleChatKey;

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

        toggleChatKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.stevmenu.toggle_chat", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F12, KeyMapping.Category.MISC));

        // /stevmenu -> buka menu (dibuka tick berikutnya supaya chat sempat tertutup)
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal("stevmenu").executes(ctx -> {
                    openRequested = true;
                    return Command.SINGLE_SUCCESS;
                })));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (toggleChatKey.consumeClick()) {
                StevConfig c = StevConfig.get();
                c.hideChat = !c.hideChat;
                StevConfig.save();
                if (mc.player != null) {
                    mc.player.displayClientMessage(Component.literal("Chat: ")
                            .append(Component.literal(c.hideChat ? "HIDDEN" : "SHOWN")
                                    .withStyle(c.hideChat ? ChatFormatting.RED : ChatFormatting.GREEN)), true);
                }
            }
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
