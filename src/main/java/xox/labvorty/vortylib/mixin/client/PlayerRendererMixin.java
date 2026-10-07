package xox.labvorty.vortylib.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xox.labvorty.vortylib.entity.SeatEntity;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {
    @Unique
    private static float vortylib$cachedXRot, vortylib$cachedXRotO, vortylib$cachedYBodyRot, vortylib$cachedYBodyRotO, vortylib$cachedYHeadRot, vortylib$cachedYHeadRotO;

    @Inject(
            method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD")
    )
    private void vortylib$applySeatPose(
            AbstractClientPlayer entity,
            float entityYaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            CallbackInfo ci
    ) {
        Entity vehicle = entity.getVehicle();
        if (!(vehicle instanceof SeatEntity seat) || !seat.hasLookAngleRestriction()) return;

        float clampedYaw = seat.clampLookYaw(entity.getViewYRot(partialTicks));

        vortylib$cachedYBodyRot = entity.yBodyRot;
        vortylib$cachedYBodyRotO = entity.yBodyRotO;
        vortylib$cachedYHeadRot = entity.yHeadRot;
        vortylib$cachedYHeadRotO = entity.yHeadRotO;
        vortylib$cachedXRot = entity.getXRot();
        vortylib$cachedXRotO = entity.xRotO;

        entity.yBodyRot = clampedYaw;
        entity.yBodyRotO = clampedYaw;
        entity.yHeadRot = clampedYaw;
        entity.yHeadRotO = clampedYaw;
    }

    @Inject(
            method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("TAIL")
    )
    private void vortylib$restorePose(
            AbstractClientPlayer entity,
            float entityYaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            CallbackInfo ci
    ) {
        Entity vehicle = entity.getVehicle();
        if (!(vehicle instanceof SeatEntity seat) || !seat.hasLookAngleRestriction()) return;

        entity.yBodyRot = vortylib$cachedYBodyRot;
        entity.yBodyRotO = vortylib$cachedYBodyRotO;
        entity.yHeadRot = vortylib$cachedYHeadRot;
        entity.yHeadRotO = vortylib$cachedYHeadRotO;
        entity.setXRot(vortylib$cachedXRot);
        entity.xRotO = vortylib$cachedXRotO;
    }
}
