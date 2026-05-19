package com.penguin.kursd.data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.item.Item;

public class ArtifactApplicableDefinition {

    private final int chance;
    private final String name;
    private final Set<Item> items;

    public ArtifactApplicableDefinition(String name, int chance, Set<Item> items) {
        this.chance = chance;
        this.items = items;
        this.name = name;
    }

    public ArtifactApplicableDefinition fromList(String name, int chance, List<Item> items) {
        Set<Item> items2 = new HashSet<>(items);
        return new ArtifactApplicableDefinition(name, chance, items2);
    }

    public String name() {
        return name;
    }

    public int chance() {
        return chance;
    }

    public Set<Item> items() {
        return items;
    }
}