package com.penguin.kursd.curse;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public final class ArtifactEvents {

    private ArtifactEvents() {}

    public static void tick(ServerPlayerEntity player, ItemStack stack, ArtifactData data) {
        for (ArtifactTrait trait : data.getTraits()) {
            trait.onTick(player, stack, data);
        }
    }

    public static void kill(ServerPlayerEntity player, LivingEntity target, ArtifactData data) {
        for (ArtifactTrait trait : data.getTraits()) {
            trait.onKill(player, target, data);
        }
    }

    public static void attack(ServerPlayerEntity player, LivingEntity target, ArtifactData data) {
        for (ArtifactTrait trait : data.getTraits()) {
            trait.onAttack(player, target, data);
        }
    }

    public static void damaged(ServerPlayerEntity player, DamageSource source, float amount, ArtifactData data) {
        for (ArtifactTrait trait : data.getTraits()) {
            trait.onDamaged(player, source, amount, data);
        }
    }

    public static void mine(ServerPlayerEntity player, BlockState state, ArtifactData data) {
        for (ArtifactTrait trait : data.getTraits()) {
            trait.onMine(player, state, data);
        }
    }

    public static void move(ServerPlayerEntity player, ArtifactData data, double distance) {
        for (ArtifactTrait trait : data.getTraits()) {
            trait.onMove(player, data, distance);
        }
    }
}