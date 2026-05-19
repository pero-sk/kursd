package com.penguin.kursd.curse.traits;

import com.penguin.kursd.curse.ArtifactData;
import com.penguin.kursd.curse.ArtifactTrait;
import com.penguin.kursd.curse.TraitType;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class RavagerTrait implements ArtifactTrait {

    @Override
    public String id() {
        return "ravager";
    }

    @Override
    public TraitType type() {
        return TraitType.BENEFICIAL;
    }

    @Override
    public void onKill(ServerPlayerEntity player, LivingEntity target, ArtifactData data) {

        int momentum = data.getMemory("momentum");

        momentum += 2;

        data.setMemory("momentum", Math.min(momentum, 50));

        float damage = data.getStat("damage_boost");
        damage += 0.5f;

        data.setStat("damage_boost", Math.min(damage, 10f));
    }

    @Override
    public void onTick(ServerPlayerEntity player, ItemStack stack, ArtifactData data) {

        int momentum = data.getMemory("momentum");

        if (momentum > 0) {
            momentum -= 1;
            data.setMemory("momentum", momentum);
        } else {
            float damage = data.getStat("damage_boost");
            data.setStat("damage_boost", Math.max(0, damage - 0.2f));
        }
    }
}