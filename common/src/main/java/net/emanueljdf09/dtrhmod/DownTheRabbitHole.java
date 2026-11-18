package net.emanueljdf09.dtrhmod;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.emanueljdf09.dtrhmod.block.ModBlockCollections;
import net.emanueljdf09.dtrhmod.block.ModBlockEntities;
import net.emanueljdf09.dtrhmod.block.ModBlocks;
import net.emanueljdf09.dtrhmod.block.entity.spawn.SingingFlowersSpawnerBlockEntity;
import net.emanueljdf09.dtrhmod.entity.ModEntities;
import net.emanueljdf09.dtrhmod.item.ModItems;
import net.emanueljdf09.dtrhmod.menu.ModScreenHandlers;
import net.emanueljdf09.dtrhmod.recipe.ModRecipes;
import net.emanueljdf09.dtrhmod.util.*;
import net.emanueljdf09.dtrhmod.util.components.PlayerProgressionManager;
import net.emanueljdf09.dtrhmod.util.components.ProgressionComponent;
import net.emanueljdf09.dtrhmod.util.components.ProgressionSync;
import net.emanueljdf09.dtrhmod.util.entity.LetterPuffs;
import net.emanueljdf09.dtrhmod.world.dimension.ModDimensions;
import net.emanueljdf09.dtrhmod.world.features.ModFeatures;
import net.emanueljdf09.dtrhmod.world.features.tree.deco.ModTreeDeco;
import net.emanueljdf09.dtrhmod.world.structures.ModStructurePieces;
import net.emanueljdf09.dtrhmod.world.structures.ModStructureProcessors;
import net.emanueljdf09.dtrhmod.world.structures.ModStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static net.emanueljdf09.dtrhmod.world.dimension.ModDimensions.EXTERIOR_LEVEL_KEY;
import static net.emanueljdf09.dtrhmod.world.dimension.ModDimensions.LAND_OF_FLOWERS_LEVEL_KEY;

public final class DownTheRabbitHole {
    public static final String MOD_ID = "dtrhmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("main", () ->
            CreativeTabRegistry.create(
                    Component.translatable("itemGroup." + MOD_ID),
                    () -> ModItems.POCKETWATCH.get().getDefaultInstance()
            )
    );

    public static void init() {
        CREATIVE_TABS.register();
        ModBlocks.registerModBlock();
        ModBlockCollections.registerModBlockCollections();
        ModItems.registerModItems();

        ModEffects.registerModEffects();
        ModEffects.registerBrewingRecipes();
        ModScreenHandlers.registerScreenHandlers();
        ModRecipes.registerRecipes();

        ModBlockEntities.registerBlockEntities();
        ModParticles.registerParticles();
        ModEntities.registerModEntities();
        ModFeatures.register();
        ModStructures.registerStructures();
        ModTreeDeco.register();
        ModStructureProcessors.register();
        ModStructurePieces.register();
        WonderlandProgressionUtil.register();
        ModCommands.register();
        LetterPuffs.register();

        ProgressionSync.register();

        LOGGER.info("Follow the tunnel...");

        TickEvent.SERVER_PRE.register(server -> {
            TeleportUtil.tickMirrorTrances(server);
            TeleportUtil.tickHatTrances(server);
            TeleportUtil.tickBlockTrances(server);
        });

        InteractionEvent.RIGHT_CLICK_BLOCK.register((player, hand, pos, face) -> {
            var world = player.level();
            BlockState state = world.getBlockState(pos);

            if (player.getItemInHand(hand).is(ItemTags.SHOVELS) && state.is(ModBlocks.WONDER_GRASS.get())) {
                if (!world.isClientSide()) {
                    world.setBlockAndUpdate(pos, ModBlocks.WONDER_DIRT_PATH.get().defaultBlockState());
                    world.playSound(null, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0f, 1.0f);
                    ItemStack stack = player.getItemInHand(hand);
                    stack.hurtAndBreak(
                            1,
                            player,
                            hand == InteractionHand.MAIN_HAND
                                    ? EquipmentSlot.MAINHAND
                                    : EquipmentSlot.OFFHAND
                    );
                }
                return EventResult.interruptTrue();
            }
            return EventResult.pass();
        });

        TickEvent.SERVER_LEVEL_POST.register(level -> {
            var dimensionKey = level.dimension();

            if (dimensionKey.equals(ModDimensions.STORYBOOK_LEVEL_KEY)) {
                for (var player : new ArrayList<>(level.players())) {
                    TeleportUtil.checkAndHandleVoidReturn(player);
                }
            }

            if (dimensionKey.equals(LAND_OF_FLOWERS_LEVEL_KEY)) {
                for (var player : new ArrayList<>(level.players())) {
                    TeleportUtil.checkAndHandleFlowerVoidReturn(player);
                    TeleportUtil.checkGrowthPotionExit(player);
                }
            }

            if (dimensionKey.equals(ModDimensions.WONDERLAND_LEVEL_KEY)) {
                for (var player : new ArrayList<>(level.players())) {
                    TeleportUtil.checkAndHandleWonderlandVoidReturn(player);
                }
            }
        });

        EntityEvent.ADD.register((entity, level) -> {
            if (entity instanceof ServerPlayer player && level instanceof ServerLevel serverLevel) {
                var dimensionKey = serverLevel.dimension();

                if (dimensionKey.equals(EXTERIOR_LEVEL_KEY)) {
                    BlockPos spawnPos = new BlockPos(0, 62, 0);

                    serverLevel.setDefaultSpawnPos(new BlockPos(3, 70, 2), 0.0f);

                    serverLevel.getStructureManager()
                            .get(ResourceLocation.fromNamespaceAndPath(MOD_ID, "exterior/exterior_room"))
                            .ifPresentOrElse(template -> {
                                StructurePlaceSettings data = new StructurePlaceSettings()
                                        .setIgnoreEntities(false)
                                        .setKnownShape(true);

                                template.placeInWorld(serverLevel, spawnPos, spawnPos, data,
                                        serverLevel.getRandom(), 3);

                                serverLevel.updateNeighborsAt(spawnPos, ModBlocks.EXTERIOR_DOOR.get());

                                LOGGER.info("[DTRH] Successfully placed exterior_room at 0, 62, 0");
                            }, () -> LOGGER.error(
                                    "[DTRH] ERROR: Could not find NBT file at data/dtrhmod/structures/exterior_room.nbt"
                            ));

                    ProgressionComponent component =
                            PlayerProgressionManager.get(serverLevel.getServer())
                                    .get(player.getUUID());

                    if (!component.hasDoneExterior()) {
                        ModPackets.sendOpenBookPacket(player, "wonderland_intro");
                    }
                }

                if (dimensionKey.equals(LAND_OF_FLOWERS_LEVEL_KEY)) {
                    BlockPos spawnPos = new BlockPos(0, 64, 0);

                    serverLevel.setDefaultSpawnPos(new BlockPos(24, 70, 45), 0.0f);

                    serverLevel.getStructureManager()
                            .get(ResourceLocation.fromNamespaceAndPath(MOD_ID, "land_of_flowers/arena"))
                            .ifPresentOrElse(template -> {
                                StructurePlaceSettings data = new StructurePlaceSettings()
                                        .setIgnoreEntities(false)
                                        .setKnownShape(true);

                                template.placeInWorld(serverLevel, spawnPos, spawnPos, data,
                                        serverLevel.getRandom(), 3);

                                BlockPos customBlockPos = new BlockPos(24, 64, 23);

                                serverLevel.setBlock(customBlockPos,
                                        ModBlocks.SINGING_FLOWERS_SPAWNER_BLOCK.get().defaultBlockState(), 3);

                                var blockEntity = serverLevel.getBlockEntity(customBlockPos);

                                if (blockEntity instanceof SingingFlowersSpawnerBlockEntity spawnerBE) {
                                    spawnerBE.forceReset(serverLevel);
                                }

                                LOGGER.info("[DTRH] Successfully placed arena structure and custom block at 24, 64, 23");
                            }, () -> LOGGER.error(
                                    "[DTRH] ERROR: Could not find NBT file at data/dtrhmod/structures/land_of_flowers/arena.nbt"
                            ));
                }
            }

            return EventResult.pass();
        });
    }
}