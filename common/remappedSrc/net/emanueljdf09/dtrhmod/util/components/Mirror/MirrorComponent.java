package net.emanueljdf09.dtrhmod.util.components.Mirror;

import org.ladysnake.cca.api.v3.component.Component;

import java.util.Optional;
import net.minecraft.core.BlockPos;

public interface MirrorComponent extends Component {

    Optional<BlockPos> getDestination(BlockPos sourcePos);

    void linkMirrors(BlockPos posA, BlockPos posB);

    void removeLink(BlockPos pos);

}
