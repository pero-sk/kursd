package com.penguin.kursd.data;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.*;

public class ArtifactApplicableRegistry {

    private static final Map<String, ArtifactApplicableDefinition> DEFINITIONS = new HashMap<>();

    public static void register(String name, int chance, List<String> itemIds) {

        Set<Item> items = new HashSet<>();

        for (String idStr : itemIds) {
            Identifier id = Identifier.of(idStr);
            items.add(Registries.ITEM.get(id));
        }

        ArtifactApplicableDefinition definition = new ArtifactApplicableDefinition(name, chance, items);

        DEFINITIONS.put(name, definition);

        System.out.println("[KURSD] Loaded definition: " + name + " (" + items.size() + ")");
    }

    public static boolean isEligible(ItemStack stack, String definition) {
        Set<Item> set = DEFINITIONS.get(definition).items();
        return set != null && set.contains(stack.getItem());
    }

    public static Set<Item> get(String name) {
        return DEFINITIONS.getOrDefault(name, new ArtifactApplicableDefinition(name, 0, null)).items();
    }

    public static Map<String, ArtifactApplicableDefinition> getAll() {
        return DEFINITIONS;
    }
}