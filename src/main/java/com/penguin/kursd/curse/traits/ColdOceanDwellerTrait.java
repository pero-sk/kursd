package com.penguin.kursd.curse.traits;

import com.penguin.kursd.curse.ArtifactData;
import com.penguin.kursd.curse.ArtifactTrait;
import com.penguin.kursd.curse.TraitType;
import com.penguin.kursd.contexts.EnvironmentContext;
import com.penguin.kursd.contexts.HealthContext;
import com.penguin.kursd.contexts.MovementContext;

import net.minecraft.entity.player.PlayerEntity;

public class ColdOceanDwellerTrait implements ArtifactTrait {

    private static final float WATER_MAX_HEALTH = 6.0f;
    private static final float SPEED_BOOST = 2.0f;

    @Override
    public String id() {
        return "cold_ocean_dweller";
    }

    @Override
    public TraitType type() {
        return TraitType.NEUTRAL;
    }

    @Override
    public void modifyHealth(PlayerEntity player, ArtifactData data, HealthContext ctx) {

        boolean inWater = player.isTouchingWater();

        if (inWater) {
            ctx.maxHealth = Math.min(ctx.maxHealth, WATER_MAX_HEALTH);
        }
    }

    @Override
    public void modifyEnvironment(PlayerEntity player, ArtifactData data, EnvironmentContext ctx) {

        if (!player.isTouchingWater()) return;

        ctx.preventDrowning = true;
        ctx.air = 300;
        ctx.swimSpeedMultiplier *= SPEED_BOOST;
        ctx.waterSpeedMultiplier *= SPEED_BOOST;
    }

    @Override
    public void modifyMovement(PlayerEntity player, ArtifactData data, MovementContext ctx) {

        if (!player.isTouchingWater()) return;

        ctx.xzMultiplier *= SPEED_BOOST;
    }
}