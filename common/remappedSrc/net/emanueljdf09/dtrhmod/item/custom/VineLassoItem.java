package net.emanueljdf09.dtrhmod.item.custom;

import net.emanueljdf09.dtrhmod.fabric.entity.projectile.VineLassoProjectileEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class VineLassoItem extends Item {

    public static final int MAX_DURATION_TICKS = 200;
    public static final int COOLDOWN_TICKS = 300;

    public VineLassoItem(Properties settings) {
        super(settings.stacksTo(1).durability(64));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);

        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.5f, 0.4f);

        if (!world.isClientSide()) {
            VineLassoProjectileEntity projectile = new VineLassoProjectileEntity(world, user);
            projectile.setVelocity(user, user.getXRot(), user.getYRot(), 0.0f, 1.5f, 1.0f);
            world.addFreshEntity(projectile);

            stack.hurtAndBreak(1, user, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        }

        user.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

        return InteractionResultHolder.success(stack);
    }
}