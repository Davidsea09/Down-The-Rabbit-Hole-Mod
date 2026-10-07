package net.emanueljdf09.dtrhmod.datagen;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.block.ModBlocks;
import net.emanueljdf09.dtrhmod.item.ModItems;
import net.emanueljdf09.dtrhmod.world.dimension.ModDimensions;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.ChangedDimensionCriterion;
import net.minecraft.advancement.criterion.ImpossibleCriterion;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class ModAdvancementProvider extends FabricAdvancementProvider {
    public ModAdvancementProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateAdvancement(Consumer<Advancement> consumer) {

        Advancement root = Advancement.Builder.create()
                .display(
                        ModItems.POCKETWATCH,
                        Text.translatable("advancement.dtrhmod.root.title"),
                        Text.translatable("advancement.dtrhmod.root.description"),
                        new Identifier("textures/gui/advancements/backgrounds/adventure.png"),
                        AdvancementFrame.TASK,
                        false, false, false
                )
                .criterion("entered_exterior",
                        ChangedDimensionCriterion.Conditions.to(ModDimensions.EXTERIOR_LEVEL_KEY))
                .build(consumer, DownTheRabbitHole.MOD_ID + ":root");

        Advancement portal = Advancement.Builder.create()
                .parent(root)
                .display(
                        ModBlocks.RABBIT_HOLE.asItem(),
                        Text.translatable("advancement.dtrhmod.portal.title"),
                        Text.translatable("advancement.dtrhmod.portal.description"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("entered_exterior",
                        ChangedDimensionCriterion.Conditions.to(ModDimensions.EXTERIOR_LEVEL_KEY))
                .build(consumer, DownTheRabbitHole.MOD_ID + ":exterior_complete");

        Advancement boss_root = Advancement.Builder.create()
                .parent(portal)
                .display(
                        ModItems.MAD_HATTER_SPAWN_EGG,
                        Text.translatable("advancement.dtrhmod.boss_root.title"),
                        Text.translatable("advancement.dtrhmod.boss_root.description"),
                        null,
                        AdvancementFrame.TASK,
                        false, false, false
                )
                .criterion("boss_root", new ImpossibleCriterion.Conditions())
                .build(consumer, DownTheRabbitHole.MOD_ID + ":boss_root");

        Advancement secret_root = Advancement.Builder.create()
                .parent(portal)
                .display(
                        ModItems.POCKETWATCH,
                        Text.translatable("advancement.dtrhmod.secret_root.title"),
                        Text.translatable("advancement.dtrhmod.secret_root.description"),
                        null,
                        AdvancementFrame.TASK,
                        false, false, true
                )
                .criterion("secret_root", new ImpossibleCriterion.Conditions())
                .build(consumer, DownTheRabbitHole.MOD_ID + ":secret_root");


        Advancement madHatterTrivia = Advancement.Builder.create()
                .parent(portal)
                .display(
                        ModItems.MAD_HAT_ITEM,
                        Text.translatable("advancement.dtrhmod.mad_hatter_trivia.title"),
                        Text.translatable("advancement.dtrhmod.mad_hatter_trivia.description"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("won_trivia", new ImpossibleCriterion.Conditions())
                .build(consumer, DownTheRabbitHole.MOD_ID + ":won_mad_hatter_trivia");

        Advancement madHatterKill = Advancement.Builder.create()
                .parent(boss_root)
                .display(
                        ModItems.MAD_HATTER_HEAD_ITEM,
                        Text.translatable("advancement.dtrhmod.mad_hatter_kill.title"),
                        Text.translatable("advancement.dtrhmod.mad_hatter_kill.description"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true, true, false
                )
                .criterion("killed_mad_hatter", new ImpossibleCriterion.Conditions())
                .build(consumer, DownTheRabbitHole.MOD_ID + ":killed_mad_hatter");

        Advancement flowersKill = Advancement.Builder.create()
                .parent(boss_root)
                .display(
                        Items.ROSE_BUSH,
                        Text.translatable("advancement.dtrhmod.flowers_kill.title"),
                        Text.translatable("advancement.dtrhmod.flowers_kill.description"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true, true, true
                )
                .criterion("killed_flowers", new ImpossibleCriterion.Conditions())
                .build(consumer, DownTheRabbitHole.MOD_ID + ":killed_flowers");

        Advancement cheshireCatSnitch = Advancement.Builder.create()
                .parent(portal)
                .display(
                        ModItems.CAT_FUR.asItem(),
                        Text.translatable("advancement.dtrhmod.cheshire_cat_snitch.title"),
                        Text.translatable("advancement.dtrhmod.cheshire_cat_snitch.description"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("cheshire_cat_snitch", new ImpossibleCriterion.Conditions())
                .build(consumer, DownTheRabbitHole.MOD_ID + ":cheshire_cat_snitch");

        Advancement teapotCraftAdv = Advancement.Builder.create()
                .parent(portal)
                .display(
                        ModBlocks.TEAPOT_BLOCK.asItem(),
                        Text.translatable("advancement.dtrhmod.teapot_craft_adv.title"),
                        Text.translatable("advancement.dtrhmod.teapot_craft_adv.description"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("teapot_craft_adv", new ImpossibleCriterion.Conditions())
                .build(consumer, DownTheRabbitHole.MOD_ID + ":teapot_craft_adv");
    }
}
