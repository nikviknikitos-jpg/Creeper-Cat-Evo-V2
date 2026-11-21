package com.example.creepercat.mixin;

import com.example.creepercat.CatHunterManager;
import com.example.creepercat.CatState;
import com.example.creepercat.CatTransformationHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void creepercat$catAttack(Entity target, CallbackInfo ci) {
        ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;
        if (!CatState.isCat(self)) return;
        if (!((Object) target instanceof CreeperEntity creeper)) return;
        if (!creeper.isAlive()) return;

        if (self.getWorld() instanceof ServerWorld sw) {
            creeper.setFuseSpeed(0);
            double cx = creeper.getX(), cy = creeper.getY(), cz = creeper.getZ();
            creeper.discard();
            CatHunterManager.spawnTorn(sw, cx, cy, cz);
            CatState.setAnim(self, 25);
            CatTransformationHandler.addEvo(self, 1);
        }
        ci.cancel();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void creepercat$tick(CallbackInfo ci) {
        ServerPlayerEntity p = (ServerPlayerEntity) (Object) this;
        if (!CatState.isCat(p)) return;
        int anim = CatState.getAnim(p);
        if (anim > 0) CatState.setAnim(p, anim - 1);

        if (p.getWorld() instanceof ServerWorld sw) {
            int stage = CatState.getStage(CatState.getEvo(p));
            if (stage >= 2 && p.age % 3 == 0) {
                double r = 0.6 + stage * 0.2;
                double a = p.age * 0.3;
                sw.spawnParticles(ParticleTypes.PORTAL,
                        p.getX() + Math.cos(a) * r, p.getY() + 0.2, p.getZ() + Math.sin(a) * r,
                        1, 0, 0.02, 0, 0.01);
            }
            if (stage >= 3 && p.age % 3 == 0) {
                sw.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        p.getX() + (sw.random.nextDouble() - 0.5),
                        p.getY() + sw.random.nextDouble() * 1.5,
                        p.getZ() + (sw.random.nextDouble() - 0.5),
                        1, 0, 0.02, 0, 0.01);
            }
        }
    }
}
