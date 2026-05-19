package com.penguin.kursd.curse.traits;

import com.penguin.kursd.curse.ArtifactData;
import com.penguin.kursd.curse.ArtifactTrait;
import com.penguin.kursd.curse.TraitType;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class WhisperingTrait implements ArtifactTrait {

    // what am I meant to write that is cryptic but not corny?
    private static final String[] WHISPERS = {
        "[] remembers your hands",
        "do not stop moving",
        "[] grows when you hesitate",
        "you fed [] before you knew",
        "[] is still hungry"
    };

    @Override
    public String id() {
        return "whispering";
    }

    @Override
    public TraitType type() {
        return TraitType.NEUTRAL;
    }

    @Override
    public void onTick(ServerPlayerEntity player, ItemStack stack, ArtifactData data) {

        int hunger = data.getMemory("hunger");
        if (hunger < 30) return;

        if (player.getRandom().nextFloat() < 0.02f) {
            String msg = WHISPERS[
                player.getRandom().nextInt(WHISPERS.length)
            ];

            player.sendMessage(Text.literal(msg), true);
        }
    }
}