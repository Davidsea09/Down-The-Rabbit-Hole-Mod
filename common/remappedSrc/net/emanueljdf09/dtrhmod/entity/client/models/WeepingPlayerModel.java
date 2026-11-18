package net.emanueljdf09.dtrhmod.entity.client.models;

import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.fabric.entity.custom.WeepingPlayerEntity;
import net.emanueljdf09.dtrhmod.fabric.util.TextureProcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;
import com.mojang.blaze3d.platform.NativeImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeepingPlayerModel extends GeoModel<WeepingPlayerEntity> {
    private static final Map<String, ResourceLocation> STONE_SKIN_CACHE = new HashMap<>();
    private static final ResourceLocation FALLBACK_STONE_TEXTURE = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/entity/pool/alice_slim.png");

    private static final List<ResourceLocation> LOCAL_SKIN_POOL = List.of(
            ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/entity/pool/alice_slim.png"),
            ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/entity/pool/ceshire_slim.png"),
            ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/entity/pool/hatter.png")
    );

    @Override
    public ResourceLocation getModelResource(WeepingPlayerEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "geo/entity/weeping_statue.geo.json");
    }

    @Override
    public ResourceLocation getAnimationResource(WeepingPlayerEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "animations/entity/weeping_players.animation.json");
    }

    @Override
    public ResourceLocation getTextureResource(WeepingPlayerEntity animatable) {
        int poolIndex = animatable.getSkinPoolIndex();
        Minecraft client = Minecraft.getInstance();

        if (poolIndex == -1 && client.level != null) {
            Player closestPlayer = client.level.getNearestPlayer(animatable.getX(), animatable.getY(), animatable.getZ(), 24.0, false);

            if (closestPlayer instanceof AbstractClientPlayer player) {
                String cacheKey = "player_" + player.getUUID().toString();

                if (STONE_SKIN_CACHE.containsKey(cacheKey)) {
                    return STONE_SKIN_CACHE.get(cacheKey);
                }

                ResourceLocation originalSkin = player.getSkin().texture();

                try {

                    var resourceOpt = client.getResourceManager().getResource(originalSkin);
                    if (resourceOpt.isPresent()) {
                        NativeImage originalNative = NativeImage.read(resourceOpt.get().open());
                        return registerAndCacheStoneTexture(client, originalNative, cacheKey, player.getUUID().toString());
                    }

                    AbstractTexture texture = client.getTextureManager().getTexture(originalSkin);
                    if (texture instanceof DynamicTexture nativeTex) {
                        NativeImage originalNative = nativeTex.getPixels();
                        if (originalNative != null) {
                            NativeImage stoneNative = TextureProcessor.convertToStone(originalNative);
                            DynamicTexture dynamicTex = new DynamicTexture(stoneNative);
                            ResourceLocation stoneIdentifier = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "dynamic_stone_" + player.getUUID());

                            client.getTextureManager().register(stoneIdentifier, dynamicTex);
                            STONE_SKIN_CACHE.put(cacheKey, stoneIdentifier);
                            return stoneIdentifier;
                        }
                    }

                    return originalSkin;

                } catch (Exception e) {
                    return FALLBACK_STONE_TEXTURE;
                }
            }
            return FALLBACK_STONE_TEXTURE;
        }

        if (poolIndex >= LOCAL_SKIN_POOL.size() || poolIndex < 0) poolIndex = 0;
        ResourceLocation localSkinPath = LOCAL_SKIN_POOL.get(poolIndex);
        String cacheKey = localSkinPath.toString();

        if (STONE_SKIN_CACHE.containsKey(cacheKey)) {
            return STONE_SKIN_CACHE.get(cacheKey);
        }

        try {
            NativeImage originalNative = NativeImage.read(client.getResourceManager().getResource(localSkinPath).get().open());
            return registerAndCacheStoneTexture(client, originalNative, cacheKey, "pool_" + poolIndex);
        } catch (Exception e) {
            return FALLBACK_STONE_TEXTURE;
        }
    }

    private ResourceLocation registerAndCacheStoneTexture(Minecraft client, NativeImage originalNative, String cacheKey, String uniqueId) {
        NativeImage stoneNative = TextureProcessor.convertToStone(originalNative);
        DynamicTexture dynamicTex = new DynamicTexture(stoneNative);
        ResourceLocation stoneIdentifier = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "stone_tex_" + uniqueId);

        client.getTextureManager().register(stoneIdentifier, dynamicTex);
        STONE_SKIN_CACHE.put(cacheKey, stoneIdentifier);

        return stoneIdentifier;
    }

    public static boolean isPoolSkinSlim(int index) {
        if (index >= 0 && index < LOCAL_SKIN_POOL.size()) {
            return LOCAL_SKIN_POOL.get(index).getPath().contains("slim");
        }
        return false;
    }

    @Override
    public void setCustomAnimations(WeepingPlayerEntity animatable, long instanceId, AnimationState<WeepingPlayerEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}