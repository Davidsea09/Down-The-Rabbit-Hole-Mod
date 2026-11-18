package net.emanueljdf09.dtrhmod.world.features.tree.deco;

import com.mojang.serialization.MapCodec;
import net.emanueljdf09.dtrhmod.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class WwTreeDecorator extends TreeDecorator {
    public static final MapCodec<WwTreeDecorator> CODEC =
            MapCodec.unit(WwTreeDecorator::new);

    @Override
    protected TreeDecoratorType<?> type() {
        return net.emanueljdf09.dtrhmod.world.features.tree.deco.ModTreeDeco.WW_TREE_DECORATOR;
    }

    @Override
    public void place(Context generator) {
        RandomSource random = generator.random();

        for (BlockPos pos : generator.leaves()) {

            if (random.nextFloat() < 0.35f) {
                BlockPos currentPos = pos.below();

               int strandLength = random.nextIntBetweenInclusive(2, 4);

                for (int i = 0; i < strandLength; i++) {
                    if (generator.isAir(currentPos)) {
                        boolean isBottomTip = (i == strandLength - 1);

                        BlockState blockStateToPlace = isBottomTip
                                ? ModBlocks.WW_HANGING_LEAVES.get().defaultBlockState()
                                : ModBlocks.WW_HANGING_LEAVES_PLANT.get().defaultBlockState();

                        generator.setBlock(currentPos, blockStateToPlace);
                        currentPos = currentPos.below();
                    } else {
                        break;
                    }
                }
            }
        }
    }
}
