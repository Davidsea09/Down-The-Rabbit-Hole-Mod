package net.emanueljdf09.dtrhmod.block.entity;

import net.emanueljdf09.dtrhmod.block.ModBlockEntities;
import net.emanueljdf09.dtrhmod.item.ModItems;
import net.emanueljdf09.dtrhmod.util.ClientAnimationHelper;
import net.emanueljdf09.dtrhmod.util.components.ClientProgression;
import net.emanueljdf09.dtrhmod.util.ModEffects;
import net.emanueljdf09.dtrhmod.util.components.PlayerProgressionManager;
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
        super(ModBlockEntities.EXTERIOR_CHEST_ENTITY.get(), pos, state);
    }

    public InteractionResult onUse(Player player, InteractionHand hand) {
        assert level != null;
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        ProgressionComponent component;
        if (level.isClientSide()) {
            component = ClientProgression.get();
        } else if (player instanceof ServerPlayer serverPlayer && serverPlayer.getServer() != null) {
            component = PlayerProgressionManager.get(serverPlayer.getServer()).get(serverPlayer.getUUID());
        } else {
            return InteractionResult.PASS;
        }

        if (component.hasOpenedExtGrownChest()) {
            return InteractionResult.PASS;
        }

        if (!component.hasOpenedExtChest()) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("exterior.player.openchest"), true);
                player.addItem(ModItems.EAT_ME.get().getDefaultInstance());
                component.setOpenedExtChest(true);

                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                setChanged();
            }
            return InteractionResult.SUCCESS;
        }

        if (!component.hasOpenedExtGrownChest() && player.hasEffect(ModEffects.GROW)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("exterior.player.opengrownchest"), true);
                player.addItem(ModItems.DRINK_ME.get().getDefaultInstance());
                player.addItem(ModItems.EXTERIOR_KEY.get().getDefaultInstance());
                component.setOpenedExtGrownChest(true);

                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
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