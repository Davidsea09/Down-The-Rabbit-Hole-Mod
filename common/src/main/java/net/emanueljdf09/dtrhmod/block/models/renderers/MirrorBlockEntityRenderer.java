package net.emanueljdf09.dtrhmod.block.models.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.block.ModBlocks;
import net.emanueljdf09.dtrhmod.block.custom.mirror.MirrorBlock;
import net.emanueljdf09.dtrhmod.block.custom.mirror.MirrorType;
import net.emanueljdf09.dtrhmod.block.entity.MirrorBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class MirrorBlockEntityRenderer implements BlockEntityRenderer<MirrorBlockEntity> {
    private static final ResourceLocation WONDERLAND_PORTAL = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/block/mirror/mirror_portal_animated.png");

    private final BlockRenderDispatcher renderManager;

    public MirrorBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.renderManager = ctx.getBlockRenderDispatcher();
    }

    @Override
    public void render(MirrorBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        BlockState state = entity.getBlockState();
        if (!state.is(ModBlocks.MIRROR_BLOCK)) return;

        MirrorType type = state.getValue(MirrorBlock.TYPE);
        if (type != MirrorType.wonderland && type != MirrorType.structure) return;

        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        float overlayAlpha = 0.0f;
        ResourceLocation textureToUse = WONDERLAND_PORTAL;

        double pX = client.player.getX();
        double pY = client.player.getEyeY();
        double pZ = client.player.getZ();
        double mX = entity.getBlockPos().getX() + 0.5;
        double mY = entity.getBlockPos().getY() + 1.0;
        double mZ = entity.getBlockPos().getZ() + 0.5;
        double distance = Math.sqrt(Math.pow(pX - mX, 2) + Math.pow(pY - mY, 2) + Math.pow(pZ - mZ, 2));

        if (entity.isPlayerInTrance(client.player.getUUID())) {
            overlayAlpha = 1.0f;
        } else {
            double maxDist = 6.0;
            double minDist = 2.0;
            if (distance <= minDist) {
                overlayAlpha = 1.0f;
            } else if (distance < maxDist) {
                overlayAlpha = (float) ((maxDist - distance) / (maxDist - minDist));
            } else {
                overlayAlpha = 0.0f;
            }
        }

        if (overlayAlpha > 0.0f) {
            Direction facing = state.getValue(MirrorBlock.FACING);

            matrices.pushPose();
            matrices.translate(0.5, 0.5, 0.5);
            matrices.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));

            matrices.scale(1.0f, 1.0f, -1.0f);

            matrices.translate(-0.5, -0.5, -0.5);

            float glassDepth = (15.25f / 16.0f) - 0.003f;
            matrices.translate(0.0f, 0.0f, glassDepth);

            VertexConsumer buffer = vertexConsumers.getBuffer(RenderType.entityTranslucent(textureToUse));
            PoseStack.Pose entry = matrices.last();

            float minX = 2.25f / 16.0f;
            float maxX = 13.75f / 16.0f;
            float minY = 2.75f / 16.0f;
            float maxY = 2.0f - minX;

            float normalX = entry.normal().m02();
            float normalY = entry.normal().m12();
            float normalZ = entry.normal().m22();

            buffer.addVertex(entry.pose(), minX, minY, 0.0f)
                    .setColor(1.0f, 1.0f, 1.0f, overlayAlpha)
                    .setUv(1.0f, 1.0f)
                    .setOverlay(overlay)
                    .setLight(0xF000F0)
                    .setNormal(entry, normalX, normalY, normalZ);

            buffer.addVertex(entry.pose(), maxX, minY, 0.0f)
                    .setColor(1.0f, 1.0f, 1.0f, overlayAlpha)
                    .setUv(0.0f, 1.0f)
                    .setOverlay(overlay)
                    .setLight(0xF000F0)
                    .setNormal(entry, normalX, normalY, normalZ);

            buffer.addVertex(entry.pose(), maxX, maxY, 0.0f)
                    .setColor(1.0f, 1.0f, 1.0f, overlayAlpha)
                    .setUv(0.0f, 0.0f)
                    .setOverlay(overlay)
                    .setLight(0xF000F0)
                    .setNormal(entry, normalX, normalY, normalZ);

            buffer.addVertex(entry.pose(), minX, maxY, 0.0f)
                    .setColor(1.0f, 1.0f, 1.0f, overlayAlpha)
                    .setUv(1.0f, 0.0f)
                    .setOverlay(overlay)
                    .setLight(0xF000F0)
                    .setNormal(entry, normalX, normalY, normalZ);

            matrices.popPose();
        }
    }
}