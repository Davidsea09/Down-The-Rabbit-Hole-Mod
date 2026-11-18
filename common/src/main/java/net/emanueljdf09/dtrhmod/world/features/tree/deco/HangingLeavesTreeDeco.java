package net.emanueljdf09.dtrhmod.world.features.tree.deco;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.emanueljdf09.dtrhmod.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class HangingLeavesTreeDeco extends TreeDecorator {

    public static final MapCodec<HangingLeavesTreeDeco> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.FLOAT.fieldOf("probability").forGetter(decorator -> decorator.probability),
                            Codec.INT.fieldOf("min_length").forGetter(decorator -> decorator.minLength),
                            Codec.INT.fieldOf("max_length").forGetter(decorator -> decorator.maxLength))
                    .apply(instance, HangingLeavesTreeDeco::new));

    private final float probability;
    private final int minLength;
    private final int maxLength;

    public HangingLeavesTreeDeco(float probability, int minLength, int maxLength) {
        this.probability = probability;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return net.emanueljdf09.dtrhmod.world.features.tree.deco.ModTreeDeco.HANGING_LEAVES_DECORATOR;
    }

    @Override
    public void place(Context generator) {
        generator.leaves().forEach(pos -> {
            if (generator.random().nextFloat() < this.probability) {

                int length = Mth.nextInt(generator.random(), this.minLength, this.maxLength);

                BlockPos.MutableBlockPos currentPos = pos.below().mutable();

                for (int i = 0; i < length; i++) {
                    if (generator.isAir(currentPos)) {
                        boolean isTip = (i == length - 1);

                        BlockState state;
                        if (isTip) {
                            int age = generator.random().nextInt(15);
                            state = ModBlocks.WW_HANGING_LEAVES.get().defaultBlockState()
                                    .setValue(GrowingPlantHeadBlock.AGE, age);
                        } else {
                            state = ModBlocks.WW_HANGING_LEAVES_PLANT.get().defaultBlockState();
                        }

                        generator.setBlock(currentPos, state);
                        currentPos.move(Direction.DOWN);
                    } else {
                        break;
                    }
                }
            }
        });
    }
}