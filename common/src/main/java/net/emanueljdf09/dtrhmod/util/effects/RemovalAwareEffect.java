package net.emanueljdf09.dtrhmod.util.effects;

import net.minecraft.world.entity.LivingEntity;

public interface RemovalAwareEffect {
    void onRemovedFrom(LivingEntity entity);
}
