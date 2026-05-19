package com.penguin.kursd.curse.traits;

import com.penguin.kursd.curse.ArtifactData;
import com.penguin.kursd.curse.ArtifactTrait;
import com.penguin.kursd.curse.TraitType;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

public class GlassbonedTrait implements ArtifactTrait {

    @Override
    public String id() {
        return "glass_boned";
    }

    @Override
    public TraitType type() {
        return TraitType.DETRIMENTAL;
    }

    @Override
    public void onDamaged(ServerPlayerEntity player, DamageSource source, float amount, ArtifactData data) {

        float stability = data.getStat("stability");
        stability -= amount * 0.02f;

        data.setStat("stability", Math.max(0.1f, stability));

        if (amount > 6.0f) {
            player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.SLOWNESS, 60, 1
            ));
        }
    }
}