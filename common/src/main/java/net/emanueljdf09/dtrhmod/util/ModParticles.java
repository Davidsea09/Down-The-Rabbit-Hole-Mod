package net.emanueljdf09.dtrhmod.util;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(DownTheRabbitHole.MOD_ID, Registries.PARTICLE_TYPE);

    public static final RegistrySupplier<SimpleParticleType> BB_LEAVE_PARTICLE =
            PARTICLE_TYPES.register("bb", () -> new SimpleParticleType(false) {});

    public static final RegistrySupplier<SimpleParticleType> TH_LEAVE_PARTICLE  =
            PARTICLE_TYPES.register("th", () -> new SimpleParticleType(false) {});

    public static final RegistrySupplier<SimpleParticleType> WW_LEAVE_PARTICLE  =
            PARTICLE_TYPES.register("ww", () -> new SimpleParticleType(false) {});

    public static final RegistrySupplier<SimpleParticleType> WONDERLAND_LOCK_PARTICLE  =
            PARTICLE_TYPES.register("wonderland_lock", () -> new SimpleParticleType(false) {});

    public static void registerParticles() {
        PARTICLE_TYPES.register();
        DownTheRabbitHole.LOGGER.info("Registering Particles for " + DownTheRabbitHole.MOD_ID);
    }
}