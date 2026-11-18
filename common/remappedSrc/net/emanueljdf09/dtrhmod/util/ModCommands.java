package net.emanueljdf09.dtrhmod.util;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.emanueljdf09.dtrhmod.fabric.entity.custom.MadHatterEntity;
import net.emanueljdf09.dtrhmod.fabric.util.components.ProgressionComponent;
import net.emanueljdf09.dtrhmod.fabric.util.config.ModConfigData;
import net.emanueljdf09.dtrhmod.fabric.util.entity.MadHatterTrivia;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import java.util.UUID;

public class ModCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("madhatter_answer")
                    .then(CommandManager.argument("entityUuid", StringArgumentType.string())
                            .then(CommandManager.argument("choiceIndex", IntegerArgumentType.integer())
                                    .executes(context -> {
                                        var player = context.getSource().getPlayerOrThrow();
                                        String uuidStr = StringArgumentType.getString(context, "entityUuid");
                                        int choiceIndex = IntegerArgumentType.getInteger(context, "choiceIndex");

                                        UUID hatterUuid = UUID.fromString(uuidStr);
                                        var entity = player.getWorld().getEntitiesByClass(
                                                MadHatterEntity.class,
                                                player.getBoundingBox().expand(15.0D),
                                                hatter -> hatter.getUuid().equals(hatterUuid)
                                        ).stream().findFirst().orElse(null);

                                        if (entity != null) {
                                            MadHatterTrivia.handleAnswerSelection(player, entity, choiceIndex);
                                        } else {
                                            player.sendMessage(Text.literal("The Mad Hatter is too far away!").formatted(net.minecraft.util.Formatting.RED), false);
                                        }
                                        return 1;
                                    })
                            )
                    )
            );
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("dtrh")
                    .requires(source -> source.hasPermissionLevel(0))

                    .then(CommandManager.literal("togglelocks")
                            .executes(context -> {
                                boolean newState = ModConfigData.toggleBiomeLocking();
                                String status = newState ? "§aENABLED" : "§cDISABLED";
                                context.getSource().sendFeedback(
                                        () -> Text.literal("§e[DTRH] Wonderland Biome Locking is now " + status),
                                        true
                                );
                                return 1;
                            })
                    )

                    .then(CommandManager.literal("stage")
                            .then(CommandManager.literal("set")
                                    .then(CommandManager.argument("target", EntityArgumentType.player())
                                            .then(CommandManager.argument("stage_number", IntegerArgumentType.integer(0, 10))
                                                    .executes(context -> {
                                                        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "target");
                                                        int targetStage = IntegerArgumentType.getInteger(context, "stage_number");

                                                        ProgressionComponent story = ModComponents.PROGRESSION_COMPONENT.get(player);
                                                        story.setCompletedStages(targetStage);

                                                        if (targetStage >= 1) {
                                                            story.setWonderlandMirrorUnlocked(true);
                                                        } else {
                                                            story.setWonderlandMirrorUnlocked(false);
                                                        }

                                                        ModComponents.PROGRESSION_COMPONENT.sync(player);

                                                        context.getSource().sendFeedback(() -> Text.literal(
                                                                "Successfully set " + player.getName().getString() + "'s  stage to: " + targetStage
                                                        ), true);

                                                        player.sendMessage(Text.literal("Progression stage set to: " + targetStage), false);
                                                        return 1;
                                                    })
                                            )
                                    )
                            )
                            .then(CommandManager.literal("query")
                                    .then(CommandManager.argument("target", EntityArgumentType.player())
                                            .executes(context -> {
                                                ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "target");
                                                ProgressionComponent story = ModComponents.PROGRESSION_COMPONENT.get(player);
                                                int currentStage = story.getCompletedStages();
                                                boolean mirrorOk = story.isWonderlandMirrorUnlocked();

                                                context.getSource().sendFeedback(() -> Text.literal(
                                                        player.getName().getString() + " is currently at Stage: " + currentStage + "(Mirror Unlocked: " + mirrorOk + ")"
                                                ), false);
                                                return 1;
                                            })
                                    )
                            )

                            .then(CommandManager.literal("reset")
                                    .then(CommandManager.argument("target", EntityArgumentType.player())
                                            .executes(context -> {
                                                ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "target");
                                                ProgressionComponent story = ModComponents.PROGRESSION_COMPONENT.get(player);

                                                story.reset();

                                                context.getSource().sendFeedback(() -> Text.literal(
                                                        "Successfully reset progression for " + player.getName().getString()
                                                ), true);

                                                player.sendMessage(Text.literal("Your progression has been reset."), false);
                                                return 1;
                                            })
                                    )
                            )
                    )
            );
        });
    }
}
