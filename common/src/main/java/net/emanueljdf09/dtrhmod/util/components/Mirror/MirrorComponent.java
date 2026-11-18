package net.emanueljdf09.dtrhmod.util.components.Mirror;

import java.util.Optional;
import net.minecraft.core.BlockPos;

public interface MirrorComponent {

    Optional<BlockPos> getDestination(BlockPos sourcePos);

    void linkMirrors(BlockPos posA, BlockPos posB);

    void removeLink(BlockPos pos);

}
