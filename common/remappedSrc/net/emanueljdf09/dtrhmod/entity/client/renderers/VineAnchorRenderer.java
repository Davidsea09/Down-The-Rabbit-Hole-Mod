package net.emanueljdf09.dtrhmod.entity.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.fabric.entity.custom.VineAnchorEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class VineAnchorRenderer extends EntityRenderer<VineAnchorEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/entity/vine_lead.png");
    private static final int SEGMENTS = 14;
    private static final double HALF_WIDTH = 0.08;
    private static final double SAG = 0.35;

    private static final double TILE_LENGTH = 1.0;

    public VineAnchorRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public ResourceLocation getTexture(VineAnchorEntity entity) {
        return TEXTURE;
    }

    @Override
    public boolean shouldRender(VineAnchorEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }

    @Override
    public void render(VineAnchorEntity vine, float yaw, float tickDelta, PoseStack matrices,
                       MultiBufferSource vertexConsumers, int light) {
        super.render(vine, yaw, tickDelta, matrices, vertexConsumers, light);

        Entity flower = vine.getWorld().getEntityById(vine.getOwnerId());
        Entity victim = vine.getWorld().getEntityById(vine.getVictimId());
        if (flower == null || victim == null) return;

        Vec3 origin = vine.getLerpedPos(tickDelta);
        Vec3 from = flower.getPosition(tickDelta).add(0, flower.getBbHeight() * 0.7, 0).subtract(origin);
        Vec3 to = victim.getPosition(tickDelta).add(0, victim.getBbHeight() * 0.5, 0).subtract(origin);

        Vec3 dir = to.subtract(from);
        double length = dir.length();
        if (length < 0.01) return;
        Vec3 d = dir.normalize();

        Vec3 side = d.cross(new Vec3(0, 1, 0));
        if (side.lengthSqr() < 1.0E-4) side = new Vec3(1, 0, 0);
        side = side.normalize().scale(HALF_WIDTH);
        Vec3 up = d.cross(side).normalize().scale(HALF_WIDTH);

        VertexConsumer buffer = vertexConsumers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        PoseStack.Pose entry = matrices.last();
        Matrix4f pos = entry.pose();
        Matrix3f normal = entry.normal();

        Vec3 prev = point(from, to, 0.0);
        for (int i = 1; i <= SEGMENTS; i++) {
            Vec3 cur = point(from, to, i / (double) SEGMENTS);
            float v0 = (float) ((i - 1) / (double) SEGMENTS * length / TILE_LENGTH);
            float v1 = (float) (i / (double) SEGMENTS * length / TILE_LENGTH);
            strip(buffer, pos, normal, prev, cur, side, v0, v1, light);
            strip(buffer, pos, normal, prev, cur, up, v0, v1, light);
            prev = cur;
        }
    }

    private static Vec3 point(Vec3 from, Vec3 to, double t) {
        return from.lerp(to, t).add(0, -Math.sin(Math.PI * t) * SAG, 0);
    }

    private static void strip(VertexConsumer buf, Matrix4f pos, Matrix3f normal,
                              Vec3 a, Vec3 b, Vec3 offset, float v0, float v1, int light) {
        vertex(buf, pos, normal, a.add(offset), 0f, v0, light);
        vertex(buf, pos, normal, a.subtract(offset), 1f, v0, light);
        vertex(buf, pos, normal, b.subtract(offset), 1f, v1, light);
        vertex(buf, pos, normal, b.add(offset), 0f, v1, light);
    }

    private static void vertex(VertexConsumer buf, Matrix4f pos, Matrix3f normal, Vec3 p, float u, float v, int light) {
        buf.addVertex(pos, (float) p.x, (float) p.y, (float) p.z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(0f, 1f, 0f);
    }
}