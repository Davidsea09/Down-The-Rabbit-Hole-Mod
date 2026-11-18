package net.emanueljdf09.dtrhmod.world.features.biome;

import com.mojang.serialization.Codec;
import net.emanueljdf09.dtrhmod.fabric.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class TulgeyPathFeature extends Feature<NoneFeatureConfiguration> {
    public TulgeyPathFeature(Codec<NoneFeatureConfiguration> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel world = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        int pathLength = 8 + random.nextInt(8);
        BlockPos.MutableBlockPos currentPos = origin.mutable();

        int[][] directions = { {0, -1}, {0, 1}, {1, 0}, {-1, 0} };

        for (int i = 0; i < pathLength; i++) {

            int surfaceY = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, currentPos).getY();
            BlockPos targetPos = new BlockPos(currentPos.getX(), surfaceY - 1, currentPos.getZ());


            if (world.getBlockState(targetPos).is(ModBlocks.WONDER_GRASS) || world.getBlockState(targetPos).is(Blocks.GRASS_BLOCK)) {

                world.setBlock(targetPos, ModBlocks.WONDER_DIRT_PATH.getDefaultState(), 3);
            }

            if (i == pathLength - 1) {
                BlockPos signPos = targetPos.above();
                if (world.getBlockState(signPos).isAir()) {
                    world.setBlock(signPos, Blocks.OAK_SIGN.defaultBlockState(), 3);
                }
            }

            int[] dir = directions[random.nextInt(directions.length)];
            currentPos.move(dir[0], 0, dir[1]);
        }

        return true;
    }
}