package net.emanueljdf09.dtrhmod.block.entity;

import net.emanueljdf09.dtrhmod.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ModSignBlockEntity extends SignBlockEntity {
    public ModSignBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.SIGN_BLOCK_ENTITIES.get(), blockPos, blockState);
    }
}