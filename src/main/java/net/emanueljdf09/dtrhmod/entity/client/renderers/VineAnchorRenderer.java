package net.emanueljdf09.dtrhmod.entity.client.renderers;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.entity.custom.VineAnchorEntity;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

/**
 * Draws the vine as a ribbon (two crossed strips) from the flower's "mouth" to the victim's waist,
 * sagging a little in the middle. The entity itself (the thing you hit) has no model.
 * Move this next to your other renderers (the package here is a guess).
 */
public class VineAnchorRenderer extends EntityRenderer<VineAnchorEntity> {
    private static final Identifier TEXTURE = new Identifier(DownTheRabbitHole.MOD_ID, "textures/entity/vine_lead.png");
    private static final int SEGMENTS = 14;
    private static final double HALF_WIDTH = 0.08;
    private static final double SAG = 0.35;
    // How many blocks of vine one copy of the texture covers (1.0 = the texture repeats every block).
    // Lower it (e.g. 0.5) if the texture looks stretched along the vine.
    private static final double TILE_LENGTH = 1.0;

    public VineAnchorRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(VineAnchorEntity entity) {
        return TEXTURE;
    }

    // the beam can be longer than the anchor's tiny hitbox, so don't cull it by the hitbox
    @Override
    public boolean shouldRender(VineAnchorEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }

    @Override
    public void render(VineAnchorEntity vine, float yaw, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light) {
        super.render(vine, yaw, tickDelta, matrices, vertexConsumers, light);

        Entity flower = vine.getWorld().getEntityById(vine.getFlowerId());
        Entity victim = vine.getWorld().getEntityById(vine.getVictimId());
        if (flower == null || victim == null) return;

        // matrices are already translated to the vine's position, so work relative to it
        Vec3d origin = vine.getLerpedPos(tickDelta);
        Vec3d from = flower.getLerpedPos(tickDelta).add(0, flower.getHeight() * 0.7, 0).subtract(origin);
        Vec3d to = victim.getLerpedPos(tickDelta).add(0, victim.getHeight() * 0.5, 0).subtract(origin);

        Vec3d dir = to.subtract(from);
        double length = dir.length();
        if (length < 0.01) return;
        Vec3d d = dir.normalize();

        Vec3d side = d.crossProduct(new Vec3d(0, 1, 0));
        if (side.lengthSquared() < 1.0E-4) side = new Vec3d(1, 0, 0);
        side = side.normalize().multiply(HALF_WIDTH);
        Vec3d up = d.crossProduct(side).normalize().multiply(HALF_WIDTH);

        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(TEXTURE));
        MatrixStack.Entry entry = matrices.peek();
        Matrix4f pos = entry.getPositionMatrix();
        Matrix3f normal = entry.getNormalMatrix();

        Vec3d prev = point(from, to, 0.0);
        for (int i = 1; i <= SEGMENTS; i++) {
            Vec3d cur = point(from, to, i / (double) SEGMENTS);
            float v0 = (float) ((i - 1) / (double) SEGMENTS * length / TILE_LENGTH);
            float v1 = (float) (i / (double) SEGMENTS * length / TILE_LENGTH);
            strip(buffer, pos, normal, prev, cur, side, v0, v1, light);
            strip(buffer, pos, normal, prev, cur, up, v0, v1, light);
            prev = cur;
        }
    }

    private static Vec3d point(Vec3d from, Vec3d to, double t) {
        return from.lerp(to, t).add(0, -Math.sin(Math.PI * t) * SAG, 0);
    }

    private static void strip(VertexConsumer buf, Matrix4f pos, Matrix3f normal,
                              Vec3d a, Vec3d b, Vec3d offset, float v0, float v1, int light) {
        vertex(buf, pos, normal, a.add(offset), 0f, v0, light);
        vertex(buf, pos, normal, a.subtract(offset), 1f, v0, light);
        vertex(buf, pos, normal, b.subtract(offset), 1f, v1, light);
        vertex(buf, pos, normal, b.add(offset), 0f, v1, light);
    }

    private static void vertex(VertexConsumer buf, Matrix4f pos, Matrix3f normal, Vec3d p, float u, float v, int light) {
        buf.vertex(pos, (float) p.x, (float) p.y, (float) p.z)
                .color(255, 255, 255, 255)
                .texture(u, v)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(normal, 0f, 1f, 0f)
                .next();
    }
}