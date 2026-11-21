package com.example.creepercat.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
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
            model = new CatEntityModel<>(MinecraftClient.getInstance()
                    .getEntityModelLoader()
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

        // Анимация вставания на две задние лапы
        float stand = 0f;
        if (anim > 0) {
            float k = 1f - (anim / 25f);
            stand = Math.min(1f, k * 1.4f);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-85f * stand));
            matrices.translate(0, 0.4 * stand, 0);
        }

        // Выравниваем модель кота по земле (модель в Minecraft перевернута по Y)
        matrices.translate(0, 1.5, 0);
        matrices.scale(1.0f, -1.0f, -1.0f);

        VertexConsumer vc = vcp.getBuffer(RenderLayer.getEntityCutoutNoCull(tex));
        ModelPart root = m.getPart();

        // Анимация лап при ходьбе
        float walkPhase = age * 0.5f;
        ModelPart rightHind = root.getChild("right_hind_leg");
        ModelPart leftHind = root.getChild("left_hind_leg");
        ModelPart rightFront = root.getChild("right_front_leg");
        ModelPart leftFront = root.getChild("left_front_leg");

        rightHind.pitch = MathHelper.cos(walkPhase) * 0.5f;
        leftHind.pitch = MathHelper.cos(walkPhase + (float) Math.PI) * 0.5f;
        rightFront.pitch = MathHelper.cos(walkPhase + (float) Math.PI) * 0.5f;
        leftFront.pitch = MathHelper.cos(walkPhase) * 0.5f;

        if (stand > 0.01f) {
            rightFront.pitch = -1.0f * stand;
            leftFront.pitch = -1.0f * stand;
            rightFront.yaw = 0.6f * stand;
            leftFront.yaw = -0.6f * stand;
        } else {
            rightFront.yaw = 0f;
            leftFront.yaw = 0f;
        }

        ModelPart tail1 = root.getChild("tail1");
        tail1.pitch = MathHelper.sin(age * 0.2f) * 0.3f + 0.8f;
        tail1.yaw = MathHelper.cos(age * 0.15f) * 0.4f;

        ModelPart head = root.getChild("head");
        head.pitch = MathHelper.sin(age * 0.1f) * 0.1f;

        m.render(matrices, vc, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);

        matrices.pop();

        // Частицы стадий эволюции
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
