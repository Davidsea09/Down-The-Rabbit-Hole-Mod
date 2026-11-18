package net.emanueljdf09.dtrhmod.world.dimension;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import java.util.OptionalLong;

public class ModDimensions {
    public static final ResourceKey<LevelStem> WONDERLAND_KEY = ResourceKey.create(Registries.LEVEL_STEM,
            ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "wonderland"));
    public static final ResourceKey<Level> WONDERLAND_LEVEL_KEY = ResourceKey.create(Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "wonderland"));
    public static final ResourceKey<DimensionType> WONDERLAND_DIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE,
            ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "wonderland_type"));


    public static final ResourceKey<LevelStem> EXTERIOR_KEY = ResourceKey.create(Registries.LEVEL_STEM,
            ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "exterior"));
    public static final ResourceKey<Level> EXTERIOR_LEVEL_KEY = ResourceKey.create(Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "exterior"));
    public static final ResourceKey<DimensionType> EXTERIOR_DIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE,
            ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "exterior_type"));

    public static final ResourceKey<LevelStem> LAND_OF_FLOWERS_KEY = ResourceKey.create(Registries.LEVEL_STEM,
            ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "land_of_flowers_dim"));
    public static final ResourceKey<Level> LAND_OF_FLOWERS_LEVEL_KEY = ResourceKey.create(Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "land_of_flowers_dim"));
    public static final ResourceKey<DimensionType> LAND_OF_FLOWERS_DIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE,
            ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "land_of_flowers_dim_type"));

    public static final ResourceKey<LevelStem> STORYBOOK_KEY = ResourceKey.create(Registries.LEVEL_STEM,
            ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "storybook"));
    public static final ResourceKey<Level> STORYBOOK_LEVEL_KEY = ResourceKey.create(Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "storybook"));
    public static final ResourceKey<DimensionType> STORYBOOK_DIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE,
            ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "storybook_type"));

    public static void bootstrapType(BootstrapContext<DimensionType> context) {
        context.register(WONDERLAND_DIM_TYPE, new DimensionType(
                OptionalLong.of(12900),
                true,
                false,
                false,
                true,
                1.0,
                true,
                true,
                0,
                256,
                256,
                BlockTags.INFINIBURN_OVERWORLD,
                BuiltinDimensionTypes.OVERWORLD_EFFECTS,
                0.3f,
                new DimensionType.MonsterSettings(true, false, UniformInt.of(0, 7), 7)));

        context.register(EXTERIOR_DIM_TYPE, new DimensionType(
                OptionalLong.of(6000),
                false,
                false,
                false,
                false,
                1.0,
                false,
                false,
                0,
                256,
                256,
                BlockTags.INFINIBURN_OVERWORLD,
                BuiltinDimensionTypes.END_EFFECTS,
                0.3f,
                new DimensionType.MonsterSettings(true, false, UniformInt.of(0, 0), 0)));

        context.register(LAND_OF_FLOWERS_DIM_TYPE, new DimensionType(
                OptionalLong.of(6000),
                true,
                false,
                false,
                false,
                1.0,
                false,
                false,
                0,
                256,
                256,
                BlockTags.INFINIBURN_OVERWORLD,
                BuiltinDimensionTypes.OVERWORLD_EFFECTS,
                0.3f,
                new DimensionType.MonsterSettings(true, false, UniformInt.of(0, 0), 0)));


        context.register(STORYBOOK_DIM_TYPE, new DimensionType(
                OptionalLong.of(6000),
                true,
                false, //hasCeiling
                false, //ultraWarm
                false, //natural
                1.0, //coordinateScale
                false, //bedWorks
                false, //respawnAnchorWorks
                0, //minY
                256, //MaxHeight
                256, //logicalHeight
                BlockTags.INFINIBURN_OVERWORLD, //infiniburn
                BuiltinDimensionTypes.OVERWORLD_EFFECTS, //effectsLocation
                0.3f, //ambientlight              piglinsafe   Raids
                new DimensionType.MonsterSettings(true, false, UniformInt.of(0, 0), 0)));
    }
}
