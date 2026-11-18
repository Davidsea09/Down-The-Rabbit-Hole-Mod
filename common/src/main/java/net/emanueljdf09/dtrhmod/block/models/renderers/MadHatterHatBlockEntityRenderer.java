package net.emanueljdf09.dtrhmod.block.models.renderers;

import net.emanueljdf09.dtrhmod.block.entity.MadHatterHatBlockEntity;
import net.emanueljdf09.dtrhmod.block.models.MadHatterHatModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class MadHatterHatBlockEntityRenderer extends GeoBlockRenderer<MadHatterHatBlockEntity> {

    public MadHatterHatBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new MadHatterHatModel());
    }
}
