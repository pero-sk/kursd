package com.penguin.kursd.curse;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import java.util.*;

/**
 * Persistent procedural artifact state.
 *
 * Stored directly on the ItemStack under CUSTOM_DATA.
 *
 * Traits themselves should remain stateless.
 */
public class ArtifactData {

    public static final String NBT_KEY = "kursd_artifact";

    private String name;
    private String instanceId;


    private float intensity;

    private int awakening;

    private int stage;

    // trait state markers, stats -> floats; memories -> ints; flags -> boolean-ish

    private final Map<String, Float> stats = new HashMap<>();

    private final Map<String, Integer> memories = new HashMap<>();

    private final Set<String> flags = new HashSet<>();

    // to get stack -> data AND data -> stack
    private transient ItemStack sourceStack;

    public ItemStack getSourceStack() {
        return sourceStack;
    }

    private final List<ArtifactTrait> traits = new ArrayList<>();

    public ArtifactData(String name, float intensity) {
        this.name = name;
        this.intensity = intensity;
        this.awakening = 0;
        this.stage = 0;

        this.instanceId = UUID.randomUUID().toString();
    }

    public String getName() {
        return name;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public static boolean canRename(ArtifactData data) {
        return data.isAwoken();
    }

    public boolean isAwoken() {
        return awakening == 1;
    }

    public void Awaken() {
        this.awakening = 1;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public float getStat(String key) {
        return stats.getOrDefault(key, 0f);
    }

    public void setStat(String key, float value) {
        stats.put(key, value);
    }

    public void addStat(String key, float amount) {
        setStat(key, getStat(key) + amount);
    }

    public Map<String, Float> getStats() {
        return stats;
    }

    public int getMemory(String key) {
        return memories.getOrDefault(key, 0);
    }

    public void setMemory(String key, int value) {
        memories.put(key, value);
    }

    public void addMemory(String key, int amount) {
        setMemory(key, getMemory(key) + amount);
    }

    public Map<String, Integer> getMemories() {
        return memories;
    }

    public boolean hasFlag(String flag) {
        return flags.contains(flag);
    }

    public void addFlag(String flag) {
        flags.add(flag);
    }

    public void removeFlag(String flag) {
        flags.remove(flag);
    }

    public Set<String> getFlags() {
        return flags;
    }

    public void addTrait(ArtifactTrait trait) {
        traits.add(trait);
    }

    public List<ArtifactTrait> getTraits() {
        return traits;
    }
    
    public void setTraits(List<ArtifactTrait> traits) {
        this.traits.clear();
        this.traits.addAll(traits);
    }

    public boolean hasTrait(String id) {
        return traits.stream().anyMatch(t -> t.id().equals(id));
    }

    public void writeTo(ItemStack stack) {

        NbtCompound tag = new NbtCompound();

        tag.putString("name", name);
        tag.putFloat("intensity", intensity);
        tag.putInt("awakening", awakening);
        tag.putInt("stage", stage);

        tag.putString("instance_id", instanceId);

        NbtCompound statsTag = new NbtCompound();
        for (Map.Entry<String, Float> entry : stats.entrySet()) {
            Float value = entry.getValue();
            if (value != null) {
                statsTag.putFloat(entry.getKey(), value);
            }
        }
        tag.put("stats", statsTag);

        NbtCompound memTag = new NbtCompound();
        for (Map.Entry<String, Integer> entry : memories.entrySet()) {
            Integer value = entry.getValue();
            if (value != null) {
                memTag.putInt(entry.getKey(), value);
            }
        }
        tag.put("memories", memTag);

        NbtList flagsList = new NbtList();
        for (String f : flags) {
            flagsList.add(NbtString.of(f));
        }
        tag.put("flags", flagsList);

        NbtList traitList = new NbtList();
        for (ArtifactTrait t : traits) {
            traitList.add(NbtString.of(t.id()));
        }
        tag.put("traits", traitList);

        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));
    }

    public static ArtifactData readFrom(ItemStack stack) {

        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null) return null;

        NbtCompound tag = comp.copyNbt();

        if (tag == null || tag.isEmpty()) return null;

        ArtifactData data = new ArtifactData(
            tag.getString("name").orElse("Unknown Artifact"),
            tag.getFloat("intensity").orElse(0f)
        );

        data.sourceStack = stack;

        data.awakening = tag.getInt("awakening").orElse(0);
        data.stage = tag.getInt("stage").orElse(0);

        data.instanceId = tag.getString("instance_id").orElse(UUID.randomUUID().toString());

        NbtCompound statsTag = tag.getCompound("stats").orElse(new NbtCompound());

        for (String key : statsTag.getKeys()) {
            data.stats.put(key, statsTag.getFloat(key).orElse(0f));
        }

        NbtCompound memTag = tag.getCompound("memories").orElse(new NbtCompound());

        for (String key : memTag.getKeys()) {
            data.memories.put(key, memTag.getInt(key).orElse(0));
        }

        NbtList flagsList = tag.getList("flags").orElse(new NbtList());

        for (int i = 0; i < flagsList.size(); i++) {
            String flag = flagsList.getString(i).orElse("");
            if (!flag.isEmpty()) {
                data.flags.add(flag);
            }
        }

        NbtList traitList = tag.getList("traits").orElse(new NbtList());

        for (int i = 0; i < traitList.size(); i++) {
            String id = traitList.getString(i).orElse("");
            if (id.isEmpty()) continue;

            ArtifactTrait trait = TraitRegistry.get(id);
            if (trait != null) {
                data.traits.add(trait);
            }
        }

        return data;
    }

    public static boolean isArtifact(ItemStack stack) {

        if (!stack.contains(DataComponentTypes.CUSTOM_DATA)) {
            return false;
        }

        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (customData == null) {
            return false;
        }

        return customData.copyNbt().contains(NBT_KEY);
    }
}