package com.penguin.kursd.curse.traits;

import com.penguin.kursd.curse.ArtifactData;
import com.penguin.kursd.curse.ArtifactTrait;
import com.penguin.kursd.curse.TraitType;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class CinderLacedTrait implements ArtifactTrait {

    // I made this trait early on and I can't be arsed to remember what it does so I am not touching this code.

    @Override
    public String id() {
        return "cinder_laced";
    }

    @Override
    public TraitType type() {
        return TraitType.NEUTRAL;
    }

    @Override
    public void onAttack(ServerPlayerEntity player, LivingEntity target, ArtifactData data) {

        float intensity = data.getIntensity();

        int burnTime = (int) (60 + intensity * 120);

        target.setOnFireFor(burnTime / 20);

        if (intensity > 0.6f) {
            target.setOnFireFor(burnTime / 10);
        }
    }

    @Override
    public void onTick(ServerPlayerEntity player, ItemStack stack, ArtifactData data) {

        if (player.isOnFire() && data.getIntensity() > 0.7f && player.age % 40 == 0) {
            player.extinguish();
        }
    }
}