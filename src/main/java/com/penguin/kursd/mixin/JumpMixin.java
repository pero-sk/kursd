package com.penguin.kursd.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.penguin.kursd.curse.ArtifactData;
import com.penguin.kursd.curse.ArtifactManager;
import com.penguin.kursd.curse.ArtifactTrait;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(LivingEntity.class)
public abstract class JumpMixin {

    @ModifyReturnValue(method = "getJumpVelocity", at = @At("RETURN"))
    private float kursd$jump(float original) {

        if (!((Object)this instanceof PlayerEntity player)) {
            return original;
        }

        float value = original;

        for (ArtifactData data : ArtifactManager.getActiveArtifacts(player)) {

            for (ArtifactTrait trait : data.getTraits()) {
                value = trait.modifyJump(player, data, value);
            }
        }

        return value;
    }
}