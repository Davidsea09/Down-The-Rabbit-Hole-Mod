package net.emanueljdf09.dtrhmod.datagen;

import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.fabric.block.ModBlocks;
import net.emanueljdf09.dtrhmod.fabric.item.ModItems;
import net.emanueljdf09.dtrhmod.fabric.world.dimension.ModDimensions;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.ChangeDimensionTrigger;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementProvider extends FabricAdvancementProvider {

    public ModAdvancementProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {

        AdvancementHolder root = Advancement.Builder.advancement()
                .display(
                        ModItems.POCKETWATCH,
                        Component.translatable("advancement.dtrhmod.root.title"),
                        Component.translatable("advancement.dtrhmod.root.description"),
                        ResourceLocation.parse("textures/gui/advancements/backgrounds/adventure.png"),
                        AdvancementType.TASK,
                        false, false, false
                )
                .criterion("entered_exterior",
                        ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(ModDimensions.EXTERIOR_LEVEL_KEY))
                .build(consumer, DownTheRabbitHole.MOD_ID + ":root");

        AdvancementHolder portal = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        ModBlocks.RABBIT_HOLE.asItem(),
                        Component.translatable("advancement.dtrhmod.portal.title"),
                        Component.translatable("advancement.dtrhmod.portal.description"),
                        null,
                        AdvancementType.TASK,
                        true, true, false
                )
                .criterion("entered_exterior",
                        ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(ModDimensions.EXTERIOR_LEVEL_KEY))
                .build(consumer, DownTheRabbitHole.MOD_ID + ":exterior_complete");

        AdvancementHolder boss_root = Advancement.Builder.advancement()
                .parent(portal)
                .display(
                        ModItems.MAD_HATTER_SPAWN_EGG,
                        Component.translatable("advancement.dtrhmod.boss_root.title"),
                        Component.translatable("advancement.dtrhmod.boss_root.description"),
                        null,
                        AdvancementType.TASK,
                        false, false, false
                )
                .criterion("boss_root", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .build(consumer, DownTheRabbitHole.MOD_ID + ":boss_root");

        AdvancementHolder secret_root = Advancement.Builder.advancement()
                .parent(portal)
                .display(
                        ModItems.POCKETWATCH,
                        Component.translatable("advancement.dtrhmod.secret_root.title"),
                        Component.translatable("advancement.dtrhmod.secret_root.description"),
                        null,
                        AdvancementType.TASK,
                        false, false, true
                )
                .criterion("secret_root", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .build(consumer, DownTheRabbitHole.MOD_ID + ":secret_root");


        AdvancementHolder madHatterTrivia = Advancement.Builder.advancement()
                .parent(portal)
                .display(
                        ModItems.MAD_HAT_ITEM,
                        Component.translatable("advancement.dtrhmod.mad_hatter_trivia.title"),
                        Component.translatable("advancement.dtrhmod.mad_hatter_trivia.description"),
                        null,
                        AdvancementType.TASK,
                        true, true, true
                )
                .criterion("won_trivia", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .build(consumer, DownTheRabbitHole.MOD_ID + ":won_mad_hatter_trivia");

        AdvancementHolder madHatterKill = Advancement.Builder.advancement()
                .parent(boss_root)
                .display(
                        ModItems.MAD_HATTER_HEAD_ITEM,
                        Component.translatable("advancement.dtrhmod.mad_hatter_kill.title"),
                        Component.translatable("advancement.dtrhmod.mad_hatter_kill.description"),
                        null,
                        AdvancementType.CHALLENGE,
                        true, true, false
                )
                .criterion("killed_mad_hatter", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .build(consumer, DownTheRabbitHole.MOD_ID + ":killed_mad_hatter");

        AdvancementHolder flowersKill = Advancement.Builder.advancement()
                .parent(boss_root)
                .display(
                        Items.ROSE_BUSH,
                        Component.translatable("advancement.dtrhmod.flowers_kill.title"),
                        Component.translatable("advancement.dtrhmod.flowers_kill.description"),
                        null,
                        AdvancementType.CHALLENGE,
                        true, true, true
                )
                .addCriterion("killed_flowers", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .save(consumer, DownTheRabbitHole.MOD_ID + ":killed_flowers");

        AdvancementHolder caterpillarKill = Advancement.Builder.advancement()
                .parent(boss_root)
                .display(
                        ModItems.CATERPILLAR_HEAD_ITEM,
                        Component.translatable("advancement.dtrhmod.caterpillar_kill.title"),
                        Component.translatable("advancement.dtrhmod.caterpillar_kill.description"),
                        null,
                        AdvancementType.CHALLENGE,
                        true, true, false
                )
                .criterion("killed_caterpillar", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .build(consumer, DownTheRabbitHole.MOD_ID + ":killed_caterpillar");

        AdvancementHolder cheshireCatSnitch = Advancement.Builder.advancement()
                .parent(portal)
                .display(
                        ModItems.CAT_GRIN.asItem(),
                        Component.translatable("advancement.dtrhmod.cheshire_cat_snitch.title"),
                        Component.translatable("advancement.dtrhmod.cheshire_cat_snitch.description"),
                        null,
                        AdvancementType.TASK,
                        true, true, false
                )
                .criterion("cheshire_cat_snitch", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .build(consumer, DownTheRabbitHole.MOD_ID + ":cheshire_cat_snitch");

        AdvancementHolder teapotCraftAdv = Advancement.Builder.advancement()
                .parent(portal)
                .display(
                        ModBlocks.TEAPOT_BLOCK.asItem(),
                        Component.translatable("advancement.dtrhmod.teapot_craft_adv.title"),
                        Component.translatable("advancement.dtrhmod.teapot_craft_adv.description"),
                        null,
                        AdvancementType.TASK,
                        true, true, false
                )
                .criterion("teapot_craft_adv", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .build(consumer, DownTheRabbitHole.MOD_ID + ":teapot_craft_adv");
    }
}
