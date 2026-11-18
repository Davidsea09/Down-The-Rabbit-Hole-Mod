package net.emanueljdf09.dtrhmod.block.entity;

import net.emanueljdf09.dtrhmod.block.ModBlockEntities;
import net.emanueljdf09.dtrhmod.item.ModItems;
import net.emanueljdf09.dtrhmod.util.ClientAnimationHelper;
import net.emanueljdf09.dtrhmod.util.ModComponents;
import net.emanueljdf09.dtrhmod.util.ModEffects;
import net.emanueljdf09.dtrhmod.util.components.ProgressionComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ExteriorDoorEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.doorhandle.idle");
    private static final RawAnimation OPENING = RawAnimation.begin().thenPlay("animation.doorhandle.opening").thenLoop("animation.doorhandle.open_idle");
    private static final RawAnimation FAIL = RawAnimation.begin().thenPlay("animation.doorhandle.fail").thenLoop("animation.doorhandle.idle");
    private static final RawAnimation OPEN_IDLE = RawAnimation.begin().thenLoop("animation.doorhandle.open_idle");

    public ExteriorDoorEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EXTERIOR_DOOR_ENTITY, pos, state);
    }

    public InteractionResult onUse(Player player, InteractionHand hand) {
        assert level != null;

        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        ProgressionComponent component = ModComponents.PROGRESSION_COMPONENT.get(player);
        ItemStack stack = player.getItemInHand(hand);

        if (component.hasOpenedExtDoor()) {
            return InteractionResult.PASS;
        }

        if (stack.is(ModItems.EXTERIOR_KEY) && player.hasEffect(ModEffects.SHRINK)) {
            if (!level.isClientSide) {
                component.setOpenedExtDoor(true);
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
                player.displayClientMessage(Component.translatable("exterior.player.opendoor"), false);

                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            } else {
                triggerAnim("door_controller", "opening");
            }
            return InteractionResult.SUCCESS;
        }

        else {
            if (!level.isClientSide) {
                if (!component.hasOpenedExtGrownChest()) {
                    player.displayClientMessage(Component.translatable("exterior.player.faileddoorgrowth"), false);
                } else {
                    player.displayClientMessage(Component.translatable("exterior.player.faileddoor"), false);
                }
            } else {
                triggerAnim("door_controller", "fail");
            }
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "door_controller", 0, state -> {
            if (this.getLevel() != null && this.getLevel().isClientSide()) {
                return ClientAnimationHelper.handleDoorAnimation(state, IDLE, OPEN_IDLE);
            }
            return state.setAndContinue(IDLE);
        })
                .triggerableAnim("opening", OPENING)
                .triggerableAnim("fail", FAIL));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}


