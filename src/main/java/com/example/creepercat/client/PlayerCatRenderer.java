package com.example.creepercat.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class PlayerCatRenderer {
    private static CatEntityModel<CatEntity> model;

    private static CatEntityModel<CatEntity> getModel() {
        if (model == null) {
            var ctx = MinecraftClient.getInstance().getEntityRenderDispatcher().getTextRenderer();
            model = new CatEntityModel<>(MinecraftClient.getInstance()
                    .getEntityRenderDispatcher().getEntityModels()
                    .getModelPart(EntityModelLayers.CAT));
        }
        return model;
    }

    public static Identifier getTexture(int stage) {
        if (stage >= 3) return new Identifier("minecraft", "textures/entity/cat/all_black.png");
        if (stage >= 2) return new Identifier("minecraft", "textures/entity/cat/red.png");
        return new Identifier("minecraft", "textures/entity/cat/black.png");
    }

    public static void render(AbstractClientPlayerEntity player, float yaw, float tickDelta,
                              MatrixStack matrices, VertexConsumerProvider vcp, int light) {
        int stage = ClientCatState.getStage(player.getUuid());
        float scale = ClientCatState.stageScale(stage);
        int anim = ClientCatState.getAnim(player.getUuid());
        float age = player.age + tickDelta;

        CatEntityModel<CatEntity> m = getModel();
        Identifier tex = getTexture(stage);

        matrices.push();
        matrices.translate(0, player.isSneaking() ? 0.3 : 0.55, 0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-yaw));
        matrices.scale(scale, scale, scale);

        // Animation: stand up on hind legs + front paws spread
        float stand = 0f;
        if (anim > 0) {
            float k = 1f - (anim / 25f); // 0 -> 1 over animation
            stand = Math.min(1f, k * 1.4f);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-85f * stand));
            matrices.translate(0, 0.4 * stand, 0);
        }

        // Position model so its feet are at y=0
        matrices.translate(0, 0.55, 0);

        VertexConsumer vc = vcp.getBuffer(RenderLayer.getEntityCutoutNoCull(tex));

        // Animate legs and tail procedurally on top of vanilla model
        float walkPhase = age * 0.5f;
        m.rightHindLeg.pitch = MathHelper.cos(walkPhase) * 0.5f;
        m.leftHindLeg.pitch = MathHelper.cos(walkPhase + (float) Math.PI) * 0.5f;
        m.rightFrontLeg.pitch = MathHelper.cos(walkPhase + (float) Math.PI) * 0.5f;
        m.leftFrontLeg.pitch = MathHelper.cos(walkPhase) * 0.5f;

        if (stand > 0.01f) {
            // Front paws spread out
            m.rightFrontLeg.pitch = -1.0f * stand;
            m.leftFrontLeg.pitch = -1.0f * stand;
            m.rightFrontLeg.yaw = 0.6f * stand;
            m.leftFrontLeg.yaw = -0.6f * stand;
        }

        m.tail[0].pitch = MathHelper.sin(age * 0.2f) * 0.3f;
        m.tail[0].yaw = MathHelper.cos(age * 0.15f) * 0.4f;

        m.head.pitch = MathHelper.sin(age * 0.1f) * 0.1f;

        m.render(matrices, vc, light, OverlayTexture.DEFAULT_UV);

        matrices.pop();

        // Stage aura on client
        if (stage >= 2) {
            for (int i = 0; i < 3; i++) {
                double a = (age * 0.2 + i * Math.PI * 2 / 3) % (Math.PI * 2);
                double r = 0.5;
                player.getWorld().addParticle(net.minecraft.particle.ParticleTypes.WITCH,
                        player.getX() + Math.cos(a) * r, player.getY() + 0.2, player.getZ() + Math.sin(a) * r,
                        0, 0.01, 0);
            }
        }
        if (stage >= 3) {
            player.getWorld().addParticle(net.minecraft.particle.ParticleTypes.SOUL_FIRE_FLAME,
                    player.getX() + (Math.random() - 0.5), player.getY() + Math.random() * 1.5,
                    player.getZ() + (Math.random() - 0.5), 0, 0.02, 0);
            player.getWorld().addParticle(net.minecraft.particle.ParticleTypes.PORTAL,
                    player.getX() + (Math.random() - 0.5), player.getY() + Math.random() * 1.5,
                    player.getZ() + (Math.random() - 0.5), 0, 0.02, 0);
        }
    }
}
