package com.penguin.kursd.curse.traits;

import com.penguin.kursd.curse.ArtifactData;
import com.penguin.kursd.curse.ArtifactTrait;
import com.penguin.kursd.curse.TraitType;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

public class AnemicTrait implements ArtifactTrait {

    @Override
    public String id() {
        return "anemic";
    }

    @Override
    public TraitType type() {
        return TraitType.DETRIMENTAL;
    }

    private static final int COOLDOWN_TICKS = 60;

    @Override
    public void onMove(ServerPlayerEntity player, ArtifactData data, double distance) {
        boolean isSprinting = player.isSprinting();
        boolean wasSprintingLastTick = data.hasFlag("was_sprinting");
        boolean wasCrouchingLastTick = data.hasFlag("was_crouching");
        boolean isCrouching = player.isInSneakingPose();

        // Detect sprint start or uncrouching
        boolean shouldTrigger = (isSprinting && !wasSprintingLastTick) || 
                               (isCrouching && wasCrouchingLastTick) ||
                               (!isCrouching && wasCrouchingLastTick);


        if (shouldTrigger) {
            // Check cooldown
            float lastTriggerTick = data.getStat("last_trigger_tick");
            long worldTick = player.getEntityWorld().getTime();
            
            if (worldTick - lastTriggerTick >= COOLDOWN_TICKS) {
                float intensity = data.getIntensity();
                float triggerChance = 0.1f + (0.1f * intensity);
                float roll = player.getRandom().nextFloat();

                if (roll < triggerChance) {
                    applyDebuffs(player, intensity);
                    data.setStat("last_trigger_tick", (float) worldTick);
                }
            } else {
            }
        }
        if (isSprinting) {
            data.addFlag("was_sprinting");
        } else {
            data.removeFlag("was_sprinting");
        }
        
        if (isCrouching) {
            data.addFlag("was_crouching");
        } else {
            data.removeFlag("was_crouching");
        }
    }

    private void applyDebuffs(ServerPlayerEntity player, float intensity) {
        int duration = Math.max(40, (int)(100 - 10 * intensity));

        player.addStatusEffect(
            new StatusEffectInstance(StatusEffects.BLINDNESS, duration, 0, false, false)
        );

        player.addStatusEffect(
            new StatusEffectInstance(StatusEffects.SLOWNESS, duration, 1, false, false)
        );

        // scales with intensity (0.01-0.99): 0.5-2.0 health
        float damage = 0.5f + (1.5f * intensity);
        player.damage(player.getEntityWorld(), player.getEntityWorld().getDamageSources().sting(player), damage);
    }
}
