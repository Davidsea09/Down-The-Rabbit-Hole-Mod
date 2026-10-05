package net.emanueljdf09.dtrhmod.world.features.biome;

import com.mojang.serialization.Codec;
import net.emanueljdf09.dtrhmod.block.ModBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class TulgeyPathFeature extends Feature<DefaultFeatureConfig> {
    public TulgeyPathFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        Random random = context.getRandom();

        int pathLength = 8 + random.nextInt(8);
        BlockPos.Mutable currentPos = origin.mutableCopy();

        int[][] directions = { {0, -1}, {0, 1}, {1, 0}, {-1, 0} };

        for (int i = 0; i < pathLength; i++) {

            int surfaceY = world.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, currentPos).getY();
            BlockPos targetPos = new BlockPos(currentPos.getX(), surfaceY - 1, currentPos.getZ());


            if (world.getBlockState(targetPos).isOf(ModBlocks.WONDER_GRASS) || world.getBlockState(targetPos).isOf(Blocks.GRASS_BLOCK)) {

                world.setBlockState(targetPos, ModBlocks.WONDER_DIRT_PATH.getDefaultState(), 3);
            }

            if (i == pathLength - 1) {
                BlockPos signPos = targetPos.up();
                if (world.getBlockState(signPos).isAir()) {
                    world.setBlockState(signPos, Blocks.OAK_SIGN.getDefaultState(), 3);
                }
            }

            int[] dir = directions[random.nextInt(directions.length)];
            currentPos.move(dir[0], 0, dir[1]);
        }

        return true;
    }
}