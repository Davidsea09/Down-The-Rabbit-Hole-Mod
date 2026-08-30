package net.emanueljdf09.dtrhmod.block.models.renderers;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.block.custom.head.ModHeadBlock;
import net.emanueljdf09.dtrhmod.block.custom.head.ModWallHeadBlock;
import net.emanueljdf09.dtrhmod.block.entity.ModHeadBlockEntity;
import net.emanueljdf09.dtrhmod.block.models.ModHeadBlockModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.util.math.Direction;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationPropertyHelper;
import org.joml.Quaternionf;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ModHeadBlockEntityRenderer extends GeoBlockRenderer<ModHeadBlockEntity> {
    public ModHeadBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(new ModHeadBlockModel());
    }
}