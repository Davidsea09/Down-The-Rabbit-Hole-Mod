package net.emanueljdf09.dtrhmod.entity;

import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.fabric.entity.custom.*;
import net.emanueljdf09.dtrhmod.fabric.entity.projectile.PlantFangEntity;
import net.emanueljdf09.dtrhmod.fabric.entity.projectile.SkySeedProjectile;
import net.emanueljdf09.dtrhmod.fabric.entity.projectile.TeapotProjectile;
import net.emanueljdf09.dtrhmod.fabric.entity.projectile.VineLassoProjectileEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {

    public static final EntityType<TeapotProjectile> TEAPOT_PROJECTILE = register("teapot_projectile",
            EntityType.Builder.<TeapotProjectile>of(TeapotProjectile::new, MobCategory.MISC)
                    .dimensions(0.5f, 0.5f)
                    .maxTrackingRange(64)
                    .trackingTickInterval(10)
    );

    public static final EntityType<WhiteRabbitEntity> WHITE_RABBIT = register("white_rabbit",
            EntityType.Builder.of(WhiteRabbitEntity::new, MobCategory.CREATURE)
                    .dimensions(1.0f, 1.25f)
                    .maxTrackingRange(64)
                    .trackingTickInterval(3)
    );

    public static final EntityType<CheshireCatEntity> CHESHIRE_CAT = register("cheshire_cat",
            EntityType.Builder.of(CheshireCatEntity::new, MobCategory.CREATURE)
                    .dimensions(1.0f, 1.0f)
                    .maxTrackingRange(64)
                    .trackingTickInterval(3)
    );

    public static final EntityType<WeepingPlayerEntity> WEEPING_PLAYER = register("weeping_player",
            EntityType.Builder.of(WeepingPlayerEntity::new, MobCategory.MONSTER)
                    .dimensions(0.8f, 2.25f)
                    .maxTrackingRange(64)
                    .trackingTickInterval(3)
    );

    public static final EntityType<MadHatterEntity> MAD_HATTER = register("mad_hatter",
            EntityType.Builder.of(MadHatterEntity::new, MobCategory.MONSTER)
                    .dimensions(0.8f, 2.25f)
                    .maxTrackingRange(64)
                    .trackingTickInterval(3)
    );

    public static final EntityType<SingingFlowerEntity> SINGING_FLOWER = register("singing_flower",
            EntityType.Builder.of(SingingFlowerEntity::new, MobCategory.MONSTER)
                    .dimensions(0.8f, 2.25f)
                    .maxTrackingRange(64)
                    .trackingTickInterval(3)
    );

    public static final EntityType<MockTurtleEntity> MOCK_TURTLE = register("mock_turtle",
            EntityType.Builder.of(MockTurtleEntity::new, MobCategory.CREATURE)
                    .dimensions(0.8f, 2.25f)
                    .maxTrackingRange(64)
                    .trackingTickInterval(3)
    );

    public static final EntityType<CaterpillarEntity> CATERPILLAR = register("caterpillar",
            EntityType.Builder.of(CaterpillarEntity::new, MobCategory.MONSTER)
                    .dimensions(2.2f, 1.6f)
                    .maxTrackingRange(80)
                    .trackingTickInterval(1)
    );

    public static final EntityType<VineAnchorEntity> VINE_ANCHOR = register("vine_anchor",
            EntityType.Builder.of(VineAnchorEntity::new, MobCategory.MISC)
                    .dimensions(0.9f, 1.2f)
                    .maxTrackingRange(64)
                    .trackingTickInterval(2)
    );

    public static final EntityType<SkySeedProjectile> SKY_SEED = register("sky_seed",
            EntityType.Builder.<SkySeedProjectile>of(SkySeedProjectile::new, MobCategory.MISC)
                    .dimensions(0.5f, 0.5f)
                    .maxTrackingRange(64)
                    .trackingTickInterval(10)
    );

    public static final EntityType<VineLassoProjectileEntity> VINE_LASSO_PROJECTILE = register("vine_lasso_projectile",
            EntityType.Builder.<VineLassoProjectileEntity>of(VineLassoProjectileEntity::new, MobCategory.MISC)
                    .dimensions(0.5f, 0.5f)
                    .maxTrackingRange(64)
                    .trackingTickInterval(10)
    );

    public static final EntityType<PlantFangEntity> PLANT_FANGS = register("plant_fangs",
            EntityType.Builder.<PlantFangEntity>of(PlantFangEntity::new, MobCategory.MISC)
                    .dimensions(0.5f, 0.5f)
                    .maxTrackingRange(64)
                    .trackingTickInterval(10)
    );

    public static final EntityType<HareEntity> HARE = register("hare",
            EntityType.Builder.<HareEntity>of(HareEntity::new, MobCategory.MISC)
                    .dimensions(0.5f, 0.5f)
                    .maxTrackingRange(64)
                    .trackingTickInterval(10)
    );

    private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
        return Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, name),
                builder.build(name)
        );
    }

    public static void registerModEntities() {
        DownTheRabbitHole.LOGGER.info("Registering ModEntities for " + DownTheRabbitHole.MOD_ID);
        FabricDefaultAttributeRegistry.register(WHITE_RABBIT, WhiteRabbitEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(WEEPING_PLAYER, WeepingPlayerEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(MAD_HATTER, MadHatterEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(CHESHIRE_CAT, CheshireCatEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(SINGING_FLOWER, SingingFlowerEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(MOCK_TURTLE, MockTurtleEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(CATERPILLAR, CaterpillarEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(HARE, HareEntity.setAttributes());
    }
}