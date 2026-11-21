package com.example.creepercat.client;

import com.example.creepercat.entity.TornCreeperEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class TornCreeperRenderer extends EntityRenderer<TornCreeperEntity> {
    private static final Identifier TEX = new Identifier("minecraft", "textures/entity/creeper/creeper.png");
    private final CreeperEntityModel<TornCreeperEntity> model;

    public TornCreeperRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new CreeperEntityModel<>(ctx.getPart(EntityModelLayers.CREEPER));
    }

    @Override
    public Identifier getTexture(TornCreeperEntity entity) {
        return TEX;
    }

    @Override
    public void render(TornCreeperEntity entity, float yaw, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vcp, int light) {
        matrices.push();
        VertexConsumer vc = vcp.getBuffer(RenderLayer.getEntityCutoutNoCull(TEX));
        ModelPart root = this.model.getPart();

        // Верхняя половина крипера (голова и верх тела) с вращением и подлетом
        matrices.push();
        matrices.translate(entity.dx * (entity.age + tickDelta) * 0.1, 0.8 + Math.sin((entity.age + tickDelta) * 0.2) * 0.2, entity.dz * (entity.age + tickDelta) * 0.1);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.rotation));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(entity.rotation * 0.5f));
        root.getChild("head").render(matrices, vc, light, OverlayTexture.DEFAULT_UV);
        root.getChild("body").render(matrices, vc, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();

        // Нижняя половина крипера (4 ноги) разлетается в противоположную сторону
        matrices.push();
        matrices.translate(-entity.dx * (entity.age + tickDelta) * 0.1, 0.0, -entity.dz * (entity.age + tickDelta) * 0.1);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-entity.rotation));
        root.getChild("right_hind_leg").render(matrices, vc, light, OverlayTexture.DEFAULT_UV);
        root.getChild("left_hind_leg").render(matrices, vc, light, OverlayTexture.DEFAULT_UV);
        root.getChild("right_front_leg").render(matrices, vc, light, OverlayTexture.DEFAULT_UV);
        root.getChild("left_front_leg").render(matrices, vc, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();

        matrices.pop();
    }
}
