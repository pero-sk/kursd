package com.penguin.kursd.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.penguin.kursd.curse.ArtifactData;
import com.penguin.kursd.curse.ArtifactEvents;
import com.penguin.kursd.curse.ArtifactManager;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

@Mixin(ServerPlayerInteractionManager.class)
public class BlockBreakMixin {

    @Shadow @Final
    private ServerPlayerEntity player;

    @Inject(
        method = "tryBreakBlock",
        at = @At("HEAD")
    )
    private void onBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {

        ServerWorld world = player.getEntityWorld();

        for (ArtifactData data : ArtifactManager.getActiveArtifacts(player)) {

            ItemStack stack = data.getSourceStack();

            BlockState state = world.getBlockState(pos);

            ArtifactEvents.mine(player, state, data);

            data.writeTo(stack);
        }

    }
}