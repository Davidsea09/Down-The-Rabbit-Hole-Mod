package net.emanueljdf09.dtrhmod.block.models.renderers;

import net.emanueljdf09.dtrhmod.fabric.block.entity.ExteriorDoorEntity;
import net.emanueljdf09.dtrhmod.fabric.block.models.ExteriorDoorModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;


public class ExteriorDoorRenderer extends GeoBlockRenderer<ExteriorDoorEntity> {

    public ExteriorDoorRenderer(BlockEntityRendererProvider.Context context) {
        super(new ExteriorDoorModel());
    }

}
