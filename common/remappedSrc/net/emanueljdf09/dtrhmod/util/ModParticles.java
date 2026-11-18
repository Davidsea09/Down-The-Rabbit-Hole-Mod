package net.emanueljdf09.dtrhmod.util;

import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class ModParticles {

    public static final SimpleParticleType BB_LEAVE_PARTICLE = registerParticle("bb");
    public static final SimpleParticleType TH_LEAVE_PARTICLE  = registerParticle("th");
    public static final SimpleParticleType WW_LEAVE_PARTICLE  = registerParticle("ww");
    public static final SimpleParticleType WONDERLAND_LOCK_PARTICLE  = registerParticle("wonderland_lock");

    private static SimpleParticleType registerParticle(String name) {
        return Registry.register(
                BuiltInRegistries.PARTICLE_TYPE,
                ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, name),
                FabricParticleTypes.simple()
        );
    }

    public static void registerParticles() {
        DownTheRabbitHole.LOGGER.info("Registering Particles for " + DownTheRabbitHole.MOD_ID);
    }
}
