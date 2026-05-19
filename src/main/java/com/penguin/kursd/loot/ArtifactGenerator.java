package com.penguin.kursd.loot;

import com.penguin.kursd.curse.ArtifactTrait;
import com.penguin.kursd.curse.TraitRarity;
import com.penguin.kursd.curse.TraitRegistry;
import com.penguin.kursd.curse.TraitType;
import com.penguin.kursd.curse.ArtifactData;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;


public class ArtifactGenerator {

    public static ArtifactData generate(Random random) {

        float intensity =
            0.2f + random.nextFloat() * 0.8f;

        ArtifactData data = new ArtifactData(
            generateName(random),
            intensity
        );

        TraitRarity rarity = TraitRarity.roll(random);
        int traitCount = rarity.traitCount;

        List<ArtifactTrait> selected =
            rollTraits(random, traitCount);

        for (ArtifactTrait trait : selected) {
            data.addTrait(trait);
        }

        return data;
    }

    private static List<ArtifactTrait> rollTraits(
        Random random,
        int count
    ) {

        List<ArtifactTrait> pool =
            new ArrayList<>(TraitRegistry.all());

        List<ArtifactTrait> result =
            new ArrayList<>();

        while (!pool.isEmpty() && result.size() < count) {

            ArtifactTrait chosen =
                weightedRoll(random, pool);

            boolean compatible = true;

            for (ArtifactTrait existing : result) {
                if (!chosen.compatibleWith(existing)
                    || !existing.compatibleWith(chosen)) {

                    compatible = false;
                    break;
                }
            }

            pool.remove(chosen);

            if (compatible) {
                result.add(chosen);
            }
        }

        return result;
    }

    // first weigh against trait TYPE: NEUTRAL > DETRIMENTAL > BENEFICIAL
    // second weigh against weight of trait, ArtifactTrait.weight()
    // third get the output trait
    private static ArtifactTrait weightedRoll(Random random, List<ArtifactTrait> pool) {
        int totalTypeWeight = 0;

        for (ArtifactTrait trait : pool) {
            totalTypeWeight += typeWeight(trait.type());
        }

        int roll = random.nextInt(totalTypeWeight);

        TraitType chosenType = null;

        for (ArtifactTrait trait : pool) {
            roll -= typeWeight(trait.type());

            if (roll < 0) {
                chosenType = trait.type();
                break;
            }
        }

        if (chosenType == null) {
            return pool.getFirst();
        }

        List<ArtifactTrait> filtered = new ArrayList<>();

        for (ArtifactTrait trait : pool) {
            if (trait.type() == chosenType) {
                filtered.add(trait);
            }
        }

        if (filtered.isEmpty()) {
            return pool.getFirst();
        }

        int total = 0;

        for (ArtifactTrait trait : filtered) {
            total += trait.weight();
        }

        int innerRoll = random.nextInt(total);

        for (ArtifactTrait trait : filtered) {
            innerRoll -= trait.weight();

            if (innerRoll < 0) {
                return trait;
            }
        }

        return filtered.getFirst();
    }

    private static String generateName(Random random) {
        return "§kBECURSED";
    }


    private static int typeWeight(TraitType type) {
        return switch (type) {
            case NEUTRAL -> 60;
            case DETRIMENTAL -> 30;
            case BENEFICIAL -> 10;
        };
    }
}