package net.emanueljdf09.dtrhmod.world.structures.pieces;

import net.emanueljdf09.dtrhmod.world.structures.ModStructurePieces;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class MazeStructurePiece extends TemplateStructurePiece {

    public MazeStructurePiece(StructureTemplateManager manager, ResourceLocation templateId, BlockPos pos, Rotation rotation) {
        super(ModStructurePieces.MAZE_STRUCTURE_PIECE_TYPE, 0, manager, templateId, templateId.toString(), createPlacementData(rotation), pos);
    }

    public MazeStructurePiece(StructurePieceSerializationContext context, CompoundTag nbt) {
        super(
                ModStructurePieces.MAZE_STRUCTURE_PIECE_TYPE,
                nbt,
                context.structureTemplateManager(),
                id -> createPlacementData(
                        nbt.contains("Rot")
                                ? Rotation.valueOf(nbt.getString("Rot"))
                                : Rotation.NONE
                )
        );
    }

    private static StructurePlaceSettings createPlacementData(Rotation rotation) {
        return new StructurePlaceSettings()
                .setRotation(rotation)
                .setMirror(Mirror.NONE);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {
        super.addAdditionalSaveData(context, nbt);
        nbt.putString("Rot", this.placeSettings.getRotation().name());
    }

    @Override
    public void postProcess(WorldGenLevel world, StructureManager structureAccessor, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        super.postProcess(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pivot);
    }

    @Override
    protected void handleDataMarker(String metadata, BlockPos pos, ServerLevelAccessor world, RandomSource random, BoundingBox boundingBox) {

    }
}
