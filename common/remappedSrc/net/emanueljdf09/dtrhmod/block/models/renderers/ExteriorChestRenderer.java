package net.emanueljdf09.dtrhmod.block.models.renderers;

import net.emanueljdf09.dtrhmod.fabric.block.entity.ExteriorChestEntity;
import net.emanueljdf09.dtrhmod.fabric.block.models.ExteriorChestModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ExteriorChestRenderer extends GeoBlockRenderer<ExteriorChestEntity> {

    public ExteriorChestRenderer(BlockEntityRendererProvider.Context context) {
        super(new ExteriorChestModel());
    }

}
