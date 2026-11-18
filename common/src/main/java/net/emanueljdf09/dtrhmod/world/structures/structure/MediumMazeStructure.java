package net.emanueljdf09.dtrhmod.world.structures.structure;

import com.mojang.serialization.MapCodec;
import net.emanueljdf09.dtrhmod.world.structures.ModStructures;
import net.emanueljdf09.dtrhmod.world.structures.maze.MazeLayoutGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import java.util.Optional;

import static net.emanueljdf09.dtrhmod.world.structures.maze.MazeLayoutGenerator.MODULE_SIZE;

public class MediumMazeStructure extends Structure {
    public static final MapCodec<MediumMazeStructure> CODEC = simpleCodec(MediumMazeStructure::new);

    public MediumMazeStructure(StructureSettings config) {
        super(config);
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        return Optional.of(new GenerationStub(context.chunkPos().getWorldPosition(), collector -> {
            StructureTemplateManager templateManager = context.structureTemplateManager();
            BlockPos centerPos = context.chunkPos().getMiddleBlockPosition(0);
            RandomState noiseConfig = context.randomState();

            int surfaceY = context.chunkGenerator().getBaseHeight(centerPos.getX(), centerPos.getZ(),
                    Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), noiseConfig);

            int dimension = MazeLayoutGenerator.MazeSize.MEDIUM.getDimension();
            int totalWidthBlocks = dimension * MODULE_SIZE;

            BlockPos originPos = new BlockPos(
                    centerPos.getX() - (totalWidthBlocks / 2),
                    surfaceY - 1,
                    centerPos.getZ() - (totalWidthBlocks / 2));
            MazeLayoutGenerator.generateAndPlaceMaze(collector, templateManager, originPos, context.random(), MazeLayoutGenerator.MazeSize.MEDIUM);
        }));
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.MEDIUM_MAZE_STRUCTURE_TYPE.get();
    }
}
