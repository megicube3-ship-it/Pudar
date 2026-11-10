package com.example.enchantborder;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class EnchantBorderClient implements ClientModInitializer {

    // Warna gradasi: kiri-atas (terang) -> kanan-bawah (gelap). Format 0xRRGGBB.
    private static final int COLOR_START = 0xD39BFF;
    private static final int COLOR_END   = 0x7A22D6;
    // Ukuran kotak border (item 16px + 1px padding tiap sisi = 18px)
    private static final int SIZE = 18;

    @Override
    public void onInitializeClient() {
        // Digambar SETELAH hotbar bawaan, jadi tampil di atas slot.
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.HOTBAR,
                Identifier.of("enchantborder", "hotbar_border"),
                EnchantBorderClient::render
        );
    }

    private static void render(DrawContext ctx, RenderTickCounter tick) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden) return;

        int cx = mc.getWindow().getScaledWidth() / 2;
        int h  = mc.getWindow().getScaledHeight();

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty() || !isEnchanted(stack)) continue;

            // Posisi ikon item vanilla: x = cx - 88 + i*20, y = h - 19
            int x = cx - 88 + i * 20 - 1;
            int y = h - 19 - 1;
            drawGradientBorder(ctx, x, y);
        }
    }

    private static boolean isEnchanted(ItemStack stack) {
        if (stack.hasEnchantments()) return true;
        // Enchanted book
        return !stack.getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS,
                ItemEnchantmentsComponent.DEFAULT).isEmpty();
    }

    private static void drawGradientBorder(DrawContext ctx, int x, int y) {
        int max = (SIZE - 1) * 2;
        for (int dx = 0; dx < SIZE; dx++) {
            ctx.fill(x + dx, y,            x + dx + 1, y + 1,        colorAt(dx, 0, max));
            ctx.fill(x + dx, y + SIZE - 1, x + dx + 1, y + SIZE,     colorAt(dx, SIZE - 1, max));
        }
        for (int dy = 1; dy < SIZE - 1; dy++) {
            ctx.fill(x,            y + dy, x + 1,        y + dy + 1, colorAt(0, dy, max));
            ctx.fill(x + SIZE - 1, y + dy, x + SIZE,     y + dy + 1, colorAt(SIZE - 1, dy, max));
        }
    }

    private static int colorAt(int dx, int dy, int max) {
        float t = (dx + dy) / (float) max;
        int r = lerp((COLOR_START >> 16) & 0xFF, (COLOR_END >> 16) & 0xFF, t);
        int g = lerp((COLOR_START >> 8) & 0xFF,  (COLOR_END >> 8) & 0xFF,  t);
        int b = lerp(COLOR_START & 0xFF,         COLOR_END & 0xFF,         t);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    private static int lerp(int a, int b, float t) {
        return Math.round(a + (b - a) * t);
    }
}
