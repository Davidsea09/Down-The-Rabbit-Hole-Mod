package net.emanueljdf09.dtrhmod.util.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class WonderlandLockParticle extends SpriteBillboardParticle {
    protected WonderlandLockParticle(ClientWorld clientWorld, double x, double y, double z, double vx, double vy, double vz, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, vx, vy, vz);

        this.velocityX = vx * 0.1D + (Math.random() * 2.0D - 1.0D) * 0.05D;
        this.velocityY = vy + 0.05D;
        this.velocityZ = vz * 0.1D + (Math.random() * 2.0D - 1.0D) * 0.05D;

        this.scale *= 1.2F + (random.nextFloat() - 0.5F) * 0.4F;
        this.maxAge = (int)(20.0D / (Math.random() * 0.8D + 0.2D));
        this.collidesWithWorld = false;

        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.dead) {
            this.alpha = (float) (this.maxAge - this.age) / this.maxAge;
        }

        this.scale += 0.02F;
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new WonderlandLockParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
        }
    }
}
