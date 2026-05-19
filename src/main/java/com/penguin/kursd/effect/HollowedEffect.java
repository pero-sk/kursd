package com.penguin.kursd.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class HollowedEffect extends StatusEffect {

    public HollowedEffect() {
        super(
            StatusEffectCategory.BENEFICIAL,
            0xD8D8FF
        );
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyUpdateEffect(
        ServerWorld world,
        LivingEntity entity,
        int amplifier
    ) {

        if (!(entity instanceof PlayerEntity player)) {
            return true;
        }

        Vec3d vel = player.getVelocity();

        // reduce downward speed
        if (vel.y < 0) {

            // Player velocity is handled in `HollowedMovementMixin` (src\main\java\com\penguin\kursd\mixin\HollowedMovementMixin.java)
            // player.setVelocity(
            //     vel.x * 1.02,
            //     vel.y * 0.25,
            //     vel.z * 1.02
            // );

            player.fallDistance = 0;
        }

        return true;
    }

    @Override
    public void onEntityDamage(
        ServerWorld world,
        LivingEntity entity,
        int amplifier,
        DamageSource source,
        float amount
    ) {
        if (!(entity instanceof PlayerEntity player)) {
            return;
        }

        // Full fall damage immunity
        if (source.isOf(net.minecraft.entity.damage.DamageTypes.FALL)) {

            player.fallDistance = 0.0f;

            // cancel damage entirely (by healing back or intercepting depending on your API)
            entity.setHealth(entity.getHealth()); 
            return;
        }

        super.onEntityDamage(world, entity, amplifier, source, amount);
    }
}