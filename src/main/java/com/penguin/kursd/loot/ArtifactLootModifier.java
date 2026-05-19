package com.penguin.kursd.loot;

import java.util.Set;

import com.penguin.kursd.Ksd;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class ArtifactLootModifier {

    public static final LootFunctionType<ArtifactApplyFunction>
        ARTIFACT_FUNCTION =
            new LootFunctionType<>(ArtifactApplyFunction.CODEC);

    private static final Set<RegistryKey<LootTable>> ALLOWED_LOOT_TABLES = Set.of(
        LootTables.ABANDONED_MINESHAFT_CHEST,
        LootTables.BURIED_TREASURE_CHEST,
        LootTables.END_CITY_TREASURE_CHEST,
        LootTables.SIMPLE_DUNGEON_CHEST,
        LootTables.STRONGHOLD_CORRIDOR_CHEST
    );

    public static void init() {

        Registry.register(
            Registries.LOOT_FUNCTION_TYPE,
            Identifier.of(Ksd.MOD_ID, "artifact_apply"),
            ARTIFACT_FUNCTION
        );

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {

            if (!ALLOWED_LOOT_TABLES.contains(key)) return;

            tableBuilder.modifyPools(poolBuilder -> {
                poolBuilder.apply(new ArtifactApplyFunction.Builder());
            });
        });
    }
}