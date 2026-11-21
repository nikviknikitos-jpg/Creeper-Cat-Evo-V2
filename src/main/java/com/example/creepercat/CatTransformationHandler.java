package com.example.creepercat;

import com.example.creepercat.network.CatStateSync;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class CatTransformationHandler {
    public static void toggle(ServerPlayerEntity p) {
        boolean nowCat = !CatState.isCat(p);
        CatState.setCat(p, nowCat);
        CatState.setAnim(p, 0);
        ServerWorld sw = (ServerWorld) p.getWorld();

        var lightning = net.minecraft.entity.EntityType.LIGHTNING_BOLT.create(sw);
        if (lightning != null) {
            lightning.refreshPositionAfterTeleport(p.getX(), p.getY(), p.getZ());
            lightning.setCosmetic(true);
            sw.spawnEntity(lightning);
        }
        sw.spawnParticles(ParticleTypes.WITCH, p.getX(), p.getY() + 1, p.getZ(), 80, 0.7, 1.2, 0.7, 0.3);
        sw.spawnParticles(ParticleTypes.END_ROD, p.getX(), p.getY() + 1, p.getZ(), 50, 0.6, 0.9, 0.6, 0.2);
        sw.playSound(null, p.getBlockPos(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
                SoundCategory.PLAYERS, 1.0f, 1.4f);

        if (nowCat) {
            applyStageEffects(p);
            p.sendMessage(Text.literal("§d§lВы приняли облик Боевого Кота! §7(Стадия 1)"), false);
            p.sendMessage(Text.literal("§bВы приняли облик Боевого Кота! §7(Стадия 1)"), true);
        } else {
            clearStageEffects(p);
            p.sendMessage(Text.literal("§aВы вернулись в человеческий облик."), false);
        }
        CatStateSync.send(p);
    }

    public static void applyStageEffects(ServerPlayerEntity p) {
        int stage = CatState.getStage(CatState.getEvo(p));
        p.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 20 * 60 * 30, 0, false, false));
        p.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20 * 60 * 30, 1, false, false));
        p.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 20 * 60 * 30, 1, false, false));
        if (stage >= 2) {
            p.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 60 * 30, 1, false, false));
        }
        if (stage >= 3) {
            p.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 20 * 60 * 30, 1, false, false));
            p.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20 * 60 * 30, 1, false, false));
        }
    }

    public static void clearStageEffects(ServerPlayerEntity p) {
        p.removeStatusEffect(StatusEffects.NIGHT_VISION);
        p.removeStatusEffect(StatusEffects.SPEED);
        p.removeStatusEffect(StatusEffects.JUMP_BOOST);
        p.removeStatusEffect(StatusEffects.STRENGTH);
        p.removeStatusEffect(StatusEffects.REGENERATION);
        p.removeStatusEffect(StatusEffects.RESISTANCE);
    }

    public static void addEvo(ServerPlayerEntity p, int delta) {
        int prev = CatState.getEvo(p);
        int now = prev + delta;
        CatState.setEvo(p, now);
        int prevStage = CatState.getStage(prev);
        int newStage = CatState.getStage(now);
        p.sendMessage(Text.literal("§aКрипер разорван! §eЭволюция: " + now + " / 5"), true);
        if (newStage > prevStage) {
            ServerWorld sw = (ServerWorld) p.getWorld();
            if (newStage == 2) {
                sw.playSound(null, p.getBlockPos(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
                        SoundCategory.PLAYERS, 1.5f, 1.2f);
                p.sendMessage(Text.literal("§c§lЭВОЛЮЦИЯ! §6Стадия 2: Кровавый Рысь-Мутант!"), false);
            } else if (newStage == 3) {
                var lightning = net.minecraft.entity.EntityType.LIGHTNING_BOLT.create(sw);
                if (lightning != null) {
                    lightning.refreshPositionAfterTeleport(p.getX(), p.getY(), p.getZ());
                    sw.spawnEntity(lightning);
                }
                p.sendMessage(Text.literal("§5§lВЫСШАЯ ЭВОЛЮЦИЯ! §dСтадия 3: Альфа-Пожиратель!"), false);
            }
            applyStageEffects(p);
        }
        CatStateSync.send(p);
    }
}
