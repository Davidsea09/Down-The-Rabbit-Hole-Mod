package net.emanueljdf09.dtrhmod.entity.projectile;

import net.emanueljdf09.dtrhmod.entity.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlantFangEntity extends Entity implements TraceableEntity {
    private int warmup;
    private boolean startedAttack;
    private int ticksLeft;
    private boolean playingAnimation;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUuid;

    public PlantFangEntity(EntityType<? extends PlantFangEntity> entityType, Level world) {
        super(entityType, world);
        this.ticksLeft = 22;
    }

    public PlantFangEntity(Level world, double x, double y, double z, float yaw, int warmup, LivingEntity owner) {
        this(ModEntities.PLANT_FANGS.get(), world);
        this.warmup = warmup;
        this.setOwner(owner);
        this.setYRot(yaw * (180F / (float)Math.PI));
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.owner = owner;
        this.ownerUuid = owner == null ? null : owner.getUUID();
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUuid != null && this.level() instanceof ServerLevel serverWorld) {
            Entity entity = serverWorld.getEntity(this.ownerUuid);
            if (entity instanceof LivingEntity livingEntity) {
                this.owner = livingEntity;
            }
        }
        return this.owner;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        this.warmup = nbt.getInt("Warmup");
        if (nbt.hasUUID("Owner")) {
            this.ownerUuid = nbt.getUUID("Owner");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putInt("Warmup", this.warmup);
        if (this.ownerUuid != null) {
            nbt.putUUID("Owner", this.ownerUuid);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            if (this.playingAnimation) {
                --this.ticksLeft;
                if (this.ticksLeft == 14) {
                    for (int i = 0; i < 12; ++i) {
                        double d = this.getX() + (this.random.nextDouble() * 2.0 - 1.0) * (double)this.getBbWidth() * 0.5;
                        double e = this.getY() + 0.05 + this.random.nextDouble();
                        double f = this.getZ() + (this.random.nextDouble() * 2.0 - 1.0) * (double)this.getBbWidth() * 0.5;
                        double g = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
                        double h = 0.3 + this.random.nextDouble() * 0.3;
                        double j = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
                        this.level().addParticle(ParticleTypes.CRIT, d, e + 1.0, f, g, h, j);
                    }
                }
            }
        } else if (--this.warmup < 0) {
            if (this.warmup == -8) {
                for (LivingEntity livingEntity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.2, 0.0, 0.2))) {
                    this.damageTarget(livingEntity);
                }
            }

            if (!this.startedAttack) {
                this.level().broadcastEntityEvent(this, (byte)4);
                this.startedAttack = true;
            }

            if (--this.ticksLeft < 0) {
                this.discard();
            }
        }
    }

    private void damageTarget(LivingEntity target) {
        LivingEntity livingEntity = this.getOwner();
        if (target.isAlive() && !target.isInvulnerable() && target != livingEntity) {
            if (this.level() instanceof ServerLevel serverWorld) {
                if (livingEntity == null) {
                    target.hurt(this.damageSources().magic(), 6.0F);
                } else {
                    if (livingEntity.isAlliedTo(target)) {
                        return;
                    }
                    target.hurt(this.damageSources().indirectMagic(this, livingEntity), 6.0F);
                }
            }
        }
    }

    @Override
    public void handleEntityEvent(byte status) {
        super.handleEntityEvent(status);
        if (status == 4) {
            this.playingAnimation = true;
            if (!this.isSilent()) {
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.EVOKER_FANGS_ATTACK, this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.2F + 0.85F, false);
            }
        }
    }

    public float getAnimationProgress(float tickDelta) {
        if (!this.playingAnimation) {
            return 0.0F;
        } else {
            int i = this.ticksLeft - 2;
            return i <= 0 ? 1.0F : 1.0F - ((float)i - tickDelta) / 20.0F;
        }
    }
}