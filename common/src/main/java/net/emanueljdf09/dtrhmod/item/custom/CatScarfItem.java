package net.emanueljdf09.dtrhmod.item.custom;

import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.slot.SlotReference;
import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class CatScarfItem extends Item implements GeoItem, Accessory {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation MOVING = RawAnimation.begin().thenLoop("moving");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public CatScarfItem(Properties settings) {
        super(settings);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoItemRenderer<CatScarfItem> renderer = null;

            @Override
            public @Nullable BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null) {
                    this.renderer = new GeoItemRenderer<>(new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "cat_scarf")));
                }
                return this.renderer;
            }
        });
    }

    @Override
    public boolean canEquip(ItemStack stack, SlotReference reference) {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 20, state -> {
            Entity entity = state.getData(DataTickets.ENTITY);
            if (entity instanceof LivingEntity living) {
                boolean isMoving = living.xxa != 0.0f || living.zza != 0.0f;

                if (isMoving) {
                    state.setAnimation(MOVING);
                    return PlayState.CONTINUE;
                }
            }

            state.setAnimation(IDLE);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        if (!world.isClientSide() && entity instanceof LivingEntity living) {
            if (living.isShiftKeyDown()) {
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}