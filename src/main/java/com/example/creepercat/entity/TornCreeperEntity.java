package com.example.creepercat.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;

public class TornCreeperEntity extends Entity {
    private int life = 0;
    public double dx = 0, dz = 0;
    public float rotation = 0;

    public TornCreeperEntity(EntityType<?> type, World world) {
        super(type, world);
        this.setNoGravity(true);
        this.setInvulnerable(true);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 1.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0);
    }

    public void setDirection(double dx, double dz) {
        this.dx = dx;
        this.dz = dz;
    }

    @Override
    public void tick() {
        super.tick();
        life++;
        setPosition(getX() + dx * 0.1, getY() + 0.02, getZ() + dz * 0.1);
        rotation += 8f;
        if (getWorld().isClient) {
            for (int i = 0; i < 2; i++) {
                getWorld().addParticle(ParticleTypes.ITEM_SLIME,
                        getX() + (random.nextDouble() - 0.5) * 0.6,
                        getY() + random.nextDouble() * 1.2,
                        getZ() + (random.nextDouble() - 0.5) * 0.6,
                        0, -0.02, 0);
                getWorld().addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                        getX() + (random.nextDouble() - 0.5) * 0.4,
                        getY() + random.nextDouble() * 1.0,
                        getZ() + (random.nextDouble() - 0.5) * 0.4,
                        0, 0.02, 0);
            }
        }
        if (life > 40) discard();
    }

    @Override
    public boolean canHit() { return false; }
    @Override protected void initDataTracker() {}
    @Override protected void readCustomDataFromNbt(NbtCompound nbt) {}
    @Override protected void writeCustomDataToNbt(NbtCompound nbt) {}
}
