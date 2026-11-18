package net.emanueljdf09.dtrhmod.block.custom;

import net.emanueljdf09.dtrhmod.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import java.util.List;


public class WonderGrassBlock extends GrassBlock {
    public WonderGrassBlock(Properties settings) {
        super(settings);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (!canBeGrass(state, world, pos)) {
            world.setBlockAndUpdate(pos, ModBlocks.WONDER_DIRT.defaultBlockState());
        } else {
            if (world.getMaxLocalRawBrightness(pos.above()) >= 9) {
                for (int i = 0; i < 4; ++i) {
                    BlockPos blockPos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);

                    if (world.getBlockState(blockPos).is(ModBlocks.WONDER_DIRT) && canPropagate(state, world, blockPos)) {
                        world.setBlockAndUpdate(blockPos, this.defaultBlockState());
                    }
                }
            }
        }
    }

    private static boolean canBeGrass(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos blockPos = pos.above();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.is(Blocks.SNOW) && (Integer)blockState.getValue(SnowLayerBlock.LAYERS) == 1) {
            return true;
        } else if (blockState.getFluidState().getAmount() == 8) {
            return false;
        } else {
            int i = LightEngine.getLightBlockInto(world, state, pos, blockState, blockPos, Direction.UP, blockState.getLightBlock(world, blockPos));
            return i < world.getMaxLightLevel();
        }
    }

    private static boolean canPropagate(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos blockPos = pos.above();
        return canBeGrass(state, world, pos) && !world.getFluidState(blockPos).is(FluidTags.WATER);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        ItemStack tool = builder.getParameter(LootContextParams.TOOL);
        ServerLevel world = builder.getLevel();

        if (tool != null && world != null) {
            var silkTouchEntry = world.registryAccess()
                    .registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                    .getHolder(Enchantments.SILK_TOUCH);

            if (silkTouchEntry.isPresent() && EnchantmentHelper.getItemEnchantmentLevel(silkTouchEntry.get(), tool) > 0) {
                return List.of(new ItemStack(this));
            }
        }

        return List.of(new ItemStack(ModBlocks.WONDER_DIRT));
    }

}
