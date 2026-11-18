package net.emanueljdf09.dtrhmod.client;

import com.terraformersmc.terraform.boat.api.client.TerraformBoatClientHelper;
import com.terraformersmc.terraform.sign.api.SpriteIdentifierRegistry;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.fabric.block.ModBlockEntities;
import net.emanueljdf09.dtrhmod.fabric.block.ModBlocks;
import net.emanueljdf09.dtrhmod.fabric.entity.ModBoats;
import net.emanueljdf09.dtrhmod.fabric.entity.ModEntities;
import net.emanueljdf09.dtrhmod.fabric.entity.client.renderers.*;
import net.emanueljdf09.dtrhmod.fabric.item.ModItems;
import net.emanueljdf09.dtrhmod.fabric.item.client.renderer.CatScarfTrinketRenderer;
import net.emanueljdf09.dtrhmod.fabric.item.client.renderer.MadHatterHatTrinketRenderer;
import net.emanueljdf09.dtrhmod.fabric.menu.ModScreenHandlers;
import net.emanueljdf09.dtrhmod.fabric.menu.screen.BookCoverScreen;
import net.emanueljdf09.dtrhmod.fabric.menu.screen.TeapotScreen;
import net.emanueljdf09.dtrhmod.fabric.util.ModPackets;
import net.emanueljdf09.dtrhmod.fabric.util.ModParticles;
import net.emanueljdf09.dtrhmod.fabric.util.WonderlandProgressionUtil;
import net.emanueljdf09.dtrhmod.fabric.util.components.ProgressionComponent;
import net.emanueljdf09.dtrhmod.fabric.util.config.ModConfigData;
import net.emanueljdf09.dtrhmod.fabric.util.particles.FallingBBLeafParticle;
import net.emanueljdf09.dtrhmod.fabric.util.particles.FallingTHLeafParticle;
import net.emanueljdf09.dtrhmod.fabric.util.particles.FallingWWLeafParticle;
import net.emanueljdf09.dtrhmod.fabric.util.particles.WonderlandLockParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.Blocks;
import java.util.Optional;

public class DownTheRabbitHoleClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        registerClientNetworkPackets();

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> GrassColors.getColor(0.5, 1.0), ModBlocks.WONDER_GRASS);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> GrassColors.getColor(0.5, 1.0), ModBlocks.SINGING_FLOWERS_SPAWNER_BLOCK);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> GrassColors.getColor(0.5, 1.0), ModBlocks.CATERPILLAR_SPAWNER_BLOCK);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            if (tintIndex == 1) {
                NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
                if (customData != null) {
                    NbtCompound nbt = customData.copyNbt();
                    if (nbt.contains("Color")) {
                         return 0xFF000000 | nbt.getInt("Color");
                    }
                }
                return 0xFF3399FF;
            }
            return -1;
        }, ModItems.FILLED_TEA_CUP);

        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {

                    if (tintIndex != 0) return 0xFFFFFF;

                    if (world == null || pos == null)
                        return 0xFFFFFF;

                    int baseColor = BiomeColors.getGrassColor(world, pos);

                    var biome = world.getBiomeFabric(pos);

                    if (biome == null || !biome.hasKeyAndValue()) {
                        return baseColor;
                    }

                    boolean isChessBiome =
                            biome.isIn(ModTags.Biomes.IS_CHESSBOARD);

                    if (!isChessBiome)
                        return baseColor;

                    int size = 4;

                    boolean darkSquare =
                            (Math.floorDiv(pos.getX(), size)
                                    + Math.floorDiv(pos.getZ(), size)) % 2 == 0;

                    return darkSquare ? darken(baseColor, 0.7f) : baseColor;

                }, ModBlocks.WONDER_GRASS,
                ModBlocks.SINGING_FLOWERS_SPAWNER_BLOCK,
                ModBlocks.CATERPILLAR_SPAWNER_BLOCK,
                Blocks.GRASS_BLOCK,
                Blocks.TALL_GRASS,
                Blocks.FERN,
                Blocks.LARGE_FERN
        );

        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {

            if (tintIndex != 0) {
                return world != null && pos != null ? BiomeColors.getGrassColor(world, pos) : GrassColors.getDefaultColor();
            } else {
                return -1;
            }
        }, ModBlocks.LAWN_DAISY_PATCH);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WONDER_GRASS, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SINGING_FLOWERS_SPAWNER_BLOCK, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CATERPILLAR_SPAWNER_BLOCK, RenderType.cutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.LAWN_DAISY_PATCH, RenderType.cutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TH_LEAVES, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TH_DOOR, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TH_TRAPDOOR, RenderType.cutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WW_LEAVES, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WW_DOOR, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WW_TRAPDOOR, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WW_HANGING_LEAVES_PLANT, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WW_HANGING_LEAVES, RenderType.cutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BB_LEAVES, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BB_DOOR, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BB_TRAPDOOR, RenderType.cutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.HH_LEAVES, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.HH_DOOR, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.HH_TRAPDOOR, RenderType.cutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TH_SAPLING, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WW_SAPLING, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BB_SAPLING, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.HH_SAPLING, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.YELLOW_MUSHROOM, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BLUE_MUSHROOM, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MAGENTA_MUSHROOM, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FLOWER_PORTAL, RenderType.cutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POTTED_FLOWER_PORTAL, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TEAPOT_BLOCK, RenderType.cutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MAD_HATTER_HEAD, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MAD_HATTER_WALL_HEAD, RenderType.cutout());


        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CATERPILLAR_HEAD, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CATERPILLAR_WALL_HEAD, RenderType.cutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MAD_HATTER_HAT, RenderType.cutout());

        TerraformBoatClientHelper.registerModelLayers(ModBoats.BB_BOAT_ID, false);
        TerraformBoatClientHelper.registerModelLayers(ModBoats.TH_BOAT_ID, false);
        TerraformBoatClientHelper.registerModelLayers(ModBoats.WW_BOAT_ID, false);
        TerraformBoatClientHelper.registerModelLayers(ModBoats.HH_BOAT_ID, false);

        BlockEntityRendererRegistry.register(ModBlockEntities.MIRROR_BLOCK_ENTITY, MirrorBlockEntityRenderer::new);

        BlockEntityRendererRegistry.register(ModBlockEntities.EXTERIOR_DOOR_ENTITY, ExteriorDoorRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.EXTERIOR_CHEST_ENTITY, ExteriorChestRenderer::new);

        BlockEntityRendererRegistry.register(ModBlockEntities.MOD_HEAD_BE, ModHeadBlockEntityRenderer::new);

        BlockEntityRendererRegistry.register(ModBlockEntities.MAD_HATTER_HAT, MadHatterHatBlockEntityRenderer::new);
        TrinketRendererRegistry.registerRenderer(ModItems.MAD_HAT_ITEM, new MadHatterHatTrinketRenderer());
        TrinketRendererRegistry.registerRenderer(ModItems.CAT_SCARF_ITEM, new CatScarfTrinketRenderer());


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

        MenuScreens.register(ModScreenHandlers.TEAPOT_SCREEN_HANDLER, TeapotScreen::new);

        SpriteIdentifierRegistry.INSTANCE.addIdentifier(new Material(Sheets.SIGN_SHEET, ModBlocks.WW_SIGN_TEXTURE));
        SpriteIdentifierRegistry.INSTANCE.addIdentifier(new Material(Sheets.SIGN_SHEET, ModBlocks.HANGING_WW_SIGN_TEXTURE));
        SpriteIdentifierRegistry.INSTANCE.addIdentifier(new Material(Sheets.SIGN_SHEET, ModBlocks.BB_SIGN_TEXTURE));
        SpriteIdentifierRegistry.INSTANCE.addIdentifier(new Material(Sheets.SIGN_SHEET, ModBlocks.HANGING_BB_SIGN_TEXTURE));
        SpriteIdentifierRegistry.INSTANCE.addIdentifier(new Material(Sheets.SIGN_SHEET, ModBlocks.TH_SIGN_TEXTURE));
        SpriteIdentifierRegistry.INSTANCE.addIdentifier(new Material(Sheets.SIGN_SHEET, ModBlocks.HANGING_TH_SIGN_TEXTURE));

        ParticleFactoryRegistry.getInstance().register(ModParticles.BB_LEAVE_PARTICLE, FallingBBLeafParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.TH_LEAVE_PARTICLE, FallingTHLeafParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.WW_LEAVE_PARTICLE, FallingWWLeafParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.WONDERLAND_LOCK_PARTICLE, WonderlandLockParticle.Factory::new);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null || client.isPaused()) return;

            if (client.world.getRegistryKey().getValue().getNamespace().equals(DownTheRabbitHole.MOD_ID)) {
                ProgressionComponent progression = ModComponents.PROGRESSION_COMPONENT.get(client.player);
                int stage = progression.getCompletedStages();
                BlockPos playerPos = client.player.getBlockPos();

                if (client.world.getTime() % 3 == 0) {
                    Optional<RegistryKey<Biome>> currentBiomeKey = client.world.getBiome(playerPos).getKey();
                    if (currentBiomeKey.isPresent()) {
                        Identifier biomeId = currentBiomeKey.get().getValue();

                        if (biomeId.getPath().equals("tulgey_wood")) {
                            double x = client.player.getX() + (client.world.random.nextDouble() - 0.5) * 16.0;
                            double y = client.player.getY() + client.world.random.nextDouble() * 8.0;
                            double z = client.player.getZ() + (client.world.random.nextDouble() - 0.5) * 16.0;
                            client.world.addParticle(ParticleTypes.SOUL, x, y, z, 0.0, 0.05, 0.0);
                        } else if (biomeId.getPath().equals("vale_of_tears") && stage >= 1) {
                            double x = client.player.getX() + (client.world.random.nextDouble() - 0.5) * 16.0;
                            double y = client.player.getY() + client.world.random.nextDouble() * 8.0;
                            double z = client.player.getZ() + (client.world.random.nextDouble() - 0.5) * 16.0;
                            client.world.addParticle(ParticleTypes.SOUL, x, y, z, 0.0, 0.05, 0.0);
                        } else if (biomeId.getPath().equals("chessboard_fields") && stage >= 2) {
                            double x = client.player.getX() + (client.world.random.nextDouble() - 0.5) * 16.0;
                            double y = client.player.getY() + client.world.random.nextDouble() * 5.0;
                            double z = client.player.getZ() + (client.world.random.nextDouble() - 0.5) * 16.0;
                            client.world.addParticle(ParticleTypes.END_ROD, x, y, z, 0.0, -0.01, 0.0);
                        }
                    }
                }

                if (client.world.getTime() % 25 == 0) {

                    if (ModConfigData.isBiomeLockingEnabled() && client.player != null
                            && !client.player.isCreative() && !client.player.isSpectator()) {
                        int radius = 8;
                        for (int x = -radius; x <= radius; x += 2) {
                            for (int z = -radius; z <= radius; z += 2) {
                                BlockPos checkPos = playerPos.add(x, 0, z);
                                Optional<RegistryKey<Biome>> biomeKey = client.world.getBiome(checkPos).getKey();

                                if (biomeKey.isPresent()) {
                                    Identifier biomeId = biomeKey.get().getValue();
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
                                            client.world.addParticle(
                                                    ModParticles.WONDERLAND_LOCK_PARTICLE,
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

                if (client.world.getTime() % 5 == 0) {
                    Optional<RegistryKey<Biome>> currentBiomeKey = client.world.getBiome(playerPos).getKey();
                    if (currentBiomeKey.isPresent()) {
                        Identifier biomeId = currentBiomeKey.get().getValue();

                        if (biomeId.getPath().equals("tulgey_wood")) {

                            for (int i = 0; i < 4; i++) {

                                double x = client.player.getX() + (client.world.random.nextDouble() - 0.5) * 32.0;
                                double y = client.player.getY() + 6.0 + client.world.random.nextDouble() * 6.0;
                                double z = client.player.getZ() + (client.world.random.nextDouble() - 0.5) * 32.0;

                                client.world.addParticle(
                                        ModParticles.BB_LEAVE_PARTICLE,
                                        x, y, z,
                                        (client.world.random.nextDouble() - 0.5) * 0.02, -0.02, (client.world.random.nextDouble() - 0.5) * 0.02
                                );
                                client.world.addParticle(
                                        ModParticles.TH_LEAVE_PARTICLE,
                                        x, y, z,
                                        (client.world.random.nextDouble() - 0.5) * 0.02, -0.02, (client.world.random.nextDouble() - 0.5) * 0.02
                                );
                            }
                        }

                        else if (biomeId.getPath().equals("vale_of_tears")) {
                            for (int i = 0; i < 4; i++) {
                                double x = client.player.getX() + (client.world.random.nextDouble() - 0.5) * 32.0;
                                double y = client.player.getY() + 6.0 + client.world.random.nextDouble() * 6.0;
                                double z = client.player.getZ() + (client.world.random.nextDouble() - 0.5) * 32.0;

                                client.world.addParticle(
                                        ModParticles.WW_LEAVE_PARTICLE,
                                        x, y, z,
                                        (client.world.random.nextDouble() - 0.5) * 0.02, -0.02, (client.world.random.nextDouble() - 0.5) * 0.02
                                );
                            }
                        }

                        else if (biomeId.getPath().equals("chessboard_fields")) {
                            for (int i = 0; i < 3; i++) {
                                double x = client.player.getX() + (client.world.random.nextDouble() - 0.5) * 32.0;
                                double y = client.player.getY() + 15.0 + client.world.random.nextDouble() * 15.0;
                                double z = client.player.getZ() + (client.world.random.nextDouble() - 0.5) * 32.0;

                                client.world.addParticle(
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
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.OpenBookPayload.ID, (payload, context) -> {
            String triggerKey = payload.triggerKey();
            context.client().execute(() -> {
                MinecraftClient.getInstance().setScreen(new BookCoverScreen(triggerKey));
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(ModPackets.LockedBiomeToastPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientProgressionCache.setNearLockedBiome(true);
                MinecraftClient.getInstance().getToastManager().add(new WonderlandProgressionUtil.LockedBiomeToast(new ItemStack(ModItems.POCKETWATCH)));
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
