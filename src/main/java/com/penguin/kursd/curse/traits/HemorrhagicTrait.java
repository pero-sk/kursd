package com.penguin.kursd.curse.traits;

import com.penguin.kursd.curse.ArtifactData;
import com.penguin.kursd.curse.ArtifactTrait;
import com.penguin.kursd.curse.TraitType;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class HemorrhagicTrait implements ArtifactTrait {

    @Override
    public String id() {
        return "hemorrhagic";
    }

    @Override
    public TraitType type() {
        return TraitType.DETRIMENTAL;
    }

    @Override
    public void onAttack(ServerPlayerEntity player, LivingEntity target, ArtifactData data) {

        float intensity = data.getIntensity();

        float healthRatio = player.getHealth() / player.getMaxHealth();

        float bonus = (1.0f - healthRatio) * (0.6f + intensity * 0.6f);

        float extraDamage = 1.0f + bonus;

        target.damage(player.getEntityWorld(), player.getEntityWorld().getDamageSources().playerAttack(player),
                1.0f * extraDamage);
    }

    @Override
    public void onDamaged(ServerPlayerEntity player, DamageSource source, float amount, ArtifactData data) {

        float intensity = data.getIntensity();

        if (player.getHealth() > 4.0f && intensity > 0.4f) {
            player.damage(player.getEntityWorld(), player.getEntityWorld().getDamageSources().magic(),
                    0.5f * intensity);
        }
    }

    @Override
    public void onTick(ServerPlayerEntity player, ItemStack stack, ArtifactData data) {

        if (player.age % 100 == 0 && player.getHealth() > 6.0f) {
            player.damage(player.getEntityWorld(), player.getEntityWorld().getDamageSources().magic(),
                    0.25f * data.getIntensity());
        }
    }
}