package id.stev.stevmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/** Lava cauldron tidak memancarkan cahaya (vanilla + Iris). Hanya di sisi client. */
public final class LavaFix {
    private static final int DELAY_TICKS = 10;
    private static final int MAX_SCANS_PER_TICK = 4;
    private static final Map<Long, Integer> pending = new HashMap<>();
    private static final Map<BlockState, Integer> originals = new HashMap<>();
    private static Field lumField;
    private static boolean fieldTried;
    private static boolean errorLogged;

    private LavaFix() {}

    private static Field lumField() {
        if (fieldTried) return lumField;
        fieldTried = true;
        Class<?> cls = null;
        for (String n : new String[]{"net.minecraft.class_4970$class_4971",
                "net.minecraft.world.level.block.state.BlockBehaviour$BlockStateBase"}) {
            try { cls = Class.forName(n); break; } catch (ClassNotFoundException ignored) {}
        }
        if (cls == null) return null;
        for (String f : new String[]{"field_23167", "lightEmission", "luminance"}) {
            try {
                Field fld = cls.getDeclaredField(f);
                if (fld.getType() == int.class) {
                    fld.setAccessible(true);
                    lumField = fld;
                    return fld;
                }
            } catch (Throwable ignored) {}
        }
        return null;
    }

    public static void applyConfig() {
        Field f = lumField();
        if (f == null) {
            if (!errorLogged) { errorLogged = true; System.err.println("[StevMenu] Field luminance tidak ketemu, No Lava Light tidak aktif"); }
            return;
        }
        boolean off = StevConfig.get().noLavaLight;
        try {
            for (BlockState s : Blocks.LAVA_CAULDRON.getStateDefinition().getPossibleStates()) {
                if (!originals.containsKey(s)) originals.put(s, f.getInt(s));
                f.setInt(s, off ? 0 : originals.get(s));
            }
        } catch (Throwable t) {
            if (!errorLogged) { errorLogged = true; System.err.println("[StevMenu] Gagal set luminance: " + t); }
            return;
        }
        relightAround();
    }

    private static void relightAround() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        int r = mc.options.getEffectiveRenderDistance();
        int pcx = mc.player.blockPosition().getX() >> 4;
        int pcz = mc.player.blockPosition().getZ() >> 4;
        for (int dx = -r; dx <= r; dx++)
            for (int dz = -r; dz <= r; dz++) queue(pcx + dx, pcz + dz);
    }

    public static void queue(int cx, int cz) {
        if (!StevConfig.get().noLavaLight && originals.isEmpty()) return;
        pending.put(key(cx, cz), DELAY_TICKS);
    }

    private static long key(int x, int z) { return ((long) x << 32) | (z & 0xffffffffL); }

    public static void tick(ClientLevel level) {
        if (pending.isEmpty()) return;
        int scans = 0;
        Iterator<Map.Entry<Long, Integer>> it = pending.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, Integer> e = it.next();
            int left = e.getValue() - 1;
            if (left > 0) { e.setValue(left); continue; }
            if (scans >= MAX_SCANS_PER_TICK) { e.setValue(1); continue; }
            it.remove();
            scans++;
            long k = e.getKey();
            scan(level, (int) (k >> 32), (int) k);
        }
    }

    private static void scan(ClientLevel level, int cx, int cz) {
        try {
            LevelChunk chunk = level.getChunkSource().getChunkNow(cx, cz);
            if (chunk == null) return;
            LevelChunkSection[] secs = chunk.getSections();
            int minY = level.getMinY();
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            for (int i = 0; i < secs.length; i++) {
                LevelChunkSection s = secs[i];
                if (s == null || s.hasOnlyAir() || !s.maybeHas(st -> st.is(Blocks.LAVA_CAULDRON))) continue;
                int baseY = minY + i * 16;
                for (int x = 0; x < 16; x++)
                    for (int y = 0; y < 16; y++)
                        for (int z = 0; z < 16; z++)
                            if (s.getBlockState(x, y, z).is(Blocks.LAVA_CAULDRON)) {
                                pos.set((cx << 4) + x, baseY + y, (cz << 4) + z);
                                level.getLightEngine().checkBlock(pos);
                            }
            }
        } catch (Throwable t) {
            if (!errorLogged) { errorLogged = true; System.err.println("[StevMenu] Relight gagal: " + t); }
        }
    }

    /** Dipanggil mixin Iris: buang lava cauldron dari peta blok shader. */
    public static void stripFromShaderMap(Object map) {
        if (!StevConfig.get().noLavaLight || !(map instanceof Map<?, ?> m)) return;
        try {
            m.keySet().removeIf(k -> k instanceof BlockState bs && bs.is(Blocks.LAVA_CAULDRON));
        } catch (Throwable ignored) {}
    }
}
