package id.stev.stevmenu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/** Config tunggal. Disimpan otomatis ke config/stevmenu.json setiap ada perubahan. */
public class StevConfig {
    public enum WeatherMode {
        SERVER("Ikut server"), CLEAR("Cerah"), RAIN("Hujan"), STORM("Badai petir"), SNOW("Salju");
        public final String label;
        WeatherMode(String label) { this.label = label; }
        public boolean raining() { return this == RAIN || this == STORM || this == SNOW; }
        public boolean thundering() { return this == STORM; }
        public WeatherMode next() { WeatherMode[] v = values(); return v[(ordinal() + 1) % v.length]; }
    }

    // HUD / tampilan
    public boolean hideHands = false;
    public boolean hideOffhandSlot = false;
    public boolean hideBlockOutline = false;
    public boolean hideScoreboard = false;
    public boolean hideChat = false;
    public boolean hideBossBar = false;
    // dunia
    public boolean hideHolograms = false;
    public boolean noLavaLight = false;
    public boolean torchLock = false;
    // cuaca & waktu
    public WeatherMode weatherMode = WeatherMode.SERVER;
    public boolean timeOverride = false;
    public int timeTicks = 6000;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static StevConfig instance;

    public static synchronized StevConfig get() {
        if (instance == null) load();
        return instance;
    }

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("stevmenu.json");
    }

    public static synchronized void load() {
        StevConfig c = null;
        try {
            Path p = path();
            if (Files.exists(p)) c = GSON.fromJson(Files.readString(p, StandardCharsets.UTF_8), StevConfig.class);
        } catch (Throwable t) {
            System.err.println("[StevMenu] Gagal baca config, pakai default: " + t);
        }
        if (c == null) c = new StevConfig();
        if (c.weatherMode == null) c.weatherMode = WeatherMode.SERVER;
        c.timeTicks = Math.floorMod(c.timeTicks, 24000);
        instance = c;
        save(); // tulis ulang supaya field baru langsung muncul di file
    }

    public static synchronized void save() {
        if (instance == null) return;
        try {
            Path p = path();
            Files.createDirectories(p.getParent());
            Path tmp = p.resolveSibling("stevmenu.json.tmp");
            Files.writeString(tmp, GSON.toJson(instance), StandardCharsets.UTF_8);
            try {
                Files.move(tmp, p, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tmp, p, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            System.err.println("[StevMenu] Gagal simpan config: " + e);
        }
    }

    public static synchronized void reset() {
        instance = new StevConfig();
        save();
    }
}
