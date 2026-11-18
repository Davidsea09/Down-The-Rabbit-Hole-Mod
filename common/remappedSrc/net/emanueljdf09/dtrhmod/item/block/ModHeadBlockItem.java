package net.emanueljdf09.dtrhmod.item.block;

import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.fabric.block.custom.head.ModHeadType;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class ModHeadBlockItem extends StandingAndWallBlockItem implements GeoItem {

    private final ModHeadType headType;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ModHeadBlockItem(ModHeadType headType, Block standingBlock, Block wallBlock, Properties settings) {
        super(standingBlock, wallBlock, settings, Direction.DOWN);
        this.headType = headType;
    }

    public ModHeadType getHeadType() {
        return headType;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoItemRenderer<ModHeadBlockItem> renderer = null;

            @Override
            public @Nullable BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null) {
                    this.renderer = new GeoItemRenderer<>(new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, headType.getName() + "_head")) {
                        @Override
                        public Identifier getTextureResource(ModHeadBlockItem animatable) {
                            return headType.getTextureId();
                        }

                        @Override
                        public Identifier getAnimationResource(ModHeadBlockItem animatable) {
                            return null;
                        }
                    });
                }
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}