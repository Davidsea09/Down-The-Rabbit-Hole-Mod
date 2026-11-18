package net.emanueljdf09.dtrhmod.util.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class WonderlandLockParticle extends TextureSheetParticle {
    protected WonderlandLockParticle(ClientLevel clientWorld, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteProvider) {
        super(clientWorld, x, y, z, vx, vy, vz);

        this.xd = vx * 0.1D + (Math.random() * 2.0D - 1.0D) * 0.05D;
        this.yd = vy + 0.05D;
        this.zd = vz * 0.1D + (Math.random() * 2.0D - 1.0D) * 0.05D;

        this.quadSize *= 1.2F + (random.nextFloat() - 0.5F) * 0.4F;
        this.lifetime = (int)(20.0D / (Math.random() * 0.8D + 0.2D));
        this.hasPhysics = false;

        this.setSpriteFromAge(spriteProvider);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.removed) {
            this.alpha = (float) (this.lifetime - this.age) / this.lifetime;
        }

        this.quadSize += 0.02F;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;

        public Factory(SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new WonderlandLockParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
        }
    }
}
