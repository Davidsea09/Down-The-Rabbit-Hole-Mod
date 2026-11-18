package net.emanueljdf09.dtrhmod.entity.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.emanueljdf09.dtrhmod.fabric.entity.client.models.CaterpillarModel;
import net.emanueljdf09.dtrhmod.fabric.entity.custom.CaterpillarEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CaterpillarRenderer extends GeoEntityRenderer<CaterpillarEntity> {

    private static final String PIPE_BONE = "pipe";

    private int lastEmitAge = -1;

    public CaterpillarRenderer(EntityRendererProvider.Context context) {
        super(context, new CaterpillarModel());
        this.shadowRadius = 1.2f;
    }


    @Override
    public void preRender(PoseStack poseStack, CaterpillarEntity animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        model.getBone(PIPE_BONE).ifPresent(bone -> bone.setTrackingMatrices(true));
    }

    @Override
    public void postRender(PoseStack poseStack, CaterpillarEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.postRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

        if (isReRender || animatable.age == lastEmitAge) return;
        lastEmitAge = animatable.age;

        emitSmoke(animatable, model);
    }

    private void emitSmoke(CaterpillarEntity caterpillar, BakedGeoModel model) {
        Vector3d pipe = positionOf(caterpillar, model, PIPE_BONE);
        boolean burst = caterpillar.consumePuffBurst();

        Level world = caterpillar.getWorld();
        switch (caterpillar.getState()) {
            case NEUTRAL, IDLE -> {
                if (caterpillar.getPhase() != 1) return;
                if (caterpillar.age % 20 == 0) wisp(world, pipe);
            }
            case PUFFING -> {
                if (caterpillar.getClientStateTicks() < caterpillar.getPuffReleaseTick() && caterpillar.age % 2 == 0) {
                    wisp(world, pipe);
                }
                if (burst) {
                    burst(world, pipe, 16);
                }
            }
            default -> { }
        }
    }

    private static Vector3d positionOf(CaterpillarEntity caterpillar, BakedGeoModel model, String boneName) {
        Vector3d p = model.getBone(boneName).map(GeoBone::getWorldPosition).orElse(null);
        if (p == null) return null;
        if (caterpillar.squaredDistanceTo(p.x, p.y, p.z) > 64.0) return null;
        return p;
    }

    private static void wisp(Level world, Vector3d p) {
        if (p == null) return;
        world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, p.x, p.y, p.z, 0.0, 0.04, 0.0);
    }

    private static void burst(Level world, Vector3d p, int count) {
        if (p == null) return;
        RandomSource random = world.getRandom();
        for (int i = 0; i < count; i++) {
            world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, p.x, p.y, p.z,
                    random.nextGaussian() * 0.03, 0.05 + random.nextDouble() * 0.04, random.nextGaussian() * 0.03);
        }
    }
}
