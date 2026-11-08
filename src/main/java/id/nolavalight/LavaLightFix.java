package id.nolavalight;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;

public final class LavaLightFix {
    private static final int DELAY_TICKS = 3;
    private static final Map<Long, Integer> pending = new ConcurrentHashMap<>();
    private static Block lava;
    private static boolean errorLogged;
    private static boolean irisLogged;
    private static boolean irisErrorLogged;

    private LavaLightFix() {}

    public static void log(String msg) {
        System.out.println("[NoLavaLight] " + msg);
    }

    private static Class<?> cls(String... names) throws ClassNotFoundException {
        ClassNotFoundException last = null;
        for (String n : names) {
            try {
                return Class.forName(n);
            } catch (ClassNotFoundException e) {
                last = e;
            }
        }
        throw last;
    }

    private static Field field(Class<?> c, String... names) throws NoSuchFieldException {
        for (String n : names) {
            try {
                return c.getDeclaredField(n);
            } catch (NoSuchFieldException ignored) {
            }
        }
        throw new NoSuchFieldException(names[0]);
    }

    public static void init() {
        try {
            Block block = Blocks.LAVA_CAULDRON;
            Class<?> stateCls = cls("net.minecraft.class_4970$class_4971",
                    "net.minecraft.block.AbstractBlock$AbstractBlockState");
            Field luminance = field(stateCls, "field_23167", "luminance");
            luminance.setAccessible(true);
            for (BlockState state : block.getStateManager().getStates()) {
                int before = luminance.getInt(state);
                luminance.setInt(state, 0);
                log("lava cauldron luminance " + before + " -> " + luminance.getInt(state));
            }
            lava = block;
        } catch (Throwable t) {
            log("failed to patch: " + t);
        }
    }

    public static void stripFromShaderMap(Object map) {
        try {
            if (!(map instanceof Map<?, ?> m)) {
                return;
            }
            int removed = 0;
            for (BlockState state : Blocks.LAVA_CAULDRON.getStateManager().getStates()) {
                if (m.remove(state) != null) {
                    removed++;
                }
            }
            if (removed > 0 && !irisLogged) {
                irisLogged = true;
                log("Iris: lava cauldron dibuang dari block id map shader (" + removed + " state)");
            }
        } catch (Throwable t) {
            if (!irisErrorLogged) {
                irisErrorLogged = true;
                log("Iris hook failed: " + t);
            }
        }
    }

    public static void queue(int chunkX, int chunkZ) {
        if (lava == null) {
            return;
        }
        pending.put(((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL), DELAY_TICKS);
    }

    public static void tick(ClientWorld world) {
        if (lava == null || pending.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<Long, Integer>> it = pending.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, Integer> e = it.next();
            int left = e.getValue() - 1;
            if (left > 0) {
                e.setValue(left);
                continue;
            }
            it.remove();
            try {
                long key = e.getKey();
                relight(world, (int) (key >> 32), (int) key);
            } catch (Throwable t) {
                if (!errorLogged) {
                    errorLogged = true;
                    log("relight failed: " + t);
                }
            }
        }
    }

    private static void relight(ClientWorld world, int chunkX, int chunkZ) {
        Block target = lava;
        Chunk chunk = ((WorldView) world).getChunk(chunkX, chunkZ);
        ChunkSection[] sections = chunk.getSectionArray();
        int bottom = world.getBottomSectionCoord();
        for (int i = 0; i < sections.length; i++) {
            ChunkSection section = sections[i];
            if (section == null || !section.hasAny(s -> s.isOf(target))) {
                continue;
            }
            int baseY = (bottom + i) << 4;
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    for (int x = 0; x < 16; x++) {
                        if (section.getBlockState(x, y, z).isOf(target)) {
                            world.getLightingProvider().checkBlock(
                                    new BlockPos((chunkX << 4) + x, baseY + y, (chunkZ << 4) + z));
                        }
                    }
                }
            }
        }
    }
}
