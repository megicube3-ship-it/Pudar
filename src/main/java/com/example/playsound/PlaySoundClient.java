package com.example.playsound;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import org.lwjgl.glfw.GLFW;

public class PlaySoundClient implements ClientModInitializer {
	public static final String MOD_ID = "playsound";

	private static KeyMapping playKey;
	private static SoundEvent customSound;

	@Override
	public void onInitializeClient() {
		customSound = SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(MOD_ID, "custom"));

		KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "main"));

		playKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.playsound.play",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_K,
				category
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (playKey.consumeClick()) {
				Minecraft.getInstance().getSoundManager()
						.play(SimpleSoundInstance.forUI(customSound, 1.0F));
			}
		});
	}
}
