package net.emanueljdf09.dtrhmod.item.block;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.block.custom.head.ModHeadType;
import net.minecraft.block.Block;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.item.VerticallyAttachableBlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModHeadBlockItem extends VerticallyAttachableBlockItem implements GeoItem {

    private final ModHeadType headType;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    public ModHeadBlockItem(ModHeadType headType, Block standingBlock, Block wallBlock, Settings settings) {
        super(standingBlock, wallBlock, settings, Direction.DOWN);
        this.headType = headType;
    }

    public ModHeadType getHeadType() {
        return headType;
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private GeoItemRenderer<ModHeadBlockItem> renderer = null;

            @Override
            public BuiltinModelItemRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = new GeoItemRenderer<>(new DefaultedItemGeoModel<>(new Identifier(DownTheRabbitHole.MOD_ID, headType.getName() + "_head")) {
                        @Override
                        public Identifier getTextureResource(ModHeadBlockItem animatable) {
                            return headType.getTextureId();
                        }

                        @Override
                        public Identifier getAnimationResource(ModHeadBlockItem animatable) {
                            return null;
                        }
                    });

                return this.renderer;
            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return this.renderProvider;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}