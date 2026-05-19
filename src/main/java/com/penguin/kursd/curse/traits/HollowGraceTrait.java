package com.penguin.kursd.curse.traits;

import com.penguin.kursd.Ksd;
import com.penguin.kursd.curse.ArtifactData;
import com.penguin.kursd.curse.ArtifactTrait;
import com.penguin.kursd.curse.TraitType;
import com.penguin.kursd.contexts.HealthContext;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.ItemStack;

public class HollowGraceTrait implements ArtifactTrait {

    private static final float HOLLOW_MAX_HEALTH = 12.0f;

    @Override
    public String id() {
        return "hollow_grace";
    }

    @Override
    public TraitType type() {
        return TraitType.NEUTRAL;
    }

    @Override
    public void modifyHealth(PlayerEntity player, ArtifactData data, HealthContext ctx) {
        ctx.maxHealth = Math.min(ctx.maxHealth, HOLLOW_MAX_HEALTH);
    }

    @Override
    public void onTick(ServerPlayerEntity player, ItemStack stack, ArtifactData data) {

        player.addStatusEffect(new StatusEffectInstance(
            Ksd.HOLLOWED,
            40,
            0,
            true,
            false
        ));
    }

    @Override
    public float modifyJump(PlayerEntity player, ArtifactData data, float original) {

        float intensity = data.getIntensity();
        float boost = 1.0f + (0.6f * intensity);

        return original * boost;
    }
}