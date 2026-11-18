package net.emanueljdf09.dtrhmod.util;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import net.emanueljdf09.dtrhmod.entity.custom.MadHatterEntity;
import net.emanueljdf09.dtrhmod.util.components.PlayerProgressionManager;
import net.emanueljdf09.dtrhmod.util.components.ProgressionComponent;
import net.emanueljdf09.dtrhmod.util.config.ModConfigData;
import net.emanueljdf09.dtrhmod.util.entity.MadHatterTrivia;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class ModCommands {
    public static void register() {
        CommandRegistrationEvent.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("madhatter_answer")
                    .then(Commands.argument("entityUuid", StringArgumentType.string())
                            .then(Commands.argument("choiceIndex", IntegerArgumentType.integer())
                                    .executes(context -> {
                                        var player = context.getSource().getPlayerOrException();
                                        String uuidStr = StringArgumentType.getString(context, "entityUuid");
                                        int choiceIndex = IntegerArgumentType.getInteger(context, "choiceIndex");

                                        UUID hatterUuid = UUID.fromString(uuidStr);
                                        var entity = player.level().getEntitiesOfClass(
                                                MadHatterEntity.class,
                                                player.getBoundingBox().inflate(15.0D),
                                                hatter -> hatter.getUUID().equals(hatterUuid)
                                        ).stream().findFirst().orElse(null);

                                        if (entity != null) {
                                            MadHatterTrivia.handleAnswerSelection(player, entity, choiceIndex);
                                        } else {
                                            player.sendSystemMessage(Component.literal("The Mad Hatter is too far away!").withStyle(ChatFormatting.RED));
                                        }
                                        return 1;
                                    })
                            )
                    )
            );
        });

        CommandRegistrationEvent.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("dtrh")
                    .requires(source -> source.hasPermission(0))

                    .then(Commands.literal("togglelocks")
                            .executes(context -> {
                                boolean newState = ModConfigData.toggleBiomeLocking();
                                String status = newState ? "§aENABLED" : "§cDISABLED";
                                context.getSource().sendSuccess(
                                        () -> Component.literal("§e[DTRH] Wonderland Biome Locking is now " + status),
                                        true
                                );
                                return 1;
                            })
                    )

                    .then(Commands.literal("stage")
                            .then(Commands.literal("set")
                                    .then(Commands.argument("target", EntityArgument.player())
                                            .then(Commands.argument("stage_number", IntegerArgumentType.integer(0, 10))
                                                    .executes(context -> {
                                                        ServerPlayer player = EntityArgument.getPlayer(context, "target");
                                                        int targetStage = IntegerArgumentType.getInteger(context, "stage_number");
                                                        MinecraftServer server = player.getServer();
                                                        if (server == null) return 0;

                                                        ProgressionComponent story = PlayerProgressionManager.get(server).get(player.getUUID());
                                                        story.setCompletedStages(targetStage);

                                                        if (targetStage >= 1) {
                                                            story.setWonderlandMirrorUnlocked(true);
                                                        } else {
                                                            story.setWonderlandMirrorUnlocked(false);
                                                        }

                                                        context.getSource().sendSuccess(() -> Component.literal(
                                                                "Successfully set " + player.getName().getString() + "'s stage to: " + targetStage
                                                        ), true);

                                                        player.sendSystemMessage(Component.literal("Progression stage set to: " + targetStage));
                                                        return 1;
                                                    })
                                            )
                                    )
                            )
                            .then(Commands.literal("query")
                                    .then(Commands.argument("target", EntityArgument.player())
                                            .executes(context -> {
                                                ServerPlayer player = EntityArgument.getPlayer(context, "target");
                                                MinecraftServer server = player.getServer();
                                                if (server == null) return 0;

                                                ProgressionComponent story = PlayerProgressionManager.get(server).get(player.getUUID());
                                                int currentStage = story.getCompletedStages();
                                                boolean mirrorOk = story.isWonderlandMirrorUnlocked();

                                                context.getSource().sendSuccess(() -> Component.literal(
                                                        player.getName().getString() + " is currently at Stage: " + currentStage + " (Mirror Unlocked: " + mirrorOk + ")"
                                                ), false);
                                                return 1;
                                            })
                                    )
                            )

                            .then(Commands.literal("reset")
                                    .then(Commands.argument("target", EntityArgument.player())
                                            .executes(context -> {
                                                ServerPlayer player = EntityArgument.getPlayer(context, "target");
                                                MinecraftServer server = player.getServer();
                                                if (server == null) return 0;

                                                ProgressionComponent story = PlayerProgressionManager.get(server).get(player.getUUID());

                                                story.reset();

                                                context.getSource().sendSuccess(() -> Component.literal(
                                                        "Successfully reset progression for " + player.getName().getString()
                                                ), true);

                                                player.sendSystemMessage(Component.literal("Your progression has been reset."));
                                                return 1;
                                            })
                                    )
                            )
                    )
            );
        });
    }
}