package net.emanueljdf09.dtrhmod.block.entity;

import net.emanueljdf09.dtrhmod.block.ModBlockEntities;
import net.emanueljdf09.dtrhmod.item.ModItems;
import net.emanueljdf09.dtrhmod.util.ClientAnimationHelper;
import net.emanueljdf09.dtrhmod.util.ModComponents;
import net.emanueljdf09.dtrhmod.util.ModEffects;
import net.emanueljdf09.dtrhmod.util.components.ProgressionComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ExteriorChestEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final RawAnimation CLOSED = RawAnimation.begin().thenPlay("closed");
    public static final RawAnimation OPEN_NORMAL = RawAnimation.begin().thenPlay("opening_normal").thenLoop("open_empty");
    public static final RawAnimation OPEN_AND_GROW_SEQUENCE = RawAnimation.begin()
            .thenPlay("growing")
            .thenLoop("open_grown");
    public static final RawAnimation TAKE_GROWN_ITEMS = RawAnimation.begin().thenPlay("take_grown_items").thenLoop("invisible");

    public ExteriorChestEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EXTERIOR_CHEST_ENTITY, pos, state);
    }

    public InteractionResult onUse(Player player, InteractionHand hand) {
        assert level != null;
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        ProgressionComponent component = ModComponents.PROGRESSION_COMPONENT.get(player);

        if (component.hasOpenedExtGrownChest()) {
            return InteractionResult.PASS;
        }

        if (!component.hasOpenedExtChest()) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("exterior.player.openchest"), true);
                player.addItem(ModItems.EAT_ME.getDefaultInstance());
                component.setOpenedExtChest(true);

                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                ModComponents.PROGRESSION_COMPONENT.sync((ServerPlayer) player);
                setChanged();
            }
            return InteractionResult.SUCCESS;
        }

        if (!component.hasOpenedExtGrownChest() && player.hasEffect(ModEffects.GROW)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("exterior.player.opengrownchest"), true);
                player.addItem(ModItems.DRINK_ME.getDefaultInstance());
                player.addItem(ModItems.EXTERIOR_KEY.getDefaultInstance());
                component.setOpenedExtGrownChest(true);

                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                ModComponents.PROGRESSION_COMPONENT.sync((ServerPlayer) player);
                setChanged();
            } else {
                triggerAnim("chest_controller", "take_grown_items");
            }
            return InteractionResult.SUCCESS;
        }

        if (!component.hasOpenedExtGrownChest() && !player.hasEffect(ModEffects.GROW)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("exterior.player.failchestGrow"), true);
            }
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide) {
            player.displayClientMessage(Component.translatable("exterior.player.failchest"), true);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "chest_controller", 0, state -> {
            if (this.getLevel() != null && this.getLevel().isClientSide()) {
                return ClientAnimationHelper.handleChestAnimation(state, CLOSED, OPEN_NORMAL, TAKE_GROWN_ITEMS, OPEN_AND_GROW_SEQUENCE);
            }
            return state.setAndContinue(CLOSED);
        })
                .triggerableAnim("open_and_grow", OPEN_AND_GROW_SEQUENCE)
                .triggerableAnim("take_grown_items", TAKE_GROWN_ITEMS));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}