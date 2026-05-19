package com.penguin.kursd.curse.traits;

import com.penguin.kursd.curse.ArtifactData;
import com.penguin.kursd.curse.ArtifactTrait;
import com.penguin.kursd.curse.TraitType;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class ParasiticTrait implements ArtifactTrait {

    @Override
    public String id() {
        return "parasitic";
    }

    @Override
    public TraitType type() {
        return TraitType.DETRIMENTAL;
    }

    @Override
    public int weight() {
        return 8;
    }

    @Override
    public void onTick(ServerPlayerEntity player, ItemStack stack, ArtifactData data) {

        int hunger = data.getMemory("hunger");
        float stability = data.getStat("stability");

        hunger += 1;
        stability -= 0.005f;

        data.setMemory("hunger", hunger);
        data.setStat("stability", stability);

        if (hunger > 50) {
            player.damage(
                player.getEntityWorld(),
                player.getDamageSources().magic(),
                1.0f
            );
        }
    }
}