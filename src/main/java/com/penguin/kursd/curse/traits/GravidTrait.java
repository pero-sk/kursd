package com.penguin.kursd.curse.traits;

import com.penguin.kursd.curse.ArtifactData;
import com.penguin.kursd.curse.ArtifactTrait;
import com.penguin.kursd.curse.TraitType;
import com.penguin.kursd.contexts.MovementContext;
import com.penguin.kursd.contexts.HealthContext;
import com.penguin.kursd.curse.ArtifactManager;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class GravidTrait implements ArtifactTrait {

    private static final float SPEED_PENALTY = 0.65f;
    private static final float JUMP_PENALTY = 0.75f;

    @Override
    public String id() {
        return "gravid";
    }

    @Override
    public TraitType type() {
        return TraitType.NEUTRAL;
    }

    @Override
    public void modifyMovement(PlayerEntity player, ArtifactData data, MovementContext ctx) {
        ctx.xzMultiplier *= SPEED_PENALTY;
    }

    @Override
    public float modifyJump(PlayerEntity player, ArtifactData data, float original) {
        return original * JUMP_PENALTY;
    }

    @Override
    public void onDamaged(ServerPlayerEntity player, DamageSource source,
                          float amount, ArtifactData data) {

        if (source.equals(player.getDamageSources().fall())) {
            HealthContext ctx = ArtifactManager.getHealthContext(player);
            ctx.healthCap = Math.max(ctx.healthCap, player.getHealth() + amount);
        }
    }

    @Override
    public void onTick(ServerPlayerEntity player, ItemStack stack, ArtifactData data) {
        var vel = player.getVelocity();
        double MAX_NORMAL_SPEED = 0.6;
        if (Math.abs(vel.x) > MAX_NORMAL_SPEED || Math.abs(vel.z) > MAX_NORMAL_SPEED) {
            player.setVelocity(
                vel.x * 0.1,
                vel.y,
                vel.z * 0.1
            );
        }
    }
}