package net.emanueljdf09.dtrhmod.util.entity;


import net.emanueljdf09.dtrhmod.fabric.entity.custom.CaterpillarLetters;
import net.emanueljdf09.dtrhmod.fabric.entity.custom.CaterpillarLetters.Letter;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.*;

public class LetterPuffs {
    private static final double SPEED = 0.35;
    private static final int LIFETIME = 90;
    private static final double HIT_RADIUS = 0.85;

    private static final Map<ResourceKey<Level>, List<Puff>> PUFFS = new HashMap<>();

    private static class Puff {
        Vec3 pos;
        final Vec3 dir;
        final Vec3 side;
        final Letter letter;
        final UUID ownerId;
        final boolean ownerIsPlayer;
        final float damage;
        final Set<UUID> alreadyHit = new HashSet<>();
        int age = 0;

        Puff(Vec3 pos, Vec3 dir, Letter letter, @Nullable LivingEntity owner, float damage) {
            this.pos = pos;
            this.dir = dir;
            this.side = new Vec3(dir.z, 0.0, -dir.x);
            this.letter = letter;
            this.ownerId = owner == null ? null : owner.getUUID();
            this.ownerIsPlayer = owner instanceof Player;
            this.damage = damage;
        }
    }

    private LetterPuffs() { }

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(LetterPuffs::tickWorld);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> PUFFS.clear());
    }


    public static void spawn(ServerLevel world, Vec3 pos, Vec3 dir, Letter letter, @Nullable LivingEntity owner, float damage) {
        Vec3 flat = new Vec3(dir.x, 0.0, dir.z);
        if (flat.lengthSqr() < 1.0E-4) return;
        PUFFS.computeIfAbsent(world.dimension(), k -> new ArrayList<>())
                .add(new Puff(pos, flat.normalize(), letter, owner, damage));
    }

    public static void clearOwnedBy(ServerLevel world, UUID ownerId) {
        List<Puff> list = PUFFS.get(world.dimension());
        if (list != null) list.removeIf(p -> ownerId.equals(p.ownerId));
    }

    private static void tickWorld(ServerLevel world) {
        List<Puff> list = PUFFS.get(world.dimension());
        if (list == null || list.isEmpty()) return;

        Iterator<Puff> it = list.iterator();
        while (it.hasNext()) {
            Puff puff = it.next();
            puff.pos = puff.pos.add(puff.dir.scale(SPEED));
            puff.age++;

            BlockPos check = BlockPos.containing(puff.pos.x, puff.pos.y + 1.0, puff.pos.z);
            boolean blocked = !world.getBlockState(check).getCollisionShape(world, check).isEmpty();

            List<double[]> shape = CaterpillarLetters.points(puff.letter);
            draw(world, puff, shape);

            boolean hitSomething = false;
            Entity ownerEntity = puff.ownerId == null ? null : world.getEntity(puff.ownerId);
            LivingEntity owner = ownerEntity instanceof LivingEntity living ? living : null;

            AABB box = new AABB(puff.pos.x - 3.2, puff.pos.y - 0.5, puff.pos.z - 3.2,
                    puff.pos.x + 3.2, puff.pos.y + 4.5, puff.pos.z + 3.2);
            for (LivingEntity target : world.getEntitiesOfClass(LivingEntity.class, box, t -> canHurt(world, puff, owner, t))) {
                if (touchesLetter(puff, shape, target)) {
                    puff.alreadyHit.add(target.getUUID());
                    applyLetter(world, puff, owner, target);
                    hitSomething = true;
                }
            }

            boolean stopOnHit = !puff.ownerIsPlayer;
            if ((hitSomething && stopOnHit) || blocked || puff.age > LIFETIME) {
                it.remove();
            }
        }
    }

    private static boolean canHurt(ServerLevel world, Puff puff, @Nullable LivingEntity owner, LivingEntity target) {
        if (!target.isAlive() || target == owner || puff.alreadyHit.contains(target.getUUID())) return false;
        if (target instanceof ArmorStand) return false;

        if (target instanceof Player player) {
            if (player.isSpectator()) return false;
            return !puff.ownerIsPlayer || world.getServer().isPvpAllowed();
        }

        if (!puff.ownerIsPlayer) return false;
        return !(target instanceof TamableAnimal pet && owner != null && pet.isOwnedBy(owner));
    }

    private static boolean touchesLetter(Puff puff, List<double[]> shape, LivingEntity target) {
        Vec3 rel = target.getBoundingBox().getCenter().subtract(puff.pos);
        double grow = target.getBbWidth() * 0.3;

        if (rel.subtract(0.0, 2.0, 0.0).lengthSqr() > 20.0 + grow * 6.0) return false;
        if (Math.abs(rel.dot(puff.dir)) > 0.9 + grow) return false;

        double lateral = rel.dot(puff.side);
        double vert = rel.y;
        double reach = HIT_RADIUS + grow;
        for (double[] p : shape) {
            double du = lateral - p[0];
            double dv = vert - p[1];
            if (du * du + dv * dv < reach * reach) return true;
        }
        return false;
    }

    private static void applyLetter(ServerLevel world, Puff puff, @Nullable LivingEntity owner, LivingEntity target) {
        switch (puff.letter) {
            case O -> target.hurt(owner != null
                    ? world.damageSources().indirectMagic(owner, owner)
                    : world.damageSources().magic(), puff.damage);
            case R -> target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 140, 2), owner);
            case U -> target.addEffect(new MobEffectInstance(MobEffects.WITHER, 120, 1), owner);
        }
        world.sendParticles(ParticleTypes.CLOUD, target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(),
                15, 0.4, 0.5, 0.4, 0.05);
        world.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.HOSTILE, 1.0f, 0.7f);
    }

    private static void draw(ServerLevel world, Puff puff, List<double[]> shape) {
        if (puff.age % 3 != 0) return;

        ParticleOptions dust = dustFor(puff.letter);
        for (int i = 0; i < shape.size(); i += 2) {
            double[] p = shape.get(i);
            world.sendParticles(dust,
                    puff.pos.x + puff.side.x * p[0],
                    puff.pos.y + p[1],
                    puff.pos.z + puff.side.z * p[0],
                    1, 0.0, 0.0, 0.0, 0.0);
        }
        if (puff.age % 6 == 0) {
            world.sendParticles(ParticleTypes.CLOUD, puff.pos.x, puff.pos.y + 2.0, puff.pos.z, 2, 0.8, 0.8, 0.8, 0.01);
        }
    }

    public static DustParticleOptions dustFor(Letter letter) {
        return switch (letter) {
            case O -> new DustParticleOptions(new Vector3f(1.0f, 0.25f, 0.2f), 1.8f);
            case R -> new DustParticleOptions(new Vector3f(0.3f, 0.6f, 1.0f), 1.8f);
            case U -> new DustParticleOptions(new Vector3f(0.35f, 0.1f, 0.45f), 1.8f);
        };
    }
}
