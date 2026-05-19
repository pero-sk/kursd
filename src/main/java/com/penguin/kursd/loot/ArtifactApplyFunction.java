package com.penguin.kursd.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.penguin.kursd.curse.ArtifactData;
import com.penguin.kursd.data.ArtifactApplicableDefinition;
import com.penguin.kursd.data.ArtifactApplicableRegistry;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.text.Text;

import java.util.List;

public class ArtifactApplyFunction extends ConditionalLootFunction {

    public static final MapCodec<ArtifactApplyFunction> CODEC =
        RecordCodecBuilder.mapCodec(instance ->
            ConditionalLootFunction.addConditionsField(instance)
                .apply(instance, ArtifactApplyFunction::new)
        );

    protected ArtifactApplyFunction(List<LootCondition> conditions) {
        super(conditions);
    }

    // first loop against ArtifactApplicableRegistry
    // second check if it passes the percent chance in that ArtifactApplicableDefinition
    // third check if the ArtifactApplicationDefinition actually allows the item
    // fourth, generate the artifact
    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {

        var random = context.getRandom();

        var all = ArtifactApplicableRegistry.getAll();

        for (ArtifactApplicableDefinition def : all.values()) {
            if (random.nextFloat() > def.chance()) {
                continue;
            }

            if (!def.items().contains(stack.getItem())) {
                continue;
            }

            ArtifactData artifact =
                ArtifactGenerator.generate(random);

            artifact.writeTo(stack);

            stack.set(
                DataComponentTypes.CUSTOM_NAME,
                Text.literal(artifact.getName())
            );

            return stack;
        }

        return stack;
    }

    @Override
    public LootFunctionType<? extends ConditionalLootFunction> getType() {
        return ArtifactLootModifier.ARTIFACT_FUNCTION;
    }

    public static class Builder
        extends ConditionalLootFunction.Builder<Builder> {

        @Override
        protected Builder getThisBuilder() {
            return this;
        }

        @Override
        public LootFunction build() {
            return new ArtifactApplyFunction(getConditions());
        }
    }
}