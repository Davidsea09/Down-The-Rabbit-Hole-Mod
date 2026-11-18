package net.emanueljdf09.dtrhmod.block.entity;

import net.emanueljdf09.dtrhmod.block.ModBlockEntities;
import net.emanueljdf09.dtrhmod.block.custom.head.ModHeadBlock;
import net.emanueljdf09.dtrhmod.block.custom.head.ModHeadType;
import net.emanueljdf09.dtrhmod.block.custom.head.ModWallHeadBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ModHeadBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);


    public ModHeadBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MOD_HEAD_BE.get(), pos, state);
    }

    public ModHeadType getHeadType() {
        if (this.getBlockState().getBlock() instanceof ModHeadBlock floorHead) {
            return floorHead.getHeadType();
        } else if (this.getBlockState().getBlock() instanceof ModWallHeadBlock wallHead) {
            return wallHead.getHeadType();
        }
        return ModHeadType.MAD_HATTER;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
