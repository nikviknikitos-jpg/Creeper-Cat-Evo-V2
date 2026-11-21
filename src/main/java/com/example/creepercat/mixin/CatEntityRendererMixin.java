package com.example.creepercat.mixin;

import com.example.creepercat.mixin.CatEntityMixin;
import net.minecraft.client.render.entity.CatEntityRenderer;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CatEntityRenderer.class)
public class CatEntityRendererMixin {
    @Inject(method = "setupTransforms", at = @At("TAIL"))
    private void creepercat$standing(CatEntity cat, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta, CallbackInfo ci) {
        int ticks = ((CatEntityMixin) (Object) cat).creepercat$getBattleTicks();
        if (ticks > 0) {
            float k = Math.min(1f, ticks / 20f);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-85f * k));
        }
    }
}
