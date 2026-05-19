package com.penguin.kursd.curse;

import net.minecraft.util.math.random.Random;

public enum TraitRarity {
    COMMON(1, 8),
    UNCOMMON(2, 1),
    RARE(3, 0.5f),
    GREATER(4, 0.25f),
    UNHOLY(5, 0.125f);

    public final int traitCount;
    public final float weight;

    TraitRarity(int traitCount, float weight) {
        this.traitCount = traitCount;
        this.weight = weight;
    }

    public static TraitRarity roll(Random random) {
        float total = 0f;

        for (TraitRarity r : values()) {
            total += r.weight;
        }

        float roll = random.nextFloat() * total;

        for (TraitRarity r : values()) {
            roll -= r.weight;
            if (roll <= 0) {
                return r;
            }
        }

        return COMMON;
    }
}