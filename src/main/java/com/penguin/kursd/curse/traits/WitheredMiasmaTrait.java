package com.penguin.kursd.curse.traits;

import java.util.List;
import net.minecraft.util.TypeFilter;
import com.penguin.kursd.curse.ArtifactData;
import com.penguin.kursd.curse.ArtifactTrait;
import com.penguin.kursd.curse.TraitType;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class WitheredMiasmaTrait implements ArtifactTrait {

    @Override
    public String id() {
        return "withered_miasma";
    }

    @Override
    public TraitType type() {
        return TraitType.NEUTRAL;
    }

    @Override
    public void onTick(ServerPlayerEntity player, ItemStack stack, ArtifactData data) {

        float intensity = data.getIntensity();

        var world = player.getEntityWorld();

        List<LivingEntity> nearby = world.getEntitiesByType(
            TypeFilter.instanceOf(LivingEntity.class),
            player.getBoundingBox().expand(4.0),
            e -> e.isAlive() && e != player
        );

        for (LivingEntity e : nearby) {

            if (e instanceof ServerPlayerEntity) continue;

            if (player.getRandom().nextFloat() < 0.05f * intensity) {
                e.damage(player.getEntityWorld(), player.getDamageSources().magic(), 1.0f);
            }
        }
    }
}