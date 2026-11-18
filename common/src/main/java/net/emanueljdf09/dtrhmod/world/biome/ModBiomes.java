package net.emanueljdf09.dtrhmod.world.biome;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.world.features.WonderBiomeFeatures;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;

public class ModBiomes {
    public static final ResourceKey<Biome> TULGEY_WOOD =
            register("tulgey_wood");

    public static final ResourceKey<Biome> CHESSBOARD_FIELDS =
            register("chessboard_fields");

    public static final ResourceKey<Biome> VALE_OF_TEARS =
            register("vale_of_tears");

    public static final ResourceKey<Biome> THE_EXTERIOR =
            register("the_exterior");

    public static final ResourceKey<Biome> ENCHANTED_FOREST =
            register("enchanted_forest");

    public static final ResourceKey<Biome> WONDERLAND_OCEAN =
            register("wonderland_ocean");

    public static final ResourceKey<Biome> LAND_OF_FLOWERS =
            register("land_of_flowers");


    private static ResourceKey<Biome> register(String name) {
        return ResourceKey.create(
                Registries.BIOME,
                ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, name)
        );
    }

    public static void bootstrap(BootstrapContext<Biome> context) {
        context.register(TULGEY_WOOD, tulgeyForest(context));
        context.register(THE_EXTERIOR, theExterior(context));
        context.register(ENCHANTED_FOREST, enchantedForest(context));
        context.register(LAND_OF_FLOWERS, enchantedForest(context));
        context.register(VALE_OF_TEARS, valeOfTears(context));
        context.register(CHESSBOARD_FIELDS, chessBoardFields(context));
    }

    public  static Biome tulgeyForest(BootstrapContext<Biome> context) {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        BiomeGenerationSettings.Builder biomeBuilder =
                new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE),
                        context.lookup(Registries.CONFIGURED_CARVER));

        WonderBiomeFeatures.addDefaultDisks(biomeBuilder);
        WonderBiomeFeatures.addTulgeyWoodsVegetation(biomeBuilder);
        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(0.7f)
                .downfall(0.8f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(2977143)
                        .waterFogColor(2977143)
                        .fogColor(4416619)
                        .skyColor(4416619)
                        .grassColorOverride(4416619)
                        .build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(biomeBuilder.build())
                .build();
    }

    public  static Biome valeOfTears(BootstrapContext<Biome> context) {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();
        BiomeGenerationSettings.Builder biomeBuilder =
                new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE),
                        context.lookup(Registries.CONFIGURED_CARVER));

        WonderBiomeFeatures.addDefaultDisks(biomeBuilder);
        WonderBiomeFeatures.addValeOfTearsVegetation(biomeBuilder);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(0.7f)
                .downfall(0.8f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(2919324)
                        .waterFogColor(2919324)
                        .fogColor(4418396)
                        .skyColor(4747926)
                        .grassColorOverride(4418396)
                        .build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(biomeBuilder.build())
                .build();
    }

    public  static Biome chessBoardFields(BootstrapContext<Biome> context) {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        BiomeGenerationSettings.Builder biomeBuilder =
                new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE),
                        context.lookup(Registries.CONFIGURED_CARVER));

        WonderBiomeFeatures.addDefaultDisks(biomeBuilder);
        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(0.7f)
                .downfall(0.8f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(2461864)
                        .waterFogColor(2461864)
                        .fogColor(6000314)
                        .skyColor(6000314)
                        .grassColorOverride(3576124)
                        .build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(biomeBuilder.build())
                .build();
    }

    public  static Biome theExterior(BootstrapContext<Biome> context) {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        BiomeGenerationSettings.Builder biomeBuilder =
                new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE),
                        context.lookup(Registries.CONFIGURED_CARVER));

        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(0.5f)
                .downfall(0.5f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(4159318)
                        .waterFogColor(329011)
                        .fogColor(12632256)
                        .skyColor(7907303)
                        .grassColorOverride(7907303)
                        .build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(biomeBuilder.build())
                .build();
    }

    public  static Biome enchantedForest(BootstrapContext<Biome> context) {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        BiomeGenerationSettings.Builder biomeBuilder =
                new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE),
                        context.lookup(Registries.CONFIGURED_CARVER));

        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(0.5f)
                .downfall(0.5f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(4159318)
                        .waterFogColor(329011)
                        .fogColor(12632256)
                        .skyColor(7907303)
                        .grassColorOverride(7907303)
                        .build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(biomeBuilder.build())
                .build();
    }
}
