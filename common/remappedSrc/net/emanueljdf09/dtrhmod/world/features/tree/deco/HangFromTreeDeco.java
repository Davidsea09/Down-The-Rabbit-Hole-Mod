package net.emanueljdf09.dtrhmod.world.features.tree.deco;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.emanueljdf09.dtrhmod.fabric.world.features.tree.deco.ModTreeDeco;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class HangFromTreeDeco extends TreeDecorator {

    public static final MapCodec<HangFromTreeDeco> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.intRange(0, 32).fieldOf("attempts_minimum").forGetter(o -> o.count),
                    Codec.intRange(0, 32).fieldOf("random_add_attempts").orElse(0).forGetter(o -> o.randomAddCount),
                    Codec.intRange(1, 24).fieldOf("minimum_required_length").forGetter(o -> o.minimumRequiredLength),
                    Codec.intRange(1, 24).fieldOf("base_length").forGetter(o -> o.baseLength),
                    Codec.intRange(0, 16).fieldOf("random_add_length").orElse(0).forGetter(o -> o.randomAddLength),
                    BlockStateProvider.CODEC.fieldOf("rope_provider").forGetter(o -> o.ropeProvider),
                    BlockStateProvider.CODEC.fieldOf("baggage_provider").forGetter(o -> o.baggageProvider)
            ).apply(instance, HangFromTreeDeco::new)
    );

    private final int count;
    private final int randomAddCount;
    private final int minimumRequiredLength;
    private final int baseLength;
    private final int randomAddLength;
    private final BlockStateProvider ropeProvider;
    private final BlockStateProvider baggageProvider;

    public HangFromTreeDeco(int count, int randomAddCount, int minimumRequiredLength, int baseLength, int randomAddLength, BlockStateProvider ropeProvider, BlockStateProvider baggageProvider) {
        this.count = count;
        this.randomAddCount = randomAddCount;
        this.minimumRequiredLength = minimumRequiredLength;
        this.baseLength = baseLength;
        this.randomAddLength = randomAddLength;
        this.ropeProvider = ropeProvider;
        this.baggageProvider = baggageProvider;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return net.emanueljdf09.dtrhmod.world.features.tree.deco.ModTreeDeco.HANG_FROM_TREE_DECORATOR;
    }

    @Override
    public void place(Context generator) {
        if (generator.leaves().isEmpty()) return;

        RandomSource random = generator.random();
        int totalTries = this.count + random.nextInt(this.randomAddCount + 1);
        int leafTotal = generator.leaves().size();
        totalTries = Math.min(totalTries, leafTotal);

        for (int attempt = 0; attempt < totalTries; attempt++) {
            boolean clearedOfPossibleLeaves = false;
            BlockPos pos = generator.leaves().get(random.nextInt(leafTotal));

            if (!generator.logs().isEmpty() &&
                    pos.getX() == generator.logs().get(0).getX() &&
                    pos.getZ() == generator.logs().get(0).getZ()) {
                continue;
            }

            int cordLength = this.baseLength + random.nextInt(this.randomAddLength + 1);
            int actualRopeLength = 0;

            for (int ropeUnrolling = 1; ropeUnrolling <= cordLength; ropeUnrolling++) {
                BlockPos checkPos = pos.below(ropeUnrolling);
                boolean isAir = generator.isAir(checkPos);

                if (!clearedOfPossibleLeaves && isAir) {
                    clearedOfPossibleLeaves = true;
                }

                if (clearedOfPossibleLeaves && !isAir) {
                    actualRopeLength = ropeUnrolling - 1;
                    break;
                }

                if (ropeUnrolling == cordLength && isAir) {
                    actualRopeLength = cordLength;
                }
            }

            if (actualRopeLength >= this.minimumRequiredLength) {
                BlockPos currentPos = pos;

                for (int i = 1; i <= actualRopeLength; i++) {
                    currentPos = currentPos.below();
                    BlockState ropeState = this.ropeProvider.getState(random, currentPos);
                    generator.setBlock(currentPos, ropeState);
                }

                BlockPos baggagePos = currentPos.below();
                if (generator.isAir(baggagePos)) {
                    BlockState baggageState = this.baggageProvider.getState(random, baggagePos);
                    generator.setBlock(baggagePos, baggageState);
                }
            }
        }
    }
}