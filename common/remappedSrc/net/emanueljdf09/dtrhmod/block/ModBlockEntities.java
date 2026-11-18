package net.emanueljdf09.dtrhmod.block;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.block.entity.*;
import net.emanueljdf09.dtrhmod.block.entity.spawn.CaterpillarSpawnerBlockEntity;
import net.emanueljdf09.dtrhmod.block.entity.spawn.CheshireCatSpawnerBlockEntity;
import net.emanueljdf09.dtrhmod.block.entity.spawn.MadHatterSpawnerBlockEntity;
import net.emanueljdf09.dtrhmod.block.entity.spawn.SingingFlowersSpawnerBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;


public class ModBlockEntities {
    public static final BlockEntityType<ExteriorDoorEntity> EXTERIOR_DOOR_ENTITY =
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "exterior_door_be"),
                    BlockEntityType.Builder.of(ExteriorDoorEntity::new,
                            ModBlocks.EXTERIOR_DOOR).build());

    public static final BlockEntityType<ExteriorChestEntity> EXTERIOR_CHEST_ENTITY =
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "exterior_chest_be"),
                    BlockEntityType.Builder.of(ExteriorChestEntity::new,
                            ModBlocks.EXTERIOR_CHEST).build());

    public static final BlockEntityType<MirrorBlockEntity> MIRROR_BLOCK_ENTITY =
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mirror_block_be"),
                    BlockEntityType.Builder.of(MirrorBlockEntity::new,
                            ModBlocks.MIRROR_BLOCK).build());

    public static final BlockEntityType<RabbitHoleBlockEntity> RABBIT_HOLE_BLOCK_ENTITY=
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "rabbit_hole_be"),
                    BlockEntityType.Builder.of(RabbitHoleBlockEntity::new,
                            ModBlocks.RABBIT_HOLE).build());

    public static final BlockEntityType<TeapotBlockEntity> TEAPOT_BLOCK_ENTITY =
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "teapot_block_be"),
                    BlockEntityType.Builder.of(TeapotBlockEntity::new,
                            ModBlocks.TEAPOT_BLOCK).build());

    public static final BlockEntityType<MadHatterHatBlockEntity> MAD_HATTER_HAT =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mad_hatter_hat_be"),
                    BlockEntityType.Builder.of(MadHatterHatBlockEntity::new, ModBlocks.MAD_HATTER_HAT).build());

    public static final BlockEntityType<ModHeadBlockEntity> MOD_HEAD_BE =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mod_head_be"),
                    BlockEntityType.Builder.of(ModHeadBlockEntity::new, ModBlocks.MAD_HATTER_HEAD, ModBlocks.MAD_HATTER_WALL_HEAD, ModBlocks.CATERPILLAR_HEAD, ModBlocks.CATERPILLAR_WALL_HEAD).build());



    public static final BlockEntityType<MadHatterSpawnerBlockEntity> MAD_HATTER_SPAWNER_BE =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mad_hatter_spawner_be"),
                    BlockEntityType.Builder.of(MadHatterSpawnerBlockEntity::new, ModBlocks.MAD_HATTER_SPAWNER_BLOCK).build());

    public static final BlockEntityType<CheshireCatSpawnerBlockEntity> CHESHIRE_CAT_SPAWNER_BE =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "cheshire_cat_spawner_be"),
                    BlockEntityType.Builder.of(CheshireCatSpawnerBlockEntity::new, ModBlocks.CHESHIRE_CAT_SPAWNER_BLOCK).build());

    public static final BlockEntityType<SingingFlowersSpawnerBlockEntity> SINGING_FLOWERS_SPAWNER_BE =
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "singing_flowers_spawner_be"),
                    BlockEntityType.Builder.of(SingingFlowersSpawnerBlockEntity::new, ModBlocks.SINGING_FLOWERS_SPAWNER_BLOCK).build());

    public static final BlockEntityType<CaterpillarSpawnerBlockEntity> CATERPILLAR_SPAWNER_BE =
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "caterpillar_spawner_be"),
                    BlockEntityType.Builder.of(CaterpillarSpawnerBlockEntity::new, ModBlocks.CATERPILLAR_SPAWNER_BLOCK).build());


    public static void registerBlockEnities() {
        DownTheRabbitHole.LOGGER.info("Registering Block Entities for" + DownTheRabbitHole.MOD_ID);
    }
}
