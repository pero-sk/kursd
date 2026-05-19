package com.penguin.kursd.client;

import com.penguin.kursd.Ksd;
import com.penguin.kursd.curse.ArtifactData;
import com.penguin.kursd.curse.ArtifactManager;
import com.penguin.kursd.curse.ArtifactTrait;
import com.penguin.kursd.curse.TraitRarity;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class KsdClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

		ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {

			if (!Ksd.DEV_TOOLS) return;

			ArtifactData data = ArtifactData.readFrom(stack);
			if (data == null) return;
			lines.add(Text.literal(
				"intensity: " + String.format("%.2f", data.getIntensity())
			).formatted(Formatting.DARK_GRAY));

			lines.add(Text.literal(
				"stage: " + data.getStage()
			).formatted(Formatting.DARK_GRAY));

			lines.add(Text.literal(
				"awakening: " + data.isAwoken()
			).formatted(Formatting.DARK_GRAY));

			if (!data.getTraits().isEmpty()) {

				lines.add(Text.literal("traits:")
					.formatted(Formatting.GRAY));

				for (ArtifactTrait trait : data.getTraits()) {

					lines.add(Text.literal(
						" - " + ArtifactManager.getTranslation(trait.displayKey()).getString()
					).formatted(Formatting.GRAY));
				}
			}

			if (!data.getStats().isEmpty()) {

				lines.add(Text.literal("stats:")
					.formatted(Formatting.DARK_GRAY));

				data.getStats().forEach((k, v) -> {

					lines.add(Text.literal(
						" - " + k + ": " + String.format("%.2f", v)
					).formatted(Formatting.DARK_GRAY));
				});
			}

			if (!data.getMemories().isEmpty()) {

				lines.add(Text.literal("memories:")
					.formatted(Formatting.GRAY));

				data.getMemories().forEach((k, v) -> {

					lines.add(Text.literal(
						" - " + k + ": " + v
					).formatted(Formatting.GRAY));
				});
			}

			if (!data.getFlags().isEmpty()) {

				lines.add(Text.literal("flags:")
					.formatted(Formatting.DARK_GRAY));

				for (String flag : data.getFlags()) {

					lines.add(Text.literal(
						" - " + flag
					).formatted(Formatting.DARK_GRAY));
				}
			}

			if (!data.getTraits().isEmpty()) {
				TraitRarity rarity = TraitRarity.values()[data.getTraits().size()-1];

				lines.add(Text.literal("rarity: "+rarity).formatted(Formatting.GRAY));
			}
		});
    }
}