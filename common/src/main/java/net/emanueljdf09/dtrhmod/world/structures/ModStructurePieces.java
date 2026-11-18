package net.emanueljdf09.dtrhmod.world.structures;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.world.structures.pieces.MazeStructurePiece;
import net.emanueljdf09.dtrhmod.world.structures.pieces.MirrorRoomStructurePiece;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class ModStructurePieces {
    public static final StructurePieceType MIRROR_ROOM_STRUCTURE_PIECE_TYPE = MirrorRoomStructurePiece::new;
    public static final StructurePieceType MAZE_STRUCTURE_PIECE_TYPE = MazeStructurePiece::new;

    public static void register() {
        Registry.register(BuiltInRegistries.STRUCTURE_PIECE, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mirror_room_piece"), MIRROR_ROOM_STRUCTURE_PIECE_TYPE);
        Registry.register(BuiltInRegistries.STRUCTURE_PIECE, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "maze_piece"), MAZE_STRUCTURE_PIECE_TYPE);
    }
}
