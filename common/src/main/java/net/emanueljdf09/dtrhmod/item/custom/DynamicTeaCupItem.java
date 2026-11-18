package net.emanueljdf09.dtrhmod.item.custom;

import net.emanueljdf09.dtrhmod.item.ModItems;
import net.emanueljdf09.dtrhmod.recipe.TeapotRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class DynamicTeaCupItem extends Item {
    public DynamicTeaCupItem(Properties settings) {
        super(settings);
    }

    public static ItemStack createTeaVariant(TeapotRecipe recipe, Item fluidItem, HolderLookup.Provider registries) {
        ItemStack resultCup = new ItemStack(ModItems.FILLED_TEA_CUP.get());
        CompoundTag nbt = new CompoundTag();
        ListTag effectsList = new ListTag();

        int totalR = 0;
        int totalG = 0;
        int totalB = 0;
        int colorCount = 0;

        var ops = registries.createSerializationContext(NbtOps.INSTANCE);

        for (MobEffectInstance baseEffect : recipe.getEffects()) {
            int finalDuration = baseEffect.getDuration();
            int finalAmplifier = baseEffect.getAmplifier();

            if (fluidItem == Items.LAVA_BUCKET) {
                finalAmplifier += 1;
            } else if (fluidItem == Items.MILK_BUCKET) {
                finalDuration = 400; // 20 seconds
            }

            MobEffectInstance modifiedEffect = new MobEffectInstance(
                    baseEffect.getEffect(),
                    finalDuration,
                    finalAmplifier,
                    baseEffect.isAmbient(),
                    baseEffect.isVisible()
            );

            MobEffectInstance.CODEC.encodeStart(ops, modifiedEffect)
                    .result()
                    .ifPresent(effectsList::add);

            int effectColor = baseEffect.getEffect().value().getColor();
            totalR += (effectColor >> 16) & 0xFF;
            totalG += (effectColor >> 8) & 0xFF;
            totalB += effectColor & 0xFF;
            colorCount++;
        }

        if (!effectsList.isEmpty()) {
            int r = colorCount > 0 ? (totalR / colorCount) : 0x33;
            int g = colorCount > 0 ? (totalG / colorCount) : 0x99;
            int b = colorCount > 0 ? (totalB / colorCount) : 0xFF;

            if (fluidItem == Items.LAVA_BUCKET) {
                r = (int) (r * 0.6 + 0xFF * 0.4);
                g = (int) (g * 0.6 + 0x45 * 0.4);
                b = (int) (b * 0.6 + 0x00 * 0.4);
            } else if (fluidItem == Items.MILK_BUCKET) {
                r = (int) (r * 0.5 + 255 * 0.5);
                g = (int) (g * 0.5 + 255 * 0.5);
                b = (int) (b * 0.5 + 255 * 0.5);
            }

            int finalArgbColor = 0xFF000000 | (r << 16) | (g << 8) | b;

            nbt.put("Effects", effectsList);
            nbt.putString("FluidUsed", BuiltInRegistries.ITEM.getKey(fluidItem).toString());
            nbt.putInt("Color", finalArgbColor);
        }

        resultCup.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
        return resultCup;
    }

    public static List<MobEffectInstance> getEffectsFromStack(ItemStack stack, HolderLookup.Provider registries) {
        List<MobEffectInstance> effects = new ArrayList<>();
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag nbt = customData.copyTag();
            if (nbt.contains("Effects", Tag.TAG_LIST)) {
                ListTag effectsList = nbt.getList("Effects", Tag.TAG_COMPOUND);
                var ops = registries != null ? registries.createSerializationContext(NbtOps.INSTANCE) : NbtOps.INSTANCE;
                for (int i = 0; i < effectsList.size(); i++) {
                    CompoundTag effectNbt = effectsList.getCompound(i);
                    MobEffectInstance.CODEC.parse(ops, effectNbt)
                            .result()
                            .ifPresent(effects::add);
                }
            }
        }
        return effects;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        List<MobEffectInstance> effects = getEffectsFromStack(stack, context.registries());
        if (!effects.isEmpty()) {
            for (MobEffectInstance effect : effects) {
                MobEffect statusEffect = effect.getEffect().value();
                MutableComponent effectText = Component.translatable(statusEffect.getDescriptionId());

                if (effect.getAmplifier() > 0) {
                    effectText.append(" ")
                            .append(Component.translatable("potion.potency." + effect.getAmplifier()));
                }

                int durationSeconds = effect.getDuration() / 20;
                String timeString = String.format("%d:%02d", durationSeconds / 60, durationSeconds % 60);

                effectText.append(" (").append(timeString).append(")");

                tooltip.add(effectText.withStyle(statusEffect.getCategory().getTooltipFormatting()));
            }
        } else {
            tooltip.add(Component.translatable("tooltip.dtrhmod.empty_tea").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        if (!world.isClientSide) {
            for (MobEffectInstance effect : getEffectsFromStack(stack, world.registryAccess())) {
                user.addEffect(new MobEffectInstance(effect));
            }
        }

        if (user instanceof Player player) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            ItemStack emptyCup = new ItemStack(ModItems.EMPTY_CUP.get());
            if (stack.isEmpty()) {
                return emptyCup;
            } else {
                if (!player.getInventory().add(emptyCup)) {
                    player.drop(emptyCup, false);
                }
            }
        }
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) { // Fixed method name/signature for Mojang mappings
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(world, user, hand);
    }
}