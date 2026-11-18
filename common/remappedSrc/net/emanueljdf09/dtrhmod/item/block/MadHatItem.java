package net.emanueljdf09.dtrhmod.item.block;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class MadHatItem extends BlockItem implements GeoItem, Trinket {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public MadHatItem(Block block, Properties settings) {
        super(block, settings);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoItemRenderer<MadHatItem> renderer = null;

            @Override
            public @Nullable BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null) {
                    this.renderer = new GeoItemRenderer<>(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mad_hat")));
                }
                return this.renderer;
            }
        });
    }


    @Override
    public boolean canEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}

