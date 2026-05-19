package com.penguin.kursd.curse.traits;

import com.penguin.kursd.curse.ArtifactData;
import com.penguin.kursd.curse.ArtifactTrait;
import com.penguin.kursd.curse.TraitType;
import com.penguin.kursd.util.SafeTeleporting;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class EndermitoticTrait implements ArtifactTrait {

    @Override
    public String id() {
        return "endermitotic";
    }
    
    @Override
    public void onDamaged(ServerPlayerEntity player, DamageSource source, float amount, ArtifactData data) {
        SafeTeleporting.randomTeleport(player, 20, 32);
    }

    @Override
    public TraitType type() {
        return TraitType.NEUTRAL;
    }

    @Override
    public void onTick(
        ServerPlayerEntity player,
        ItemStack stack,
        ArtifactData data
    ) {

        if (player.isTouchingWaterOrRain()) {

            player.damage(
                player.getEntityWorld(),
                player.getDamageSources().magic(),
                1.0f
            );
        }
    }
}
