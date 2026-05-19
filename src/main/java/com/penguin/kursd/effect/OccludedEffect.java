package com.penguin.kursd.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;

public class OccludedEffect extends StatusEffect {

    public OccludedEffect() {
        super(
            StatusEffectCategory.HARMFUL,
            0x111111 // near-black, won't be seen (no icon, no particles)
        );
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false; // no per-tick logic needed
    }

    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        return true;
    }
}