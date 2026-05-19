package com.penguin.kursd;

import com.penguin.kursd.curse.traits.AnemicTrait;
import com.penguin.kursd.curse.traits.BloodfedTrait;
import com.penguin.kursd.curse.traits.CinderLacedTrait;
import com.penguin.kursd.curse.traits.ColdOceanDwellerTrait;
import com.penguin.kursd.curse.traits.DustmindedTrait;
import com.penguin.kursd.curse.traits.EndermitoticTrait;
import com.penguin.kursd.curse.traits.GlassbonedTrait;
import com.penguin.kursd.curse.traits.GravidTrait;
import com.penguin.kursd.curse.traits.HemorrhagicTrait;
import com.penguin.kursd.curse.traits.HollowGraceTrait;
import com.penguin.kursd.curse.traits.OccludedTrait;
import com.penguin.kursd.curse.traits.OverstimulatedTrait;
import com.penguin.kursd.curse.traits.ParasiticTrait;
import com.penguin.kursd.curse.traits.RavagerTrait;
import com.penguin.kursd.curse.traits.SymbioticTrait;
import com.penguin.kursd.curse.traits.TidalFleshTrait;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.penguin.kursd.contexts.MovementContext;
import com.penguin.kursd.curse.ArtifactData;
import com.penguin.kursd.curse.ArtifactEvents;
import com.penguin.kursd.curse.ArtifactManager;
import com.penguin.kursd.curse.ArtifactTrait;
import com.penguin.kursd.curse.TraitRegistry;
import com.penguin.kursd.curse.traits.WhisperingTrait;
import com.penguin.kursd.curse.traits.WitheredMiasmaTrait;
import com.penguin.kursd.data.ArtifactDatapackLoader;
import com.penguin.kursd.effect.HollowedEffect;
import com.penguin.kursd.effect.OccludedEffect;
import com.penguin.kursd.loot.ArtifactGenerator;
import com.penguin.kursd.loot.ArtifactLootModifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import static net.minecraft.server.command.CommandManager.literal;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ksd implements ModInitializer {

    public static final boolean DEV_TOOLS = true;

    public static final String MOD_ID = "kursd";

    public static final Logger LOGGER =
        LoggerFactory.getLogger(MOD_ID);

    @SuppressWarnings("null")
    public static final RegistryEntry<StatusEffect> HOLLOWED =
        Registry.registerReference(
            Registries.STATUS_EFFECT,
            Identifier.of("kursd", "hollowed"),
            new HollowedEffect()
        );

    @SuppressWarnings("null")
    public static final RegistryEntry<StatusEffect> OCCLUDED =
        Registry.registerReference(
            Registries.STATUS_EFFECT,
            Identifier.of("kursd", "occluded"),
            new OccludedEffect()
        );


    @SuppressWarnings({ "deprecation", "null" })
    @Override
    public void onInitialize() {

        LOGGER.info("kʊɹs'd awakens.");

        ResourceManagerHelper.get(ResourceType.SERVER_DATA)
            .registerReloadListener(new ArtifactDatapackLoader());


        // register all traits here
        TraitRegistry.register(new AnemicTrait());
        TraitRegistry.register(new BloodfedTrait());
        TraitRegistry.register(new WhisperingTrait());
        TraitRegistry.register(new ParasiticTrait());
        TraitRegistry.register(new HemorrhagicTrait());
        TraitRegistry.register(new CinderLacedTrait());
        TraitRegistry.register(new HollowGraceTrait());
        TraitRegistry.register(new RavagerTrait());
        TraitRegistry.register(new DustmindedTrait());
        TraitRegistry.register(new GlassbonedTrait());
        TraitRegistry.register(new WitheredMiasmaTrait());
        TraitRegistry.register(new EndermitoticTrait());
        TraitRegistry.register(new ColdOceanDwellerTrait());
        TraitRegistry.register(new OverstimulatedTrait());
        TraitRegistry.register(new SymbioticTrait());
        TraitRegistry.register(new OccludedTrait());
        TraitRegistry.register(new TidalFleshTrait());
        TraitRegistry.register(new GravidTrait());

        ArtifactLootModifier.init();

        // command dispatchers
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("artifact_apply")
                .executes(ctx -> {
                    var player = ctx.getSource().getPlayerOrThrow();
                    var stack = player.getMainHandStack();

                    Ksd.LOGGER.info("BEFORE: {}", stack);

                    ArtifactData data = ArtifactGenerator.generate(player.getRandom());

                    ItemStack newStack = stack.copy();
                    data.writeTo(newStack);
                    player.setStackInHand(Hand.MAIN_HAND, newStack);

                    Ksd.LOGGER.info("AFTER: {}", newStack);
                    Ksd.LOGGER.info("CUSTOM_DATA: {}");
                    newStack.get(DataComponentTypes.CUSTOM_DATA);

                    newStack.set(DataComponentTypes.CUSTOM_NAME, Text.literal("§kbekʊɹs'd"));

                    player.getInventory().markDirty();
                    player.currentScreenHandler.sendContentUpdates();

                    return 1;
                })
            );

            dispatcher.register(CommandManager.literal("artifact")
                .requires(source -> source.isExecutedByPlayer())
                .then(CommandManager.argument("target", EntityArgumentType.player())
                    .then(CommandManager.argument("path", StringArgumentType.word())
                        .then(CommandManager.argument("config", NbtCompoundArgumentType.nbtCompound())
                            .executes(ctx -> {

                                ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "target");
                                String path = StringArgumentType.getString(ctx, "path");

                                @SuppressWarnings("null")
                                NbtCompound nbt = ctx.getArgument("config", NbtCompound.class);

                                Float defaultIntensity = Random.create().nextFloat();
                                Integer defaultStage = 0;
                                Boolean defaultAwakening = false;


                                Float intensity = nbt.contains("intensity") ? nbt.getFloat("intensity").orElse(defaultIntensity) : null;
                                Integer stage = nbt.contains("stage") ? nbt.getInt("stage").orElse(defaultStage) : null;
                                Boolean awakening = nbt.contains("awakening") ? nbt.getBoolean("awakening").orElse(defaultAwakening) : null;

                                List<ArtifactTrait> traits = null;

                                NbtElement raw = nbt.get("traits");

                                if (raw instanceof NbtList list) {
                                    traits = list.stream()
                                        .map(NbtElement::asString)
                                        .flatMap(Optional::stream)
                                        .map(TraitRegistry::get)
                                        .filter(Objects::nonNull)
                                        .toList();
                                }

                                Set<String> flags = null;
                                NbtElement raw2 = nbt.get("flags");

                                if (raw2 instanceof NbtList list) {
                                    flags = list.stream()
                                        .map(NbtElement::asString)
                                        .flatMap(Optional::stream)
                                        .filter(s -> !s.isEmpty())
                                        .collect(Collectors.toSet());
                                }

                                System.out.println("RAW TRAITS NBT: " + nbt.get("traits"));

                                ArtifactConfig config = new ArtifactConfig(
                                    intensity,
                                    stage,
                                    awakening,
                                    traits,
                                    flags
                                );

                                ItemStack stack = resolveItem(player, path);
                                ArtifactData data = ArtifactData.readFrom(stack);

                                if (data == null) return 0;

                                applyConfig(data, config);
                                data.writeTo(stack);

                                return 1;
                            })
                        )
                    )
                )
            );

            dispatcher.register(
                CommandManager.literal("awaken")
                    .requires(source -> source.isExecutedByPlayer())
                    .executes(ctx -> {

                        var player = ctx.getSource().getPlayer();

                        if (player == null) {
                            return 0;
                        }

                        ItemStack stack = player.getMainHandStack();

                        ArtifactData data = ArtifactData.readFrom(stack);

                        if (data == null) {

                            player.sendMessage(
                                Text.literal("No artifact in main hand."),
                                false
                            );

                            return 0;
                        }

                        data.Awaken();

                        data.writeTo(stack);

                        player.sendMessage(
                            Text.literal("Artifact awakened."),
                            false
                        );

                        return 1;
                    })
            );
        });

        // Trait Hooks
        ServerTickEvents.START_WORLD_TICK.register(world -> {
            // This is now handled at END of tick instead
        });
                
        ServerTickEvents.END_WORLD_TICK.register(world -> {

            for (ServerPlayerEntity player : world.getPlayers()) {

                ArtifactManager.update(player);

                var active = ArtifactManager.getSnapshot(player);

                for (ArtifactData data : active) {

                    ItemStack stack = data.getSourceStack();
                    if (stack.isEmpty()) continue;

                    ArtifactEvents.tick(player, stack, data);
                    data.writeTo(stack);
                }

                ArtifactManager.handleMovement(player);
                ArtifactManager.buildContexts(player);

                for (ArtifactData data : active) {
                    ItemStack stack = data.getSourceStack();
                    if (!stack.isEmpty()) {
                        data.writeTo(stack);
                    }
                }

                MovementContext mctx = ArtifactManager.getMovementContext(player);
                Vec3d vel = player.getVelocity();

                player.setVelocity(
                    vel.x * mctx.xzMultiplier,
                    vel.y + mctx.yVelocity,
                    vel.z * mctx.xzMultiplier
                );

                ArtifactManager.resetMovementContext(player);

                ArtifactManager.applyContexts(player);

                Vec3d current = new Vec3d(
                        player.getX(),
                        player.getY(),
                        player.getZ()
                );

                ArtifactManager.updateLastPosition(player, current);
            }
        });

        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity, damageSource) -> {

            if (!(entity instanceof ServerPlayerEntity sp)) return;

            for (ArtifactData data : ArtifactManager.getActiveArtifacts(sp)) {

                ItemStack stack = data.getSourceStack();

                ArtifactEvents.kill(sp, killedEntity, data);

                data.writeTo(stack);
            }
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {

            if (player instanceof ServerPlayerEntity serverPlayer) {

                for (ArtifactData data : ArtifactManager.getSnapshot(serverPlayer)) {
                    ArtifactEvents.attack(serverPlayer, (LivingEntity) entity, data);
                }
            }

            return ActionResult.PASS;
        });
        
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamageTaken, damageTaken, blocked) -> {

            if (!(entity instanceof ServerPlayerEntity sp)) return;

            for (ArtifactData data : ArtifactManager.getActiveArtifacts(sp)) {

                ItemStack stack = data.getSourceStack();

                ArtifactEvents.damaged(sp, source, damageTaken, data);

                data.writeTo(stack);
            }
        });
        
        // Block Break hook is a mixin
    }

    // used for /artifact command and nothing else
    public record ArtifactConfig(
        Float intensity,
        Integer stage,
        Boolean awakening,
        List<ArtifactTrait> traits,
        Set<String> flags
    ) {}

    private static void applyConfig(ArtifactData data, ArtifactConfig cfg) {

        if (cfg.intensity() != null) {
            data.setIntensity(cfg.intensity());
        } else {
            data.setIntensity(ThreadLocalRandom.current().nextFloat());
        }

        if (cfg.stage() != null) {
            data.setStage(cfg.stage());
        }

        if (cfg.awakening() != null) {
            if (cfg.awakening()) data.Awaken();
        } else {
            // default: unawakened (0)
        }

        if (cfg.traits() != null) {
            data.setTraits(cfg.traits());
        }

        if (cfg.flags() != null) {
            for (String flag : cfg.flags()) {
                data.addFlag(flag);
            }
        }
    }

    private static ItemStack resolveItem(ServerPlayerEntity player, String path) {

        return switch (path) {

            case "SelectedItem" -> player.getMainHandStack();

            case "Offhand" -> player.getOffHandStack();

            default -> player.getMainHandStack();
        };
    }
}