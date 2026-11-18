package net.emanueljdf09.dtrhmod.item.custom;

import net.emanueljdf09.dtrhmod.entity.custom.CaterpillarLetters.Letter;
import net.emanueljdf09.dtrhmod.util.entity.LetterPuffs;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import java.util.List;
import java.util.Locale;

public class PipeItem extends Item {
    public static final int COOLDOWN_TICKS = 40;
    private static final float DAMAGE = 6.0f;
    private static final String LETTER_KEY = "Letter";

    public PipeItem(Properties settings) {
        super(settings);
    }

    public static Letter getLetter(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        int index = 0;
        if (customData != null) {
            CompoundTag nbt = customData.copyTag();
            index = nbt.getInt(LETTER_KEY);
        }
        Letter[] letters = Letter.values();
        return letters[Math.floorMod(index, letters.length)];
    }

    private static Item getRequiredIngredient(Letter letter) {
        return switch (letter) {
            case O -> Items.POPPY;
            case R -> Items.CORNFLOWER;
            case U -> Items.WITHER_ROSE;
        };
    }

    private static Component describe(Letter letter) {
        return Component.translatable("item.dtrhmod.pipe.letter." + letter.name().toLowerCase(Locale.ROOT));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);

        if (user.isShiftKeyDown()) {
            Letter[] letters = Letter.values();
            Letter next = letters[(getLetter(stack).ordinal() + 1) % letters.length];

            CompoundTag nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            nbt.putInt(LETTER_KEY, next.ordinal());
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));

            if (!world.isClientSide()) {
                user.displayClientMessage(Component.translatable("item.dtrhmod.pipe.selected", describe(next)), true);
                world.playSound(null, user.getX(), user.getY(), user.getZ(),
                        SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 0.8f, 1.2f);
            }
            return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
        }

        Letter letter = getLetter(stack);
        Item requiredIngredient = getRequiredIngredient(letter);

        boolean hasIngredient = user.getAbilities().instabuild || user.getInventory().contains(new ItemStack(requiredIngredient));

        if (!hasIngredient) {
            if (!world.isClientSide()) {
                user.displayClientMessage(Component.translatable("item.dtrhmod.pipe.missing_ingredient", requiredIngredient.getDescription()).withStyle(ChatFormatting.RED), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        if (!(world instanceof ServerLevel serverWorld)) {
            return InteractionResultHolder.success(stack);
        }

        if (!user.getAbilities().instabuild) {
            int slot = user.getInventory().findSlotMatchingItem(new ItemStack(requiredIngredient));
            if (slot != -1) {
                user.getInventory().getItem(slot).shrink(1);
            }
        }

        Vec3 look = user.getViewVector(1.0F);
        Vec3 dir = new Vec3(look.x, 0.0, look.z);
        dir = dir.lengthSqr() < 1.0E-4 ? Vec3.directionFromRotation(0f, user.getYRot()) : dir.normalize();

        LetterPuffs.spawn(serverWorld, user.position().add(dir.scale(1.2)), dir, letter, user, DAMAGE);

        Vec3 mouth = user.getEyePosition().add(dir.scale(0.6)).add(0.0, -0.2, 0.0);
        serverWorld.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, mouth.x, mouth.y, mouth.z, 10, 0.1, 0.1, 0.1, 0.03);
        serverWorld.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1.2f, 0.7f);

        stack.hurtAndBreak(1, user, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        user.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        super.appendHoverText(stack, context, tooltip, type);
        tooltip.add(Component.translatable("item.dtrhmod.pipe.selected", describe(getLetter(stack))).withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.dtrhmod.pipe.hint").withStyle(ChatFormatting.GRAY));
    }
}