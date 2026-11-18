package net.emanueljdf09.dtrhmod.block.entity;

import net.emanueljdf09.dtrhmod.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ModHangingSignBlockEntity extends SignBlockEntity {

    public ModHangingSignBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.HANGING_SIGN_BLOCK_ENTITIES.get(), blockPos, blockState);
    }

    @Override
    public int getTextLineHeight() {
        return 9;
    }

    @Override
    public int getMaxTextLineWidth() {
        return 60;
    }

    @Override
    public SoundEvent getSignInteractionFailedSoundEvent() {
        return SoundEvents.WAXED_HANGING_SIGN_INTERACT_FAIL;
    }
}
