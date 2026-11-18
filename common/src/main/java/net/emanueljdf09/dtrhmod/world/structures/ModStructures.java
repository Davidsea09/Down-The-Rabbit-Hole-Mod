package net.emanueljdf09.dtrhmod.world.structures;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.world.structures.structure.LargeMazeStructure;
import net.emanueljdf09.dtrhmod.world.structures.structure.MediumMazeStructure;
import net.emanueljdf09.dtrhmod.world.structures.structure.MirrorRoomStructure;
import net.emanueljdf09.dtrhmod.world.structures.structure.SmallMazeStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;

public class ModStructures {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(DownTheRabbitHole.MOD_ID, Registries.STRUCTURE_TYPE);

    public static final RegistrySupplier<StructureType<MirrorRoomStructure>> MIRROR_ROOM_STRUCTURE_TYPE =
            STRUCTURE_TYPES.register("mirror_room", () -> () -> MirrorRoomStructure.CODEC);

    public static final RegistrySupplier<StructureType<LargeMazeStructure>> LARGE_MAZE_STRUCTURE_TYPE =
            STRUCTURE_TYPES.register("large_maze", () -> () -> LargeMazeStructure.CODEC);

    public static final RegistrySupplier<StructureType<MediumMazeStructure>> MEDIUM_MAZE_STRUCTURE_TYPE =
            STRUCTURE_TYPES.register("medium_maze", () -> () -> MediumMazeStructure.CODEC);

    public static final RegistrySupplier<StructureType<SmallMazeStructure>> SMALL_MAZE_STRUCTURE_TYPE =
            STRUCTURE_TYPES.register("small_maze", () -> () -> SmallMazeStructure.CODEC);

    public static void registerStructures() {
        STRUCTURE_TYPES.register();
        DownTheRabbitHole.LOGGER.info("Registering Structures for " + DownTheRabbitHole.MOD_ID);
    }
}