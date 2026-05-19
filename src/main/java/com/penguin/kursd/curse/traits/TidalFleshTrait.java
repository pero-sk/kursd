package com.penguin.kursd.curse.traits;

import com.penguin.kursd.curse.ArtifactData;
import com.penguin.kursd.curse.ArtifactTrait;
import com.penguin.kursd.curse.TraitType;
import com.penguin.kursd.contexts.HealthContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class TidalFleshTrait implements ArtifactTrait {

    private static final float BASE_HEALTH = 20.0f;
    private static final float AMPLITUDE = 6.0f;
    private static final float PERIOD_TICKS = 200.0f;

    private static final String STAT_TICK      = "tidal_tick";
    private static final String STAT_LAST_MAX  = "tidal_last_max";

    @Override
    public String id() {
        return "tidal_flesh";
    }

    @Override
    public TraitType type() {
        return TraitType.NEUTRAL;
    }

    private static float computeMax(float tick) {
        double angle = (2.0 * Math.PI * tick) / PERIOD_TICKS;
        return BASE_HEALTH + (AMPLITUDE * (float) Math.sin(angle));
    }

    @Override
    public void onTick(ServerPlayerEntity player, ItemStack stack, ArtifactData data) {
        float tick = data.getStat(STAT_TICK);
        float lastMax = data.getStat(STAT_LAST_MAX);

        // Advance the wave
        tick = (tick + 1) % PERIOD_TICKS;
        data.setStat(STAT_TICK, tick);

        float newMax = computeMax(tick);
        float delta = newMax - lastMax;

        if (delta > 0) {
            // Wave rising
            float healAmount = Math.min(delta, newMax - player.getHealth());
            if (healAmount > 0) {
                player.heal(healAmount);
            }
        } else if (delta < 0) {
            // Wave falling
            if (player.getHealth() > newMax) {
                float damageAmount = Math.min(-delta, player.getHealth() - newMax);
                player.damage(
                    player.getEntityWorld(),
                    player.getDamageSources().magic(),
                    damageAmount
                );
            }
        }

        data.setStat(STAT_LAST_MAX, newMax);
    }

    @Override
    public void modifyHealth(PlayerEntity player, ArtifactData data, HealthContext ctx) {
        float tick = data.getStat(STAT_TICK);
        ctx.maxHealth = computeMax(tick);
    }

    @Override
    public void onEquip(ServerPlayerEntity player, ItemStack stack, ArtifactData data) {
        if (data.getStat(STAT_TICK) == 0) {
            data.addStat(STAT_TICK, 0f);
        }

        // no garbage delta
        data.setStat(STAT_LAST_MAX, computeMax(data.getStat(STAT_TICK)));
    }
}