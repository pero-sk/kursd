package com.penguin.kursd.curse.traits;

import com.penguin.kursd.Ksd;
import com.penguin.kursd.curse.ArtifactData;
import com.penguin.kursd.curse.ArtifactTrait;
import com.penguin.kursd.curse.TraitType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class OccludedTrait implements ArtifactTrait {

    @Override
    public String id() {
        return "occluded";
    }

    @Override
    public TraitType type() {
        return TraitType.DETRIMENTAL;
    }

    @Override
    public void onTick(ServerPlayerEntity player, ItemStack stack, ArtifactData data) {
        player.addStatusEffect(new StatusEffectInstance(
            Ksd.OCCLUDED,
            100,
            0,
            true,
            false,
            false
        ));
    }
}