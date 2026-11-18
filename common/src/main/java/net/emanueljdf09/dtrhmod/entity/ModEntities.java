package net.emanueljdf09.dtrhmod.entity;

import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.entity.custom.*;
import net.emanueljdf09.dtrhmod.entity.projectile.PlantFangEntity;
import net.emanueljdf09.dtrhmod.entity.projectile.SkySeedProjectile;
import net.emanueljdf09.dtrhmod.entity.projectile.TeapotProjectile;
import net.emanueljdf09.dtrhmod.entity.projectile.VineLassoProjectileEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(DownTheRabbitHole.MOD_ID, Registries.ENTITY_TYPE);

    /*public static final RegistrySupplier<EntityType<Boat>> TH_BOAT = register("th_boat",
            () -> EntityType.Builder.<Boat>of((type, level) -> new Boat(type, level), MobCategory.MISC)
                    .sized(1.375f, 0.5625f)
                    .clientTrackingRange(10)
                    .updateInterval(3)
    );
    public static final RegistrySupplier<EntityType<ChestBoat>> TH_CHEST_BOAT = register("th_chest_boat",
            () -> EntityType.Builder.<ChestBoat>of((type, level) -> new ChestBoat(type, level), MobCategory.MISC)
                    .sized(1.375f, 0.5625f)
                    .clientTrackingRange(10)
                    .updateInterval(3)
    );


    public static final RegistrySupplier<EntityType<Boat>> BB_BOAT = register("bb_boat",
            () -> EntityType.Builder.<Boat>of((type, level) -> new Boat(type, level), MobCategory.MISC)
                    .sized(1.375f, 0.5625f)
                    .clientTrackingRange(10)
                    .updateInterval(3)
    );
    public static final RegistrySupplier<EntityType<ChestBoat>> BB_CHEST_BOAT = register("bb_chest_boat",
            () -> EntityType.Builder.<ChestBoat>of((type, level) -> new ChestBoat(type, level), MobCategory.MISC)
                    .sized(1.375f, 0.5625f)
                    .clientTrackingRange(10)
                    .updateInterval(3)
    );

    public static final RegistrySupplier<EntityType<Boat>> WW_BOAT = register("ww_boat",
            () -> EntityType.Builder.<Boat>of((type, level) -> new Boat(type, level), MobCategory.MISC)
                    .sized(1.375f, 0.5625f)
                    .clientTrackingRange(10)
                    .updateInterval(3)
    );
    public static final RegistrySupplier<EntityType<ChestBoat>> WW_CHEST_BOAT = register("ww_chest_boat",
            () -> EntityType.Builder.<ChestBoat>of((type, level) -> new ChestBoat(type, level), MobCategory.MISC)
                    .sized(1.375f, 0.5625f)
                    .clientTrackingRange(10)
                    .updateInterval(3)
    );

    public static final RegistrySupplier<EntityType<Boat>> HH_BOAT = register("hh_boat",
            () -> EntityType.Builder.<Boat>of((type, level) -> new Boat(type, level), MobCategory.MISC)
                    .sized(1.375f, 0.5625f)
                    .clientTrackingRange(10)
                    .updateInterval(3)
    );
    public static final RegistrySupplier<EntityType<ChestBoat>> HH_CHEST_BOAT = register("hh_chest_boat",
            () -> EntityType.Builder.<ChestBoat>of((type, level) -> new ChestBoat(type, level), MobCategory.MISC)
                    .sized(1.375f, 0.5625f)
                    .clientTrackingRange(10)
                    .updateInterval(3)
    ); */

    public static final RegistrySupplier<EntityType<TeapotProjectile>> TEAPOT_PROJECTILE = register("teapot_projectile",
            () -> EntityType.Builder.<TeapotProjectile>of(TeapotProjectile::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(64)
                    .updateInterval(10)
    );

    public static final RegistrySupplier<EntityType<WhiteRabbitEntity>> WHITE_RABBIT = register("white_rabbit",
            () -> EntityType.Builder.of(WhiteRabbitEntity::new, MobCategory.CREATURE)
                    .sized(1.0f, 1.25f)
                    .clientTrackingRange(64)
                    .updateInterval(3)
    );

    public static final RegistrySupplier<EntityType<CheshireCatEntity>> CHESHIRE_CAT = register("cheshire_cat",
            () -> EntityType.Builder.of(CheshireCatEntity::new, MobCategory.CREATURE)
                    .sized(1.0f, 1.0f)
                    .clientTrackingRange(64)
                    .updateInterval(3)
    );

    public static final RegistrySupplier<EntityType<WeepingPlayerEntity>> WEEPING_PLAYER = register("weeping_player",
            () -> EntityType.Builder.of(WeepingPlayerEntity::new, MobCategory.MONSTER)
                    .sized(0.8f, 2.25f)
                    .clientTrackingRange(64)
                    .updateInterval(3)
    );

    public static final RegistrySupplier<EntityType<MadHatterEntity>> MAD_HATTER = register("mad_hatter",
            () -> EntityType.Builder.of(MadHatterEntity::new, MobCategory.MONSTER)
                    .sized(0.8f, 2.25f)
                    .clientTrackingRange(64)
                    .updateInterval(3)
    );

    public static final RegistrySupplier<EntityType<SingingFlowerEntity>> SINGING_FLOWER = register("singing_flower",
            () -> EntityType.Builder.of(SingingFlowerEntity::new, MobCategory.MONSTER)
                    .sized(0.8f, 2.25f)
                    .clientTrackingRange(64)
                    .updateInterval(3)
    );

    public static final RegistrySupplier<EntityType<MockTurtleEntity>> MOCK_TURTLE = register("mock_turtle",
            () -> EntityType.Builder.of(MockTurtleEntity::new, MobCategory.CREATURE)
                    .sized(0.8f, 2.25f)
                    .clientTrackingRange(64)
                    .updateInterval(3)
    );

    public static final RegistrySupplier<EntityType<CaterpillarEntity>> CATERPILLAR = register("caterpillar",
            () -> EntityType.Builder.of(CaterpillarEntity::new, MobCategory.MONSTER)
                    .sized(2.2f, 1.6f)
                    .clientTrackingRange(80)
                    .updateInterval(1)
    );

    public static final RegistrySupplier<EntityType<VineAnchorEntity>> VINE_ANCHOR = register("vine_anchor",
            () -> EntityType.Builder.of(VineAnchorEntity::new, MobCategory.MISC)
                    .sized(0.9f, 1.2f)
                    .clientTrackingRange(64)
                    .updateInterval(2)
    );

    public static final RegistrySupplier<EntityType<SkySeedProjectile>> SKY_SEED = register("sky_seed",
            () -> EntityType.Builder.<SkySeedProjectile>of(SkySeedProjectile::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(64)
                    .updateInterval(10)
    );

    public static final RegistrySupplier<EntityType<VineLassoProjectileEntity>> VINE_LASSO_PROJECTILE = register("vine_lasso_projectile",
            () -> EntityType.Builder.<VineLassoProjectileEntity>of(VineLassoProjectileEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(64)
                    .updateInterval(10)
    );

    public static final RegistrySupplier<EntityType<PlantFangEntity>> PLANT_FANGS = register("plant_fangs",
            () -> EntityType.Builder.<PlantFangEntity>of(PlantFangEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(64)
                    .updateInterval(10)
    );

    public static final RegistrySupplier<EntityType<HareEntity>> HARE = register("hare",
            () -> EntityType.Builder.<HareEntity>of(HareEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(64)
                    .updateInterval(10)
    );

    private static <T extends Entity> RegistrySupplier<EntityType<T>> register(String name, Supplier<EntityType.Builder<T>> factory) {
        return ENTITIES.register(name, () -> factory.get().build(name));
    }

   /* private static void registerSpawns() {
        var spawningConfig = CACCommonConfig.HANDLER.instance().spawning;

        for (var entityEntry : spawningConfig.getAllEntries().entrySet()) {
            var entityPath = entityEntry.getKey();
            var type = CACSpawnConfig.resolveEntityType(entityPath);
            if (type == null) {
                LOGGER.warn("Unknown entity '{}' in spawn config, skipping", entityPath);
                continue;
            }

            for (var spawn : entityEntry.getValue()) {
                if (spawn.weight() <= 0) {
                    continue;
                }

                var spawnerData = new MobSpawnSettings.SpawnerData(type, spawn.weight(), spawn.min(), spawn.max());

                if (spawn.isTag()) {
                    if (spawn.biomeLocation().equals(ResourceLocation.fromNamespaceAndPath("c", "is_lush"))) {
                        LushCaveSpawnHandler.addSpawnerData(spawnerData);
                        continue;
                    }

                    var tag = TagKey.create(Registries.BIOME, spawn.biomeLocation());
                    BiomeModifications.addProperties(context -> context.hasTag(tag), (context, properties) -> properties.getSpawnProperties().addSpawn(type.getCategory(), spawnerData));
                } else {
                    if (spawn.biomeLocation().equals(Biomes.LUSH_CAVES.location())) {
                        LushCaveSpawnHandler.addSpawnerData(spawnerData);
                        continue;
                    }

                    BiomeModifications.addProperties(context -> context.getKey().isPresent() && context.getKey().get().equals(spawn.biomeLocation()), (context, properties) -> properties.getSpawnProperties().addSpawn(type.getCategory(), spawnerData));
                }
            }
        }
    } */

   /* private static void registerSpawnPlacements() {
        SpawnPlacementsRegistry.register(CACEntities.OTTER, SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, OtterEntity::checkOtterSpawnRules);
        SpawnPlacementsRegistry.register(CACEntities.KOI_FISH, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules);
        SpawnPlacementsRegistry.register(CACEntities.DRAGONFLY, SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, DragonflyEntity::checkDragonflySpawnRules);
        SpawnPlacementsRegistry.register(CACEntities.SEA_BUNNY, SpawnPlacementTypes.IN_WATER, Heightmap.Types.OCEAN_FLOOR, SeaBunnyEntity::checkSeaBunnySpawnRules);
        SpawnPlacementsRegistry.register(CACEntities.FERRET, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacementsRegistry.register(CACEntities.DUMBO_OCTOPUS, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules);
        SpawnPlacementsRegistry.register(CACEntities.LEAF_INSECT, SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, LeafInsectEntity::checkLeafInsectSpawnRules);
        SpawnPlacementsRegistry.register(CACEntities.RED_PANDA, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacementsRegistry.register(CACEntities.SHIMA_ENAGA, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);
        SpawnPlacementsRegistry.register(CACEntities.JUMPING_SPIDER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (e, l, s, p, r) -> true);
        SpawnPlacementsRegistry.register(CACEntities.LADYBUG, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CACEntities::checkBugSpawnRules);
        SpawnPlacementsRegistry.register(CACEntities.STAG_BEETLE, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CACEntities::checkBugSpawnRules);
        SpawnPlacementsRegistry.register(CACEntities.ROLY_POLY, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CACEntities::checkBugSpawnRules);
        SpawnPlacementsRegistry.register(CACEntities.SNAIL, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CACEntities::checkBugSpawnRules);
        SpawnPlacementsRegistry.register(CACEntities.STICK_BUG, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CACEntities::checkBugSpawnRules);
        SpawnPlacementsRegistry.register(CACEntities.WEEVIL, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CACEntities::checkBugSpawnRules);
    } */


    private static void registerAttributes() {
        EntityAttributeRegistry.register(WHITE_RABBIT, WhiteRabbitEntity::setAttributes);
        EntityAttributeRegistry.register(WEEPING_PLAYER, WeepingPlayerEntity::setAttributes);
        EntityAttributeRegistry.register(MAD_HATTER, MadHatterEntity::setAttributes);
        EntityAttributeRegistry.register(CHESHIRE_CAT, CheshireCatEntity::setAttributes);
        EntityAttributeRegistry.register(SINGING_FLOWER, SingingFlowerEntity::setAttributes);
        EntityAttributeRegistry.register(MOCK_TURTLE, MockTurtleEntity::setAttributes);
        EntityAttributeRegistry.register(CATERPILLAR, CaterpillarEntity::setAttributes);
        EntityAttributeRegistry.register(HARE, HareEntity::setAttributes);
    }

    public static void registerModEntities() {
        ENTITIES.register();
        registerAttributes();
        DownTheRabbitHole.LOGGER.info("Registering ModEntities for " + DownTheRabbitHole.MOD_ID);

    }
}