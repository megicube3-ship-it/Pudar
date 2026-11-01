package com.bloodclient;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class BloodClient {
    static final double MAX_DISTANCE = 32.0;
    static final double PARTICLE_SCALE = 1.0;
    static final boolean SOUND = true;

    private static final Map<Integer, Integer> LAST_HURT = new HashMap<>();
    private static final Map<Integer, Float> LAST_HEALTH = new HashMap<>();
    private static float selfLastHealth = -1f;
    private static int selfLastHurt = 0;
    private static int cleanTimer = 0;

    private BloodClient() {}

    public static void tick(Minecraft mc) {
        ClientLevel level = mc.level;
        Player me = mc.player;
        if (level == null || me == null) {
            LAST_HURT.clear();
            LAST_HEALTH.clear();
            selfLastHealth = -1f;
            return;
        }

        float hp = me.getHealth();
        int hurt = me.hurtTime;
        if (selfLastHealth >= 0f && hurt > selfLastHurt) {
            float dmg = selfLastHealth - hp;
            if (dmg <= 0f) dmg = 2f;
            spawnBlood(mc, level, me, dmg, 0xFF0000);
        }
        selfLastHealth = hp;
        selfLastHurt = hurt;

        for (Entity e : level.entitiesForRendering()) {
            if (!(e instanceof LivingEntity le) || e == me || e instanceof ArmorStand) continue;
            if (le.distanceToSqr(me) > MAX_DISTANCE * MAX_DISTANCE) continue;

            int id = le.getId();
            int cur = le.hurtTime;
            float health = le.getHealth();
            Integer prev = LAST_HURT.put(id, cur);
            Float prevHealth = LAST_HEALTH.put(id, health);

            if (prev != null && cur > prev) {
                float dmg = (prevHealth != null) ? prevHealth - health : 0f;
                if (dmg <= 0f) dmg = 2f;
                spawnBlood(mc, level, le, dmg, colorFor(le));
            }
        }

        if (++cleanTimer > 400) {
            cleanTimer = 0;
            LAST_HURT.clear();
            LAST_HEALTH.clear();
        }
    }

    private static int colorFor(LivingEntity e) {
        EntityType<?> t = e.getType();
        if (t == EntityType.SLIME) return 0x55FF55;
        if (t == EntityType.MAGMA_CUBE || t == EntityType.BLAZE) return 0xFF8800;
        if (t == EntityType.ENDERMAN || t == EntityType.ENDERMITE || t == EntityType.SHULKER) return 0xAA33FF;
        if (t == EntityType.CREEPER) return 0x33CC33;
        if (t == EntityType.SKELETON || t == EntityType.STRAY || t == EntityType.BOGGED
                || t == EntityType.WITHER_SKELETON || t == EntityType.GHAST) return 0xDDDDDD;
        return 0xFF0000;
    }

    private static void spawnBlood(Minecraft mc, ClientLevel level, LivingEntity target,
                                   float damage, int color) {
        int tier = (int) Math.min(4, Math.max(0, (damage - 0.01f) / 4f));
        int chunks = (int) ((18 + tier * 10) * PARTICLE_SCALE);
        int dust = (int) ((10 + tier * 10) * PARTICLE_SCALE);

        double x = target.getX();
        double y = target.getY();
        double z = target.getZ();
        double h = Math.max(0.4, target.getBbHeight());
        RandomSource r = level.getRandom();

        ParticleOptions chunk = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.REDSTONE_BLOCK));
        ParticleOptions dustOpt = new DustParticleOptions(color, 1.3f);

        for (int i = 0; i < chunks; i++) {
            mc.particleEngine.createParticle(chunk,
                    x + r.nextGaussian() * 0.5, y + h * 0.55 + r.nextGaussian() * 0.4, z + r.nextGaussian() * 0.5,
                    (r.nextDouble() - 0.5) * 0.25, r.nextDouble() * 0.2, (r.nextDouble() - 0.5) * 0.25);
        }
        for (int i = 0; i < dust; i++) {
            mc.particleEngine.createParticle(dustOpt,
                    x + r.nextGaussian() * 0.4, y + 0.1 + Math.abs(r.nextGaussian()) * 0.3, z + r.nextGaussian() * 0.4,
                    (r.nextDouble() - 0.5) * 0.2, r.nextDouble() * 0.1, (r.nextDouble() - 0.5) * 0.2);
        }

        if (SOUND) {
            level.playLocalSound(x, y, z, SoundEvents.SLIME_BLOCK_BREAK, SoundSource.PLAYERS, 0.58f, 1.8f, false);
            level.playLocalSound(x, y, z, SoundEvents.HONEY_BLOCK_BREAK, SoundSource.PLAYERS, 0.95f, 1.1f, false);
        }
    }
}
