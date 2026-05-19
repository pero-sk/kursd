package com.penguin.kursd.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.penguin.kursd.Ksd;
import com.penguin.kursd.contexts.MovementContext;
import com.penguin.kursd.curse.ArtifactManager;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import net.minecraft.util.math.Vec3d;

@Mixin(LivingEntity.class)
public abstract class HollowedMovementMixin {

    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void kursd$hollowedGravity(CallbackInfo ci) {

        if (!((Object)this instanceof PlayerEntity player)) return;

        var inst = player.getStatusEffect(Ksd.HOLLOWED);
        if (inst == null) return;

        int amp = inst.getAmplifier();

        MovementContext ctx = ArtifactManager.getMovementContext(player);

        Vec3d vel = player.getVelocity();

        if (vel.y < 0) {

            double boost = 0.08 + (amp / 100.0);

            ctx.yVelocity += 0.02 * (amp + 1);
            ctx.xzMultiplier *= (0.2 + boost);
        }

        player.fallDistance = 0.0f;
    }
}