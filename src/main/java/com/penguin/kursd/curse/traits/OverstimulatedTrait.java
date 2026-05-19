package com.penguin.kursd.curse.traits;

import com.penguin.kursd.contexts.MovementContext;
import com.penguin.kursd.curse.ArtifactData;
import com.penguin.kursd.curse.ArtifactTrait;
import com.penguin.kursd.curse.TraitType;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class OverstimulatedTrait implements ArtifactTrait {

    private static final float SPEED_BOOST = 1.6f;

    private static final float MOVEMENT_CYCLE = 20f;

    private static final float DAMAGE = 2.0f;

    @Override
    public String id() {
        return "overstimulated";
    }
    
    @Override
    public TraitType type() {
        return TraitType.BENEFICIAL;
    }

    @Override
    public void modifyMovement(
        PlayerEntity player,
        ArtifactData data,
        MovementContext ctx
    ) {
        ctx.xzMultiplier *= SPEED_BOOST;
    }

    @Override
    public void onMove(ServerPlayerEntity player, ArtifactData data, double distance) {

        double horizontalSpeed = distance;

        float movedAmount = data.getStat("moved_amount");

        movedAmount += (float) horizontalSpeed;

        if (movedAmount >= MOVEMENT_CYCLE) {

            player.damage(
                player.getEntityWorld(),
                player.getDamageSources().flyIntoWall(),
                DAMAGE
            );
            movedAmount -= MOVEMENT_CYCLE;
        }

        data.setStat("moved_amount", movedAmount);
    }
}