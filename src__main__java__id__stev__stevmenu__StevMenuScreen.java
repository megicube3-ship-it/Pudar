package id.stev.stevmenu;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class StevMenuScreen extends Screen {
    private static final String[] TABS = {"Tampilan", "Cuaca & Waktu", "Dunia"};
    private static int lastTab = 0;

    private final Screen parent;
    private int tab = lastTab;
    private int left, right, top;

    public StevMenuScreen(Screen parent) {
        super(Component.literal("Stev Menu"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        left = cx - 154;
        right = cx + 4;
        top = 62;

        int tw = 100;
        int total = TABS.length * (tw + 4) - 4;
        for (int i = 0; i < TABS.length; i++) {
            final int idx = i;
            Button b = Button.builder(Component.literal(TABS[i]), btn -> {
                tab = idx;
                lastTab = idx;
                rebuildWidgets();
            }).bounds(cx - total / 2 + i * (tw + 4), 28, tw, 20).build();
            b.active = tab != i;
            addRenderableWidget(b);
        }

        switch (tab) {
            case 0 -> tabHud();
            case 1 -> tabWeather();
            default -> tabWorld();
        }

        addRenderableWidget(Button.builder(Component.literal("Selesai"), b -> onClose())
                .bounds(cx - 100, this.height - 28, 200, 20).build());
    }

    private int px(int i) { return (i % 2 == 0) ? left : right; }
    private int py(int i) { return top + (i / 2) * 24; }

    private void tabHud() {
        StevConfig c = StevConfig.get();
        toggle(0, "Hide Hands", () -> c.hideHands, v -> c.hideHands = v);
        toggle(1, "Hide Offhand Slot", () -> c.hideOffhandSlot, v -> c.hideOffhandSlot = v);
        toggle(2, "Hide Block Outline", () -> c.hideBlockOutline, v -> c.hideBlockOutline = v);
        toggle(3, "Hide Scoreboard", () -> c.hideScoreboard, v -> c.hideScoreboard = v);
        toggle(4, "Hide Chat", () -> c.hideChat, v -> c.hideChat = v);
        toggle(5, "Hide Boss Bar", () -> c.hideBossBar, v -> c.hideBossBar = v);
    }

    private void tabWorld() {
        StevConfig c = StevConfig.get();
        toggle(0, "Hide Hologram", () -> c.hideHolograms, v -> c.hideHolograms = v);
        toggle(1, "No Lava Light", () -> c.noLavaLight, v -> { c.noLavaLight = v; LavaFix.applyConfig(); });
        toggle(2, "Torch Lock", () -> c.torchLock, v -> c.torchLock = v);
        addRenderableWidget(Button.builder(Component.literal("Reset semua ke default"), b -> {
            StevConfig.reset();
            LavaFix.applyConfig();
            rebuildWidgets();
        }).bounds(left, py(4), 300, 20).build());
    }

    private void tabWeather() {
        StevConfig c = StevConfig.get();
        addRenderableWidget(Button.builder(weatherLabel(c), b -> {
            c.weatherMode = c.weatherMode.next();
            StevConfig.save();
            b.setMessage(weatherLabel(c));
        }).bounds(left, top, 300, 20).build());

        addRenderableWidget(Button.builder(label("Ubah Waktu", c.timeOverride), b -> {
            c.timeOverride = !c.timeOverride;
            StevConfig.save();
            b.setMessage(label("Ubah Waktu", c.timeOverride));
        }).bounds(left, top + 24, 300, 20).build());

        addRenderableWidget(new TimeSlider(left, top + 48, 300, 20));

        String[] names = {"Pagi", "Siang", "Sore", "Malam", "Tengah Malam"};
        int[] ticks = {0, 6000, 12000, 14000, 18000};
        int w = 58;
        for (int i = 0; i < names.length; i++) {
            final int t = ticks[i];
            addRenderableWidget(Button.builder(Component.literal(names[i]), b -> {
                c.timeTicks = t;
                c.timeOverride = true;
                StevConfig.save();
                rebuildWidgets();
            }).bounds(left + i * 60, top + 76, w, 20).build());
        }
    }

    private void toggle(int idx, String name, BooleanSupplier get, Consumer<Boolean> set) {
        addRenderableWidget(Button.builder(label(name, get.getAsBoolean()), btn -> {
            boolean v = !get.getAsBoolean();
            set.accept(v);
            StevConfig.save(); // auto-save setiap perubahan
            btn.setMessage(label(name, v));
        }).bounds(px(idx), py(idx), 150, 20).build());
    }

    private static Component label(String name, boolean on) {
        return Component.literal(name + ": ")
                .append(Component.literal(on ? "ON" : "OFF").withStyle(on ? ChatFormatting.GREEN : ChatFormatting.RED));
    }

    private static Component weatherLabel(StevConfig c) {
        return Component.literal("Cuaca: ").append(Component.literal(c.weatherMode.label).withStyle(ChatFormatting.YELLOW));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        super.render(g, mouseX, mouseY, delta);
        g.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFFFF);
    }

    @Override
    public void removed() {
        StevConfig.save();
    }

    @Override
    public void onClose() {
        StevConfig.save();
        this.minecraft.setScreen(parent);
    }

    private static class TimeSlider extends AbstractSliderButton {
        TimeSlider(int x, int y, int w, int h) {
            super(x, y, w, h, Component.empty(), StevConfig.get().timeTicks / 24000.0);
            updateMessage();
        }

        private int ticks() {
            return Math.max(0, Math.min(23999, (int) Math.round(this.value * 24000.0)));
        }

        @Override
        protected void updateMessage() {
            int t = ticks();
            int hour = (t / 1000 + 6) % 24;
            int min = (t % 1000) * 60 / 1000;
            setMessage(Component.literal(String.format("Jam: %02d:%02d  (%d tick)", hour, min, t)));
        }

        @Override
        protected void applyValue() {
            StevConfig c = StevConfig.get();
            c.timeTicks = ticks();
            c.timeOverride = true;
        }
    }
}
