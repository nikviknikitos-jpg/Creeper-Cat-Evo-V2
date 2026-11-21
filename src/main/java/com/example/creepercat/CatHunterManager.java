package com.example.creepercat;

import com.example.creepercat.entity.TornCreeperEntity;
import com.example.creepercat.registry.ModEntities;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;

import java.util.List;

public class CatHunterManager {
    public static void tick(ServerWorld world) {
        if (world.getTime() % 5 != 0) return;
        List<CatEntity> cats = world.getEntitiesByClass(CatEntity.class,
                new Box(-30000000, world.getBottomY(), -30000000, 30000000, world.getTopY(), 30000000),
                c -> c.isTamed() && !c.isSitting() && c.isAlive());
        for (CatEntity cat : cats) {
            Box box = new Box(cat.getX() - 10, cat.getY() - 6, cat.getZ() - 10,
                    cat.getX() + 10, cat.getY() + 6, cat.getZ() + 10);
            List<CreeperEntity> creepers = world.getEntitiesByClass(CreeperEntity.class, box, e -> e.isAlive());
            if (creepers.isEmpty()) continue;
            CreeperEntity c = creepers.get(0);
            c.setFuseSpeed(0);
            double d2 = cat.squaredDistanceTo(c);
            if (d2 < 9.0) {
                double cx = c.getX(), cy = c.getY(), cz = c.getZ();
                c.discard();
                spawnTorn(world, cx, cy, cz);
                world.spawnParticles(ParticleTypes.ANGRY_VILLAGER,
                        cat.getX(), cat.getY() + 0.7, cat.getZ(), 12, 0.3, 0.3, 0.3, 0.02);
                world.spawnParticles(ParticleTypes.WITCH,
                        cat.getX(), cat.getY() + 0.7, cat.getZ(), 8, 0.4, 0.4, 0.4, 0.02);
                world.playSound(null, cat.getBlockPos(), SoundEvents.ENTITY_CAT_HISS,
                        SoundCategory.NEUTRAL, 1.5f, 0.7f);
                world.playSound(null, cat.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
                        SoundCategory.NEUTRAL, 1.5f, 0.9f);
                if (cat.getOwner() instanceof ServerPlayerEntity owner && CatState.isCat(owner)) {
                    CatTransformationHandler.addEvo(owner, 1);
                }
            }
        }
    }

    public static void spawnTorn(ServerWorld world, double x, double y, double z) {
        TornCreeperEntity torn = ModEntities.TORN_CREEPER.create(world);
        if (torn != null) {
            torn.refreshPositionAndAngles(x, y, z, 0, 0);
            torn.setDirection(world.random.nextDouble() - 0.5, world.random.nextDouble() - 0.5);
            world.spawnEntity(torn);
        }
    }

    public static boolean isCatPlayerAttackingCreeper(PlayerEntity p, CreeperEntity c) {
        return CatState.isCat(p);
    }
}
