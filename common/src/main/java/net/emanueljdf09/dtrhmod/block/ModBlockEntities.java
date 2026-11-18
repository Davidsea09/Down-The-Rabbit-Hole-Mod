package net.emanueljdf09.dtrhmod.block;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.block.entity.*;
import net.emanueljdf09.dtrhmod.block.entity.spawn.CaterpillarSpawnerBlockEntity;
import net.emanueljdf09.dtrhmod.block.entity.spawn.CheshireCatSpawnerBlockEntity;
import net.emanueljdf09.dtrhmod.block.entity.spawn.MadHatterSpawnerBlockEntity;
import net.emanueljdf09.dtrhmod.block.entity.spawn.SingingFlowersSpawnerBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(DownTheRabbitHole.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<ExteriorDoorEntity>> EXTERIOR_DOOR_ENTITY =
            BLOCK_ENTITIES.register("exterior_door_be", () ->
                    BlockEntityType.Builder.of(ExteriorDoorEntity::new,
                            ModBlocks.EXTERIOR_DOOR.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<ExteriorChestEntity>> EXTERIOR_CHEST_ENTITY =
            BLOCK_ENTITIES.register("exterior_chest_be", () ->
                    BlockEntityType.Builder.of(ExteriorChestEntity::new,
                            ModBlocks.EXTERIOR_CHEST.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<MirrorBlockEntity>> MIRROR_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("mirror_block_be", () ->
                    BlockEntityType.Builder.of(MirrorBlockEntity::new,
                            ModBlocks.MIRROR_BLOCK.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<RabbitHoleBlockEntity>> RABBIT_HOLE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("rabbit_hole_be", () ->
                    BlockEntityType.Builder.of(RabbitHoleBlockEntity::new,
                            ModBlocks.RABBIT_HOLE.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<TeapotBlockEntity>> TEAPOT_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("teapot_block_be", () ->
                    BlockEntityType.Builder.of(TeapotBlockEntity::new,
                            ModBlocks.TEAPOT_BLOCK.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<MadHatterHatBlockEntity>> MAD_HATTER_HAT =
            BLOCK_ENTITIES.register("mad_hatter_hat_be", () ->
                    BlockEntityType.Builder.of(MadHatterHatBlockEntity::new,
                            ModBlocks.MAD_HATTER_HAT.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<ModHeadBlockEntity>> MOD_HEAD_BE =
            BLOCK_ENTITIES.register("mod_head_be", () ->
                    BlockEntityType.Builder.of(ModHeadBlockEntity::new,
                            ModBlocks.MAD_HATTER_HEAD.get(),
                            ModBlocks.MAD_HATTER_WALL_HEAD.get(),
                            ModBlocks.CATERPILLAR_HEAD.get(),
                            ModBlocks.CATERPILLAR_WALL_HEAD.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<MadHatterSpawnerBlockEntity>> MAD_HATTER_SPAWNER_BE =
            BLOCK_ENTITIES.register("mad_hatter_spawner_be", () ->
                    BlockEntityType.Builder.of(MadHatterSpawnerBlockEntity::new,
                            ModBlocks.MAD_HATTER_SPAWNER_BLOCK.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<CheshireCatSpawnerBlockEntity>> CHESHIRE_CAT_SPAWNER_BE =
            BLOCK_ENTITIES.register("cheshire_cat_spawner_be", () ->
                    BlockEntityType.Builder.of(CheshireCatSpawnerBlockEntity::new,
                            ModBlocks.CHESHIRE_CAT_SPAWNER_BLOCK.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<SingingFlowersSpawnerBlockEntity>> SINGING_FLOWERS_SPAWNER_BE =
            BLOCK_ENTITIES.register("singing_flowers_spawner_be", () ->
                    BlockEntityType.Builder.of(SingingFlowersSpawnerBlockEntity::new,
                            ModBlocks.SINGING_FLOWERS_SPAWNER_BLOCK.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<CaterpillarSpawnerBlockEntity>> CATERPILLAR_SPAWNER_BE =
            BLOCK_ENTITIES.register("caterpillar_spawner_be", () ->
                    BlockEntityType.Builder.of(CaterpillarSpawnerBlockEntity::new,
                            ModBlocks.CATERPILLAR_SPAWNER_BLOCK.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<ModSignBlockEntity>> SIGN_BLOCK_ENTITIES =
            BLOCK_ENTITIES.register("sign_be", () ->
                    BlockEntityType.Builder.of(ModSignBlockEntity::new,
                            ModBlocks.TH_SIGN.get(), ModBlocks.WALL_TH_SIGN.get(),
                            ModBlocks.WW_SIGN.get(), ModBlocks.WALL_WW_SIGN.get(),
                            ModBlocks.BB_SIGN.get(), ModBlocks.WALL_BB_SIGN.get(),
                            ModBlocks.HH_SIGN.get(), ModBlocks.WALL_HH_SIGN.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<ModHangingSignBlockEntity>> HANGING_SIGN_BLOCK_ENTITIES =
            BLOCK_ENTITIES.register("hanging_sign_be", () ->
                    BlockEntityType.Builder.of(ModHangingSignBlockEntity::new,
                            ModBlocks.TH_HANGING_SIGN.get(), ModBlocks.WALL_TH_HANGING_SIGN.get(),
                            ModBlocks.WW_HANGING_SIGN.get(), ModBlocks.WALL_WW_HANGING_SIGN.get(),
                            ModBlocks.BB_HANGING_SIGN.get(), ModBlocks.WALL_BB_HANGING_SIGN.get(),
                            ModBlocks.HH_HANGING_SIGN.get(), ModBlocks.WALL_HH_HANGING_SIGN.get()).build(null));

    public static void registerBlockEntities() {
        BLOCK_ENTITIES.register();
        DownTheRabbitHole.LOGGER.info("Registering Block Entities for " + DownTheRabbitHole.MOD_ID);
    }
}