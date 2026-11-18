package net.emanueljdf09.dtrhmod.util.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class FallingWWLeafParticle extends TextureSheetParticle {
    private float rotationSpeed;
    private final float sample;
    private final float rotSpeedMultiplier;

    protected FallingWWLeafParticle(ClientLevel world, double x, double y, double z,
                                        double velocityX, double velocityY, double velocityZ,
                                        SpriteSet spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.setSprite(spriteProvider.get(this.random.nextInt(12), 12));
        this.rotationSpeed = (float) Math.toRadians(this.random.nextBoolean() ? -30.0F : 30.0F);
        this.sample = this.random.nextFloat();
        this.rotSpeedMultiplier = (float) Math.toRadians(this.random.nextBoolean() ? -5.0F : 5.0F);
        this.lifetime = 300;
        this.gravity = 7.5E-4F;
        float size = this.random.nextBoolean() ? 0.05F : 0.075F;
        this.quadSize = size;
        this.setSize(size, size);
        this.friction = 1.0F;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.lifetime-- <= 0) {
            this.remove();
        }

        if (!this.removed) {
            float f = (float) (300 - this.lifetime);
            float g = Math.min(f / 300.0F, 1.0F);
            double d = Math.cos(Math.toRadians((double) (this.sample * 60.0F))) * 2.0D * Math.pow((double) g, 1.25D);
            double e = Math.sin(Math.toRadians((double) (this.sample * 60.0F))) * 2.0D * Math.pow((double) g, 1.25D);
            this.xd += d * 0.0025D;
            this.zd += e * 0.0025D;
            this.yd -= (double) this.gravity;
            this.rotationSpeed += this.rotSpeedMultiplier / 20.0F;
            this.oRoll = this.roll;
            this.roll += this.rotationSpeed / 20.0F;
            this.move(this.xd, this.yd, this.zd);

            if (this.onGround || this.lifetime < 299 && (this.xd == 0.0D || this.zd == 0.0D)) {
                this.remove();
            }

            if (!this.removed) {
                this.xd *= (double) this.friction;
                this.yd *= (double) this.friction;
                this.zd *= (double) this.friction;
            }
        }
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;

        public Factory(SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientLevel world,
                                       double x, double y, double z,
                                       double velocityX, double velocityY, double velocityZ) {
            return new FallingWWLeafParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
        }
    }
}
