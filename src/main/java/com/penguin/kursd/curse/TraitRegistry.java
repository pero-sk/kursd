package com.penguin.kursd.curse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TraitRegistry {

    private static final Map<String, ArtifactTrait> TRAITS =
        new HashMap<>();

    public static void register(ArtifactTrait trait) {
        TRAITS.put(trait.id(), trait);
    }

    public static ArtifactTrait get(String id) {
        return TRAITS.get(id);
    }

    public static List<ArtifactTrait> all() {
        return new ArrayList<>(TRAITS.values());
    }
}