package com.penguin.kursd.curse.traits;

import com.penguin.kursd.curse.ArtifactData;
import com.penguin.kursd.curse.ArtifactTrait;
import com.penguin.kursd.curse.TraitType;

import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.Random;

import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.block.Block;

import java.util.Set;

public class DustmindedTrait implements ArtifactTrait {

    private static final Set<TagKey<Block>> ORES = Set.of(
        BlockTags.COAL_ORES,
        BlockTags.IRON_ORES,
        BlockTags.GOLD_ORES,
        BlockTags.COPPER_ORES,
        BlockTags.LAPIS_ORES,
        BlockTags.DIAMOND_ORES,
        BlockTags.EMERALD_ORES,
        BlockTags.REDSTONE_ORES
    );

    @Override
    public String id() {
        return "dust_minded";
    }

    @Override
    public TraitType type() {
        return TraitType.BENEFICIAL;
    }

    @Override
    public void onMine(ServerPlayerEntity player, BlockState state, ArtifactData data) {

        boolean isOre = false;

        for (TagKey<Block> tag : ORES) {
            if (state.isIn(tag)) {
                isOre = true;
                break;
            }
        }

        if (!isOre) return;

        float intensity = data.getIntensity();

        if (!player.isDead() && intensity > 0.4f) {

            Random random = Random.create();

            if (random.nextBoolean()) {
                float heal = 0.5f + (intensity * 1.5f);
                player.heal(heal);
            }
        }
    }
    
}
