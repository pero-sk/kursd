package com.penguin.kursd.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.penguin.kursd.contexts.MovementContext;
import com.penguin.kursd.curse.ArtifactData;
import com.penguin.kursd.curse.ArtifactManager;
import com.penguin.kursd.curse.ArtifactTrait;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(LivingEntity.class)
public abstract class MovementMixin {

    @ModifyReturnValue(method = "getMovementSpeed", at = @At("RETURN"))
    private float kursd$move(float original) {

        if (!((Object)this instanceof PlayerEntity player)) {
            return original;
        }

        ArtifactManager.resetMovementContext(player);

        MovementContext ctx = ArtifactManager.getMovementContext(player);

        for (ArtifactData data : ArtifactManager.getActiveArtifacts(player)) {
            for (ArtifactTrait trait : data.getTraits()) {
                trait.modifyMovement(player, data, ctx);
            }
        }

        return (float)(original * ctx.xzMultiplier);
    }
}