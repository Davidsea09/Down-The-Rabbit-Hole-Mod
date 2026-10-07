package net.emanueljdf09.dtrhmod.item.custom;

import net.emanueljdf09.dtrhmod.entity.projectile.VineLassoProjectileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class VineLassoItem extends Item {

    public static final int MAX_DURATION_TICKS = 200;
    public static final int COOLDOWN_TICKS = 300;

    public VineLassoItem(Settings settings) {
        super(settings.maxCount(1).maxDamage(64));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.PLAYERS, 0.5f, 0.4f);

        if (!world.isClient()) {
            VineLassoProjectileEntity projectile = new VineLassoProjectileEntity(world, user);
            projectile.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 1.5f, 1.0f);
            world.spawnEntity(projectile);

            stack.damage(1, user, (p) -> p.sendToolBreakStatus(hand));
        }

        user.getItemCooldownManager().set(this, COOLDOWN_TICKS);

        return TypedActionResult.success(stack);
    }
}