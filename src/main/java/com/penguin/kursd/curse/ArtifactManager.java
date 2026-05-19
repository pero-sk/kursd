package com.penguin.kursd.curse;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.*;

import com.penguin.kursd.contexts.EnvironmentContext;
import com.penguin.kursd.contexts.HealthContext;
import com.penguin.kursd.contexts.MovementContext;

public class ArtifactManager {

    private static final Map<UUID, Map<String, ArtifactData>> lastArtifacts = new HashMap<>();
    private static final Map<UUID, Map<String, Boolean>> lastAwoken = new HashMap<>();
    private static final Map<UUID, Map<String, Integer>> lastStages = new HashMap<>();

    private static final Set<UUID> updatedThisTick = new HashSet<>();
    private static long lastTick = -1;

    private static final Map<UUID, Vec3d> lastPositions = new HashMap<>();

    private static final Map<UUID, MovementContext> movementContexts = new HashMap<>();
    private static final Map<UUID, HealthContext> healthContexts = new HashMap<>();
    private static final Map<UUID, EnvironmentContext> environmentContexts = new HashMap<>();

    public static Text getTranslation(String key) {
        return Text.translatable(key);
    }

    public static MovementContext getMovementContext(PlayerEntity player) {
        return movementContexts.computeIfAbsent(player.getUuid(), u -> new MovementContext());
    }

    public static void resetMovementContext(PlayerEntity player) {
        movementContexts.put(player.getUuid(), new MovementContext());
    }


    public static HealthContext getHealthContext(PlayerEntity player) {
        return healthContexts.computeIfAbsent(player.getUuid(), u -> new HealthContext());
    }

    public static void resetHealthContext(PlayerEntity player) {
        healthContexts.put(player.getUuid(), new HealthContext());
    }

    public static EnvironmentContext getEnvironmentContext(PlayerEntity player) {
        return environmentContexts.computeIfAbsent(player.getUuid(), u -> new EnvironmentContext());
    }

    public static void resetEnvironmentContext(PlayerEntity player) {
        environmentContexts.put(player.getUuid(), new EnvironmentContext());
    }

    // ran once per tick
    public static void buildContexts(ServerPlayerEntity player) {

        HealthContext health = getHealthContext(player);
        EnvironmentContext env = getEnvironmentContext(player);

        // reset defaults each tick
        health.maxHealth = 20.0f;
        health.healthCap = Float.POSITIVE_INFINITY;
        health.regenMultiplier = 1.0f;

        env.air = 300;
        env.preventDrowning = false;
        env.swimSpeedMultiplier = 1.0f;
        env.waterSpeedMultiplier = 1.0f;
        env.frictionMultiplier = 1.0f;

        // apply all artifacts
        for (ArtifactData data : getSnapshot(player)) {
            for (ArtifactTrait trait : data.getTraits()) {

                trait.modifyHealth(player, data, health);
                trait.modifyEnvironment(player, data, env);
            }
        }
    }

    // health, then environment (environment is only parially implemented)
    public static void applyContexts(ServerPlayerEntity player) {
        HealthContext health = getHealthContext(player);

        var attr = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);

        if (attr != null) {
            float finalMax = health.maxHealth;

            attr.setBaseValue(finalMax);

            if (player.getHealth() > finalMax) {
                player.setHealth(finalMax);
            }
        }

        EnvironmentContext env = getEnvironmentContext(player);

        if (env.preventDrowning) {
            player.setAir(env.air);
        } else {
            player.setAir(Math.min(player.getAir(), env.air));
        }
    }

    public static void updateLastPosition(ServerPlayerEntity player, Vec3d pos) {
        lastPositions.put(player.getUuid(), pos);
    }

    public static Vec3d getMovementDelta(PlayerEntity player) {

        Vec3d current = new Vec3d(
            player.getX(),
            player.getY(),
            player.getZ()
        );

        Vec3d last = lastPositions.get(player.getUuid());

        if (last == null) {
            lastPositions.put(player.getUuid(), current);
            return Vec3d.ZERO;
        }

        Vec3d delta = current.subtract(last);

        lastPositions.put(player.getUuid(), current);

        return delta;
    }

    public static void handleMovement(ServerPlayerEntity player) {

        Vec3d current = new Vec3d(player.getX(), player.getY(), player.getZ());
        Vec3d last = lastPositions.get(player.getUuid());

        if (last != null) {

            double distance = current.distanceTo(last);

            if (distance > 0.001) {
                for (ArtifactData data : getSnapshot(player)) {
                    ArtifactEvents.move(player, data, distance);
                }
            }
        }

        lastPositions.put(player.getUuid(), current);
    }

    public static void update(ServerPlayerEntity player) {

        long worldTick = player.getEntityWorld().getTime();

        if (worldTick != lastTick) {
            updatedThisTick.clear();
            lastTick = worldTick;
        }

        if (!updatedThisTick.add(player.getUuid())) {
            return;
        }

        // snapshots
        Map<String, ArtifactData> previous =
            lastArtifacts.getOrDefault(player.getUuid(), new HashMap<>());

        Map<String, Boolean> previousAwoken =
            lastAwoken.getOrDefault(player.getUuid(), new HashMap<>());

        Map<String, Integer> previousStages =
            lastStages.getOrDefault(player.getUuid(), new HashMap<>());

        List<ArtifactData> active = getActiveArtifacts(player);

        Map<String, ArtifactData> current = new HashMap<>();
        Map<String, Boolean> currentAwoken = new HashMap<>();
        Map<String, Integer> currentStages = new HashMap<>();

        for (ArtifactData data : active) {

            if (data.getSourceStack().isEmpty()) continue;

            String key = makeKey(player, data);
            current.put(key, data);

            if (!previous.containsKey(key)) {
                for (ArtifactTrait trait : data.getTraits()) {
                    trait.onEquip(player, data.getSourceStack(), data);
                }
            }

            boolean nowAwoken = data.isAwoken();
            boolean wasAwoken = previousAwoken.getOrDefault(key, false);

            currentAwoken.put(key, nowAwoken);

            if (nowAwoken && !wasAwoken) {
                for (ArtifactTrait trait : data.getTraits()) {
                    trait.onAwaken(player, data.getSourceStack(), data);
                }
            }

            int oldStage = previousStages.getOrDefault(key, data.getStage());
            int newStage = data.getStage();

            currentStages.put(key, newStage);

            if (oldStage != newStage) {
                for (ArtifactTrait trait : data.getTraits()) {
                    trait.onStageChanged(player, data.getSourceStack(), data, oldStage, newStage);
                }
            }
        }

        for (Map.Entry<String, ArtifactData> entry : previous.entrySet()) {

            String key = entry.getKey();

            if (!current.containsKey(key)) {

                ArtifactData data = entry.getValue();
                if (data == null) continue;

                for (ArtifactTrait trait : data.getTraits()) {
                    trait.onUnequip(player, data.getSourceStack(), data);
                }
            }

            lastAwoken.computeIfPresent(player.getUuid(), (uuid, map) -> {
                map.remove(key);
                return map;
            });
        }

        lastArtifacts.put(player.getUuid(), current);
        lastAwoken.put(player.getUuid(), currentAwoken);
        lastStages.put(player.getUuid(), currentStages);
    }

    public static boolean isArtifactActive(ServerPlayerEntity player, ArtifactData data) {
        return getActiveArtifacts(player).stream()
            .anyMatch(a -> a.getInstanceId().equals(data.getInstanceId()));
    }

    public static Collection<ArtifactData> getSnapshot(ServerPlayerEntity player) {
        Map<String, ArtifactData> map = lastArtifacts.get(player.getUuid());
        if (map == null) return List.of();
        return map.values();
    }

    public static List<ArtifactData> getActiveArtifacts(PlayerEntity player) {

        List<ArtifactData> artifacts = new ArrayList<>();

        addNonNull(player.getMainHandStack(), artifacts);
        addNonNull(player.getOffHandStack(), artifacts);

        addNonNull(player.getEquippedStack(EquipmentSlot.HEAD), artifacts);
        addNonNull(player.getEquippedStack(EquipmentSlot.CHEST), artifacts);
        addNonNull(player.getEquippedStack(EquipmentSlot.LEGS), artifacts);
        addNonNull(player.getEquippedStack(EquipmentSlot.FEET), artifacts);

        return artifacts;
    }

    private static void addNonNull(ItemStack stack, List<ArtifactData> artifacts) {

        if (stack.isEmpty()) return;

        ArtifactData data = ArtifactData.readFrom(stack);

        if (data == null) return;

        artifacts.add(data);
    }

    private static String makeKey(ServerPlayerEntity player, ArtifactData data) {

        ItemStack stack = data.getSourceStack();
        EquipmentSlot slot = player.getPreferredEquipmentSlot(stack);

        return player.getUuid() + ":" + slot.getName();
    }
}