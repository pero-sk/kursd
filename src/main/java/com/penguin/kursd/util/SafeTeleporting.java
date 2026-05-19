package com.penguin.kursd.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;

public class SafeTeleporting {

    /**
     * Attempts to teleport the player to a safe nearby position.
     *
     * @param player player to teleport
     * @param radius max horizontal teleport distance
     * @param attempts amount of positions to try
     * @return true if teleport succeeded
     */

    public static boolean randomTeleport(
        ServerPlayerEntity player,
        int radius,
        int attempts
    ) {

        ServerWorld world = player.getEntityWorld();
        Random random = player.getRandom();

        BlockPos origin = player.getBlockPos();

        for (int i = 0; i < attempts; i++) {

            int dx = random.nextBetween(-radius, radius);
            int dz = random.nextBetween(-radius, radius);

            BlockPos ground;
            BlockPos target;

            if (!world.isFlat()) { 
                ground = world.getTopPosition(
                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                    origin.add(dx, 0, dz)
                );

                target = ground.up();
            } else {
                // ground.up() doesn't work in superflat,
                // it seems to put you in the air when in superflat.
                // So the below check would fail
                ground = world.getTopPosition(
                    Heightmap.Type.WORLD_SURFACE,
                    origin.add(dx, 0, dz)
                );
                target = ground;
            }

            if (!world.getBlockState(target).isAir()) continue;
            if (!world.getBlockState(target.up()).isAir()) continue;

            if (world.getBlockState(target.down()).isAir()) continue;

            player.networkHandler.requestTeleport(
                target.getX() + 0.5,
                target.getY(),
                target.getZ() + 0.5,
                player.getYaw(),
                player.getPitch()
            );

            return true;
        }

        return false;
    }
}