package net.emanueljdf09.dtrhmod.world.structures;


import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.fabric.world.structures.structure.LargeMazeStructure;
import net.emanueljdf09.dtrhmod.fabric.world.structures.structure.MediumMazeStructure;
import net.emanueljdf09.dtrhmod.fabric.world.structures.structure.MirrorRoomStructure;
import net.emanueljdf09.dtrhmod.fabric.world.structures.structure.SmallMazeStructure;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.StructureType;

public class ModStructures {
    public static StructureType<MirrorRoomStructure> MIRROR_ROOM_STRUCTURE_TYPE;
    public static StructureType<LargeMazeStructure> LARGE_MAZE_STRUCTURE_TYPE;
    public static StructureType<MediumMazeStructure> MEDIUM_MAZE_STRUCTURE_TYPE;
    public static StructureType<SmallMazeStructure> SMALL_MAZE_STRUCTURE_TYPE;

    public static void registerStructures() {
        MIRROR_ROOM_STRUCTURE_TYPE = () -> MirrorRoomStructure.CODEC;

        Registry.register(BuiltInRegistries.STRUCTURE_TYPE,
                ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID,"mirror_room"),
                MIRROR_ROOM_STRUCTURE_TYPE);


        LARGE_MAZE_STRUCTURE_TYPE = () -> LargeMazeStructure.CODEC;

        Registry.register(BuiltInRegistries.STRUCTURE_TYPE,
                ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "large_maze"),
                LARGE_MAZE_STRUCTURE_TYPE);

        MEDIUM_MAZE_STRUCTURE_TYPE = () -> MediumMazeStructure.CODEC;

        Registry.register(BuiltInRegistries.STRUCTURE_TYPE,
                ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "medium_maze"),
                MEDIUM_MAZE_STRUCTURE_TYPE);

        SMALL_MAZE_STRUCTURE_TYPE = () -> SmallMazeStructure.CODEC;

        Registry.register(BuiltInRegistries.STRUCTURE_TYPE,
                ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "small_maze"),
                SMALL_MAZE_STRUCTURE_TYPE);
    }
}
