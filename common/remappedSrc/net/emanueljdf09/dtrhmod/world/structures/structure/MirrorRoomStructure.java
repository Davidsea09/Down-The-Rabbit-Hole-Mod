package net.emanueljdf09.dtrhmod.world.structures.structure;

import com.mojang.serialization.MapCodec;
import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.fabric.world.structures.ModStructures;
import net.emanueljdf09.dtrhmod.fabric.world.structures.pieces.MirrorRoomStructurePiece;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import java.util.Optional;

public class MirrorRoomStructure extends Structure {
    public static final MapCodec<MirrorRoomStructure> CODEC = simpleCodec(MirrorRoomStructure::new);

    public MirrorRoomStructure(StructureSettings config) {
        super(config);
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        return Optional.of(new GenerationStub(context.chunkPos().getWorldPosition(), collector -> {
            addPieces(collector, context);
        }));
    }

    private static void addPieces(StructurePiecesBuilder collector, GenerationContext context) {
        StructureTemplateManager templateManager = context.structureTemplateManager();
        BlockPos centerPos = context.chunkPos().getMiddleBlockPosition(0);
        RandomState noiseConfig = context.randomState();

        int solidFloorY = context.chunkGenerator().getBaseHeight(centerPos.getX(), centerPos.getZ(),
                Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), noiseConfig);

        int waterSurfaceY = context.chunkGenerator().getBaseHeight(centerPos.getX(), centerPos.getZ(),
                Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), noiseConfig);

        if ((solidFloorY - waterSurfaceY) > 4) {
            return;
        }

        var biomeRegistryEntry = context.biomeSource().possibleBiomes().stream().findFirst();
        boolean isOverworld = true;
        if (biomeRegistryEntry.isPresent()) {
            String namespace = biomeRegistryEntry.get().unwrapKey().map(key -> key.location().getNamespace()).orElse("minecraft");
            isOverworld = !namespace.equalsIgnoreCase(DownTheRabbitHole.MOD_ID);
        }

        ResourceLocation templateId = isOverworld
                ? ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "portal/mirror_room_ruined")
                : ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "portal/mirror_room_complete");

        BlockPos placementPos = new BlockPos(centerPos.getX(), solidFloorY, centerPos.getZ());
        Rotation rotation = Rotation.getRandom(context.random());

        collector.addPiece(new MirrorRoomStructurePiece(templateManager, templateId, placementPos, rotation));
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.MIRROR_ROOM_STRUCTURE_TYPE;
    }
}