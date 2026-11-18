package net.emanueljdf09.dtrhmod.item.custom;

import net.emanueljdf09.dtrhmod.fabric.menu.screen.BookCoverScreen;
import net.emanueljdf09.dtrhmod.fabric.util.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.locale.Language;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.Level;
import java.util.*;

public class StoryBook extends WrittenBookItem {

    private final String storyType;

    public static final Map<String, StoryData> STORIES = new HashMap<>();

    static {
        STORIES.put("cinderella",  new StoryData("Cinderella", "Aschenputtel",  "Jacob & Wilhelm Grimm"));
        STORIES.put("red_riding_hood", new StoryData("Little Red Riding Hood", "Rotkäppchen", "Jacob & Wilhelm Grimm"));
        STORIES.put("snow_white",    new StoryData("Snow White", "Schneewittchen", "Jacob & Wilhelm Grimm"));
        STORIES.put("three_little_pigs",    new StoryData("The Three Little Pigs", "Die drei kleinen Schweinchen", "Traditional English"));
        STORIES.put("jack_and_the_beanstalk",    new StoryData("Jack and the Beanstalk", "Jack and the Beanstalk", "Traditional English"));
        STORIES.put("aurora",     new StoryData("The Sleeping Beauty", "Dornröschen", "Jacob & Wilhelm Grimm"));
        STORIES.put("the_little_mermaid",   new StoryData("The Little Mermaid", "Den lille Havfrue", "Hans Christian Andersen"));
        STORIES.put("rapunzel",       new StoryData("Rapunzel", "Rapunzel", "Jacob & Wilhelm Grimm"));
    }

    public StoryBook(Properties settings, String storyType) {
        super(settings);
        this.storyType = storyType;
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = new ItemStack(this);
        StoryData data = STORIES.getOrDefault(storyType,
                new StoryData("Untitled Story", "Unknown", "Unknown")
        );

        Language language = Language.getInstance();
        String titleKey = "storybook." + storyType + ".title";
        String ogTitleKey = "storybook." + storyType + ".ogTitle";
        String authorKey = "storybook." + storyType + ".author";
        String textKey = "storybook." + storyType + ".text";

        String finalTitle = language.has(titleKey) ? language.getOrDefault(titleKey) : data.title();
        String finalOgTitle = language.has(ogTitleKey) ? language.getOrDefault(ogTitleKey) : data.ogTitle();
        String finalAuthor = language.has(authorKey) ? language.getOrDefault(authorKey) : data.author();

        String translatedText = language.getOrDefault(textKey);
        List<String> finalPages;

        if (!translatedText.equals(textKey)) {
            finalPages = ModUtils.splitIntoPages(translatedText, 114, 14);
        } else {
            finalPages = Collections.singletonList("The pages of this book are missing or unreadable.");
        }

        List<Filterable<Component>> pageList = new ArrayList<>();
        for (String pageText : finalPages) {
            pageList.add(Filterable.passThrough(Component.literal(pageText)));
        }

        stack.set(DataComponents.WRITTEN_BOOK_CONTENT, new WrittenBookContent(
                Filterable.passThrough(finalTitle),
                finalAuthor,
                0,
                pageList,
                true
        ));

        CompoundTag customData = new CompoundTag();
        customData.putString("ogTitle", finalOgTitle);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(customData));

        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);

        if (world.isClientSide) {
            Minecraft.getInstance()
                    .setScreen(new BookCoverScreen(stack));
        }

        return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        WrittenBookContent content = stack.get(DataComponents.WRITTEN_BOOK_CONTENT);
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);

        if (content != null) {
            String ogTitle = customData != null ? customData.copyTag().getString("ogTitle") : "";
            if (!ogTitle.isEmpty()) {
                tooltip.add(Component.literal(ogTitle));
            }
            tooltip.add(Component.literal("Author: " + content.author()));
        } else {
            tooltip.add(Component.literal("Storybook (Unknown)"));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    public record StoryData(String title, String ogTitle, String author) {}
}