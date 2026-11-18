package net.emanueljdf09.dtrhmod.client;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.block.ModBlockCollections;
import net.emanueljdf09.dtrhmod.block.ModBlockEntities;
import net.emanueljdf09.dtrhmod.block.ModBlocks;
import net.emanueljdf09.dtrhmod.block.models.renderers.*;
import net.emanueljdf09.dtrhmod.entity.ModEntities;
import net.emanueljdf09.dtrhmod.entity.client.renderers.*;
import net.emanueljdf09.dtrhmod.item.ModItems;
import net.emanueljdf09.dtrhmod.item.client.renderer.CatScarfTrinketRenderer;
import net.emanueljdf09.dtrhmod.item.client.renderer.MadHatterHatTrinketRenderer;
import net.emanueljdf09.dtrhmod.menu.ModScreenHandlers;
import net.emanueljdf09.dtrhmod.menu.screen.BookCoverScreen;
import net.emanueljdf09.dtrhmod.menu.screen.TeapotScreen;
import net.emanueljdf09.dtrhmod.util.*;
import net.emanueljdf09.dtrhmod.util.components.ClientProgression;
import net.emanueljdf09.dtrhmod.util.components.ClientProgressionCache;
import net.emanueljdf09.dtrhmod.util.components.ProgressionComponent;
import net.emanueljdf09.dtrhmod.util.components.ProgressionSync;
import net.emanueljdf09.dtrhmod.util.config.ModConfigData;
import net.emanueljdf09.dtrhmod.util.particles.FallingBBLeafParticle;
import net.emanueljdf09.dtrhmod.util.particles.FallingTHLeafParticle;
import net.emanueljdf09.dtrhmod.util.particles.FallingWWLeafParticle;
import net.emanueljdf09.dtrhmod.util.particles.WonderlandLockParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;

import java.util.Optional;

public class DownTheRabbitHoleClient {

    public static void init() {
        ProgressionSync.registerClientReceiver();
        registerClientNetworkPackets();

        ColorHandlerRegistry.registerItemColors((stack, tintIndex) -> GrassColor.get(0.5, 1.0),
                ModBlocks.WONDER_GRASS.get(),
                ModBlocks.SINGING_FLOWERS_SPAWNER_BLOCK.get(),
                ModBlocks.CATERPILLAR_SPAWNER_BLOCK.get()
        );

        ColorHandlerRegistry.registerItemColors((stack, tintIndex) -> {
            if (tintIndex == 1) {
                CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
                if (customData != null) {
                    CompoundTag nbt = customData.copyTag();
                    if (nbt.contains("Color")) {
                        return 0xFF000000 | nbt.getInt("Color");
                    }
                }
                return 0xFF3399FF;
            }
            return -1;
        }, ModItems.FILLED_TEA_CUP.get());

        ColorHandlerRegistry.registerBlockColors((state, world, pos, tintIndex) -> {
                    if (tintIndex != 0 || world == null || pos == null) return 0xFFFFFF;

                    int baseColor = BiomeColors.getAverageGrassColor(world, pos);
                    var level = Minecraft.getInstance().level;
                    if (level == null || !level.getBiome(pos).is(ModTags.Biomes.IS_CHESSBOARD)) {
                        return baseColor;
                    }

                    int size = 4;
                    boolean darkSquare = (Math.floorDiv(pos.getX(), size) + Math.floorDiv(pos.getZ(), size)) % 2 == 0;
                    return darkSquare ? darken(baseColor, 0.7f) : baseColor;
                },
                ModBlocks.WONDER_GRASS.get(),
                ModBlocks.SINGING_FLOWERS_SPAWNER_BLOCK.get(),
                ModBlocks.CATERPILLAR_SPAWNER_BLOCK.get(),
                Blocks.GRASS_BLOCK,
                Blocks.TALL_GRASS,
                Blocks.FERN,
                Blocks.LARGE_FERN
        );

        ColorHandlerRegistry.registerBlockColors((state, world, pos, tintIndex) -> {
            if (tintIndex != 0) {
                return world != null && pos != null ? BiomeColors.getAverageGrassColor(world, pos) : GrassColor.getDefaultColor();
            } else {
                return -1;
            }
        }, ModBlocks.LAWN_DAISY_PATCH.get());

        RenderTypeRegistry.register(RenderType.cutout(),
                ModBlocks.WONDER_GRASS.get(),
                ModBlocks.SINGING_FLOWERS_SPAWNER_BLOCK.get(),
                ModBlocks.CATERPILLAR_SPAWNER_BLOCK.get(),
                ModBlocks.LAWN_DAISY_PATCH.get(),
                ModBlocks.TH_LEAVES.get(),
                ModBlocks.TH_DOOR.get(),
                ModBlocks.TH_TRAPDOOR.get(),
                ModBlocks.WW_LEAVES.get(),
                ModBlocks.WW_DOOR.get(),
                ModBlocks.WW_TRAPDOOR.get(),
                ModBlocks.WW_HANGING_LEAVES_PLANT.get(),
                ModBlocks.WW_HANGING_LEAVES.get(),
                ModBlocks.BB_LEAVES.get(),
                ModBlocks.BB_DOOR.get(),
                ModBlocks.BB_TRAPDOOR.get(),
                ModBlocks.HH_LEAVES.get(),
                ModBlocks.HH_DOOR.get(),
                ModBlocks.HH_TRAPDOOR.get(),
                ModBlocks.TH_SAPLING.get(),
                ModBlocks.WW_SAPLING.get(),
                ModBlocks.BB_SAPLING.get(),
                ModBlocks.HH_SAPLING.get(),
                ModBlocks.YELLOW_MUSHROOM.get(),
                ModBlocks.BLUE_MUSHROOM.get(),
                ModBlocks.MAGENTA_MUSHROOM.get(),
                ModBlocks.FLOWER_PORTAL.get(),
                ModBlocks.POTTED_FLOWER_PORTAL.get(),
                ModBlocks.TEAPOT_BLOCK.get(),
                ModBlocks.MAD_HATTER_HEAD.get(),
                ModBlocks.MAD_HATTER_WALL_HEAD.get(),
                ModBlocks.CATERPILLAR_HEAD.get(),
                ModBlocks.CATERPILLAR_WALL_HEAD.get(),
                ModBlocks.MAD_HATTER_HAT.get()
        );

        BlockEntityRendererRegistry.register(ModBlockEntities.MIRROR_BLOCK_ENTITY.get(), MirrorBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.EXTERIOR_DOOR_ENTITY.get(), ExteriorDoorRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.EXTERIOR_CHEST_ENTITY.get(), ExteriorChestRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.MOD_HEAD_BE.get(), ModHeadBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.MAD_HATTER_HAT.get(), MadHatterHatBlockEntityRenderer::new);

        Sheets.SIGN_MATERIALS.put(ModBlockCollections.TH_WOOD_TYPE, Sheets.getSignMaterial(ModBlockCollections.TH_WOOD_TYPE));
        Sheets.SIGN_MATERIALS.put(ModBlockCollections.WW_WOOD_TYPE, Sheets.getSignMaterial(ModBlockCollections.WW_WOOD_TYPE));
        Sheets.SIGN_MATERIALS.put(ModBlockCollections.HH_WOOD_TYPE, Sheets.getSignMaterial(ModBlockCollections.HH_WOOD_TYPE));
        Sheets.SIGN_MATERIALS.put(ModBlockCollections.BB_WOOD_TYPE, Sheets.getSignMaterial(ModBlockCollections.BB_WOOD_TYPE));

        Sheets.HANGING_SIGN_MATERIALS.put(ModBlockCollections.TH_WOOD_TYPE, Sheets.getHangingSignMaterial(ModBlockCollections.TH_WOOD_TYPE));
        Sheets.HANGING_SIGN_MATERIALS.put(ModBlockCollections.HH_WOOD_TYPE, Sheets.getHangingSignMaterial(ModBlockCollections.HH_WOOD_TYPE));
        Sheets.HANGING_SIGN_MATERIALS.put(ModBlockCollections.WW_WOOD_TYPE, Sheets.getHangingSignMaterial(ModBlockCollections.WW_WOOD_TYPE));
        Sheets.HANGING_SIGN_MATERIALS.put(ModBlockCollections.BB_WOOD_TYPE, Sheets.getHangingSignMaterial(ModBlockCollections.BB_WOOD_TYPE));

        EntityRendererRegistry.register(ModEntities.WHITE_RABBIT, WhiteRabbitRenderer::new);
        EntityRendererRegistry.register(ModEntities.WEEPING_PLAYER, WeepingPlayerRenderer::new);
        EntityRendererRegistry.register(ModEntities.MAD_HATTER, MadHatterRenderer::new);
        EntityRendererRegistry.register(ModEntities.CHESHIRE_CAT, CheshireCatRenderer::new);
        EntityRendererRegistry.register(ModEntities.SINGING_FLOWER, SingingFlowerRenderer::new);
        EntityRendererRegistry.register(ModEntities.MOCK_TURTLE, MockTurtleRenderer::new);
        EntityRendererRegistry.register(ModEntities.CATERPILLAR, CaterpillarRenderer::new);
        EntityRendererRegistry.register(ModEntities.VINE_ANCHOR, VineAnchorRenderer::new);
        EntityRendererRegistry.register(ModEntities.SKY_SEED, ThrownItemRenderer::new);
        EntityRendererRegistry.register(ModEntities.VINE_LASSO_PROJECTILE, ThrownItemRenderer::new);
        EntityRendererRegistry.register(ModEntities.PLANT_FANGS, PlantFangEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.HARE, HareRenderer::new);
        EntityRendererRegistry.register(ModEntities.TEAPOT_PROJECTILE, ThrownItemRenderer::new);

        MenuRegistry.registerScreenFactory(ModScreenHandlers.TEAPOT_SCREEN_HANDLER.get(), TeapotScreen::new);

        AccessoriesRendererRegistry.registerRenderer(ModItems.CAT_SCARF_ITEM.get(), CatScarfTrinketRenderer::new);
        AccessoriesRendererRegistry.registerRenderer(ModItems.MAD_HAT_ITEM.get(), MadHatterHatTrinketRenderer::new);

        ParticleProviderRegistry.register(ModParticles.BB_LEAVE_PARTICLE.get().getType(), FallingBBLeafParticle.Factory::new);
        ParticleProviderRegistry.register(ModParticles.TH_LEAVE_PARTICLE.get().getType(), FallingTHLeafParticle.Factory::new);
        ParticleProviderRegistry.register(ModParticles.WW_LEAVE_PARTICLE.get().getType(), FallingWWLeafParticle.Factory::new);
        ParticleProviderRegistry.register(ModParticles.WONDERLAND_LOCK_PARTICLE.get().getType(), WonderlandLockParticle.Factory::new);

        ClientTickEvent.CLIENT_POST.register(client -> {
            if (client.player == null || client.level == null || client.isPaused()) return;

            if (client.level.dimension().location().getNamespace().equals(DownTheRabbitHole.MOD_ID)) {
                ProgressionComponent progression = ClientProgression.get();
                int stage = progression.getCompletedStages();
                BlockPos playerPos = client.player.blockPosition();

                if (client.level.getGameTime() % 3 == 0) {
                    Optional<ResourceKey<Biome>> currentBiomeKey = client.level.getBiome(playerPos).unwrapKey();
                    if (currentBiomeKey.isPresent()) {
                        ResourceLocation biomeId = currentBiomeKey.get().location();

                        if (biomeId.getPath().equals("tulgey_wood")) {
                            double x = client.player.getX() + (client.level.random.nextDouble() - 0.5) * 16.0;
                            double y = client.player.getY() + client.level.random.nextDouble() * 8.0;
                            double z = client.player.getZ() + (client.level.random.nextDouble() - 0.5) * 16.0;
                            client.level.addParticle(ParticleTypes.SOUL, x, y, z, 0.0, 0.05, 0.0);
                        } else if (biomeId.getPath().equals("vale_of_tears") && stage >= 1) {
                            double x = client.player.getX() + (client.level.random.nextDouble() - 0.5) * 16.0;
                            double y = client.player.getY() + client.level.random.nextDouble() * 8.0;
                            double z = client.player.getZ() + (client.level.random.nextDouble() - 0.5) * 16.0;
                            client.level.addParticle(ParticleTypes.SOUL, x, y, z, 0.0, 0.05, 0.0);
                        } else if (biomeId.getPath().equals("chessboard_fields") && stage >= 2) {
                            double x = client.player.getX() + (client.level.random.nextDouble() - 0.5) * 16.0;
                            double y = client.player.getY() + client.level.random.nextDouble() * 5.0;
                            double z = client.player.getZ() + (client.level.random.nextDouble() - 0.5) * 16.0;
                            client.level.addParticle(ParticleTypes.END_ROD, x, y, z, 0.0, -0.01, 0.0);
                        }
                    }
                }

                if (client.level.getGameTime() % 25 == 0) {
                    if (ModConfigData.isBiomeLockingEnabled() && client.player != null
                            && !client.player.isCreative() && !client.player.isSpectator()) {
                        int radius = 8;
                        for (int x = -radius; x <= radius; x += 2) {
                            for (int z = -radius; z <= radius; z += 2) {
                                BlockPos checkPos = playerPos.offset(x, 0, z);
                                Optional<ResourceKey<Biome>> biomeKey = client.level.getBiome(checkPos).unwrapKey();

                                if (biomeKey.isPresent()) {
                                    ResourceLocation biomeId = biomeKey.get().location();
                                    boolean isLocked = false;

                                    if (biomeId.getPath().equals("vale_of_tears") && stage < 1) {
                                        isLocked = true;
                                    } else if (biomeId.getPath().equals("chessboard_fields") && stage < 2) {
                                        isLocked = true;
                                    }

                                    if (isLocked) {
                                        double px = checkPos.getX() + 0.5;
                                        double pz = checkPos.getZ() + 0.5;

                                        for (double py = client.player.getY() - 1; py <= client.player.getY() + 3; py += 1.0) {
                                            client.level.addParticle(
                                                    ModParticles.WONDERLAND_LOCK_PARTICLE.get().getType(),
                                                    px, py, pz,
                                                    0.0, 0.02, 0.0
                                            );
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (client.level.getGameTime() % 5 == 0) {
                    Optional<ResourceKey<Biome>> currentBiomeKey = client.level.getBiome(playerPos).unwrapKey();
                    if (currentBiomeKey.isPresent()) {
                        ResourceLocation biomeId = currentBiomeKey.get().location();

                        if (biomeId.getPath().equals("tulgey_wood")) {
                            for (int i = 0; i < 4; i++) {
                                double x = client.player.getX() + (client.level.random.nextDouble() - 0.5) * 32.0;
                                double y = client.player.getY() + 6.0 + client.level.random.nextDouble() * 6.0;
                                double z = client.player.getZ() + (client.level.random.nextDouble() - 0.5) * 32.0;

                                client.level.addParticle(
                                        ModParticles.BB_LEAVE_PARTICLE.get().getType(),
                                        x, y, z,
                                        (client.level.random.nextDouble() - 0.5) * 0.02, -0.02, (client.level.random.nextDouble() - 0.5) * 0.02
                                );
                                client.level.addParticle(
                                        ModParticles.TH_LEAVE_PARTICLE.get().getType(),
                                        x, y, z,
                                        (client.level.random.nextDouble() - 0.5) * 0.02, -0.02, (client.level.random.nextDouble() - 0.5) * 0.02
                                );
                            }
                        } else if (biomeId.getPath().equals("vale_of_tears")) {
                            for (int i = 0; i < 4; i++) {
                                double x = client.player.getX() + (client.level.random.nextDouble() - 0.5) * 32.0;
                                double y = client.player.getY() + 6.0 + client.level.random.nextDouble() * 6.0;
                                double z = client.player.getZ() + (client.level.random.nextDouble() - 0.5) * 32.0;

                                client.level.addParticle(
                                        ModParticles.WW_LEAVE_PARTICLE.get().getType(),
                                        x, y, z,
                                        (client.level.random.nextDouble() - 0.5) * 0.02, -0.02, (client.level.random.nextDouble() - 0.5) * 0.02
                                );
                            }
                        } else if (biomeId.getPath().equals("chessboard_fields")) {
                            for (int i = 0; i < 3; i++) {
                                double x = client.player.getX() + (client.level.random.nextDouble() - 0.5) * 32.0;
                                double y = client.player.getY() + 15.0 + client.level.random.nextDouble() * 15.0;
                                double z = client.player.getZ() + (client.level.random.nextDouble() - 0.5) * 32.0;

                                client.level.addParticle(
                                        ParticleTypes.FALLING_NECTAR,
                                        x, y, z,
                                        0.0, -0.05, 0.0
                                );
                            }
                        }
                    }
                }
            }
        });
    }


    public static void registerClientNetworkPackets() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, ModPackets.OpenBookPayload.TYPE, ModPackets.OpenBookPayload.CODEC, (payload, context) -> {

            String key = payload.triggerKey();
            context.queue(() -> {
                Minecraft.getInstance().setScreen(new BookCoverScreen(key));
            });
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, ModPackets.LockedBiomeToastPayload.TYPE, ModPackets.LockedBiomeToastPayload.CODEC, (payload, context) -> {
            context.queue(() -> {
                ClientProgressionCache.setNearLockedBiome(true);
                Minecraft.getInstance().getToasts().addToast(
                        new WonderlandProgressionUtil.LockedBiomeToast(new ItemStack(ModItems.POCKETWATCH.get()))
                );
            });
        });
    }

    private static int darken(int color, float factor) {
        int r = (int)(((color >> 16) & 255) * factor);
        int g = (int)(((color >> 8) & 255) * factor);
        int b = (int)((color & 255) * factor);

        return (r << 16) | (g << 8) | b;
    }
}