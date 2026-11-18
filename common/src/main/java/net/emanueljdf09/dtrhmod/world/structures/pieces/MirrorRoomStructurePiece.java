package net.emanueljdf09.dtrhmod.world.structures.pieces;

import net.emanueljdf09.dtrhmod.block.ModBlocks;
import net.emanueljdf09.dtrhmod.block.custom.mirror.MirrorBlock;
import net.emanueljdf09.dtrhmod.block.custom.mirror.MirrorType;
import net.emanueljdf09.dtrhmod.world.structures.ModStructurePieces;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class MirrorRoomStructurePiece extends TemplateStructurePiece {

    public MirrorRoomStructurePiece(StructureTemplateManager manager, ResourceLocation templateId, BlockPos pos, Rotation rotation) {
        super(ModStructurePieces.MIRROR_ROOM_STRUCTURE_PIECE_TYPE, 0, manager, templateId, templateId.toString(), createPlacementData(rotation), pos);
    }

    public MirrorRoomStructurePiece(StructurePieceSerializationContext context, CompoundTag nbt) {
        super(ModStructurePieces.MIRROR_ROOM_STRUCTURE_PIECE_TYPE, nbt, context.structureTemplateManager(), (id) -> createPlacementData(Rotation.valueOf(nbt.getString("Rot"))));
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
    protected void handleDataMarker(String metadata, BlockPos pos, ServerLevelAccessor world, RandomSource random, BoundingBox boundingBox) {
    }

    @Override
    public void postProcess(WorldGenLevel world, StructureManager structures, ChunkGenerator generator, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pivot) {

        super.postProcess(world, structures, generator, random, box, chunkPos, pivot);

         BlockPos.betweenClosed(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ()).forEach(currentPos -> {
            BlockState state = world.getBlockState(currentPos);

            if (state.is(ModBlocks.MIRROR_BLOCK)) {
                if (state.getValue(MirrorBlock.TYPE) != MirrorType.structure) {

                    BlockState repairedState = state.setValue(MirrorBlock.TYPE, MirrorType.structure);

                    world.setBlock(currentPos, repairedState, 2);
                }
            }
        });
    }

}
