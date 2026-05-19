package com.penguin.kursd.curse;

import com.penguin.kursd.contexts.EnvironmentContext;
import com.penguin.kursd.contexts.HealthContext;
import com.penguin.kursd.contexts.MovementContext;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;


/**
 * A procedural behavior unit attached to kʊɹs'd's artifacts.
 *
 * Traits should avoid storing internal fields directly.
 * Persistent state belongs in ArtifactData.
 */
public interface ArtifactTrait {

    String id();

    TraitType type();

    /**
     * translation key to the display name
     */
    default String displayKey() {
        return "kursd.trait."+id();
    }

    default int weight() {
        return 10;
    }

    // Whether this trait can coexist with another trait. Override for conflict logic.
    default boolean compatibleWith(ArtifactTrait other) {
        return true;
    }

    // Called once, on first and only awakening.
    default void onAwaken(
        ServerPlayerEntity player,
        ItemStack stack,
        ArtifactData data
    ) {}

    // Called once, on every stage increase.
    default void onStageChanged(
        ServerPlayerEntity player,
        ItemStack stack,
        ArtifactData data,
        int oldStage,
        int newStage
    ) {}

    // Called every tick when discoverable to ArtifactManager
    default void onTick(
        ServerPlayerEntity player,
        ItemStack stack,
        ArtifactData data
    ) {}

    // Called once when player attacks something
    default void onAttack(
        ServerPlayerEntity player,
        LivingEntity target,
        ArtifactData data
    ) {}

    // Called once when player kills something
    default void onKill(
        ServerPlayerEntity player,
        LivingEntity target,
        ArtifactData data
    ) {}

    // Called once when player is damaged
    default void onDamaged(
        ServerPlayerEntity player,
        DamageSource source,
        float amount,
        ArtifactData data
    ) {}

    // Called once when player mines something
    default void onMine(
        ServerPlayerEntity player,
        BlockState state,
        ArtifactData data
    ) {}

    // Called semi-every tick if player's positional difference is greater than a small number (given in @param distance)
    default void onMove(
        ServerPlayerEntity player,
        ArtifactData data,
        double distance
    ) {}

    // Called in mixin to change jump height
    default float modifyJump(PlayerEntity player, ArtifactData data, float original) {
        return original;
    }

    // Called in mixin to change movement speed
    default void modifyMovement(
        PlayerEntity player,
        ArtifactData data,
        MovementContext ctx
    ) {}

    // Called in mixin to change max health 
    default void modifyHealth(
        PlayerEntity player,
        ArtifactData data,
        HealthContext ctx
    ) {}

    // Called in mixin to change whatever is in EnvironmentContext
    default void modifyEnvironment(
        PlayerEntity player,
        ArtifactData data,
        EnvironmentContext ctx
    ) {}
    
    default void onEquip(ServerPlayerEntity player, ItemStack stack, ArtifactData data) {}

    default void onUnequip(ServerPlayerEntity player, ItemStack stack, ArtifactData data) {}
}