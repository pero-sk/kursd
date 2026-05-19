package com.penguin.kursd.curse.traits;

import com.penguin.kursd.curse.ArtifactData;
import com.penguin.kursd.curse.ArtifactTrait;
import com.penguin.kursd.curse.TraitType;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class BloodfedTrait implements ArtifactTrait {

    @Override
    public String id() {
        return "bloodfed";
    }

    @Override
    public TraitType type() {
        return TraitType.BENEFICIAL;
    }

    @Override
    public int weight() {
        return 10;
    }

    @Override
    public void onKill(ServerPlayerEntity player, LivingEntity target, ArtifactData data) {

        int hunger = data.getMemory("hunger");
        int kills = data.getMemory("kills");

        hunger -= 15;
        kills += 1;

        data.setMemory("hunger", Math.max(hunger, 0));
        data.setMemory("kills", kills);

        data.addFlag("fed_recently");
    }
}