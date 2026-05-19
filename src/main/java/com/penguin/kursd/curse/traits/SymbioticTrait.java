package com.penguin.kursd.curse.traits;

import com.penguin.kursd.curse.ArtifactData;
import com.penguin.kursd.curse.ArtifactTrait;
import com.penguin.kursd.curse.TraitType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;

public class SymbioticTrait implements ArtifactTrait {

    private static final float MIRROR_FRACTION = 0.5f;
    private static final String FLAG_PREFIX = "symbiotic_";

    @Override
    public String id() {
        return "symbiotic";
    }

    @Override
    public TraitType type() {
        return TraitType.BENEFICIAL;
    }

    private static void storeUuid(ArtifactData data, UUID uuid) {
        data.getFlags().removeIf(f -> f.startsWith(FLAG_PREFIX));
        data.addFlag(FLAG_PREFIX + uuid);
        data.writeTo(data.getSourceStack());
    }

    private static UUID loadUuid(ArtifactData data) {
        return data.getFlags().stream()
            .filter(f -> f.startsWith(FLAG_PREFIX))
            .map(f -> f.substring(FLAG_PREFIX.length()))
            .map(UUID::fromString)
            .findFirst()
            .orElse(null);
    }

    @Override
    public void onAttack(ServerPlayerEntity player, LivingEntity target, ArtifactData data) {
        storeUuid(data, target.getUuid());
    }

    @Override
    public void onDamaged(ServerPlayerEntity player, DamageSource source,
                          float amount, ArtifactData data) {

        UUID targetUuid = loadUuid(data);
        if (targetUuid == null) return;

        if (!(player.getEntityWorld() instanceof ServerWorld world)) return;

        var entity = world.getEntity(targetUuid);
        if (!(entity instanceof LivingEntity livingTarget)) return;
        if (!livingTarget.isAlive()) return;

        livingTarget.damage(
            world,
            world.getDamageSources().magic(),
            amount * MIRROR_FRACTION
        );
    }
}