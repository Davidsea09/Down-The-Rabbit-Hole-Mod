package net.emanueljdf09.dtrhmod.menu.screen;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.item.ModItems;
import net.emanueljdf09.dtrhmod.util.CustomTexturedButton;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class BookCoverScreen extends Screen {
    public static ResourceLocation coverTexture;
    public static final ResourceLocation COVER_TEXTURE = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/gui/storybooks/book_first.png");
    private final ItemStack bookStack;
    protected int leftPos;
    protected int topPos;
    private Button openBookButton;

    public BookCoverScreen(ItemStack bookStack) {
        super(GameNarrator.NO_TITLE);
        this.bookStack = bookStack;

        if (bookStack.is(ModItems.AURORA_STORYBOOK.get())) {
            coverTexture = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/gui/storybooks/aurora_cover.png");
        } else if (bookStack.is(ModItems.CINDERELLA_STORYBOOK.get())) {
            coverTexture = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/gui/storybooks/cinder_cover.png");
        } else if (bookStack.is(ModItems.RED_RIDING_HOOD_STORYBOOK.get())) {
            coverTexture = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/gui/storybooks/lrrh_cover.png");
        } else if (bookStack.is(ModItems.THE_LITTLE_MERMAID_STORYBOOK.get())) {
            coverTexture = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/gui/storybooks/seashell_cover.png");
        } else if (bookStack.is(ModItems.JACK_AND_THE_BEANSTALK_STORYBOOK.get())) {
            coverTexture = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/gui/storybooks/jb_cover.png");
        } else if (bookStack.is(ModItems.RAPUNZEL_STORYBOOK.get())) {
            coverTexture = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/gui/storybooks/hair_cover.png");
        } else if (bookStack.is(ModItems.THREE_LITTLE_PIGS_STORYBOOK.get())) {
            coverTexture = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/gui/storybooks/tlp_cover.png");
        } else if (bookStack.is(ModItems.SNOW_WHITE_STORYBOOK.get())) {
            coverTexture = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/gui/storybooks/sw_cover.png");
        } else {
            coverTexture = COVER_TEXTURE;
        }
    }

    private String triggerKey = null;

    public BookCoverScreen(String triggerKey) {
        super(GameNarrator.NO_TITLE);
        this.bookStack = ItemStack.EMPTY;
        this.triggerKey = triggerKey;

        if ("wonderland_intro".equals(triggerKey)) {
            coverTexture = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/gui/storybooks/enter_wonder_cover.png");
        } else if ("mad_hatter_intro".equals(triggerKey)) {
            coverTexture = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/gui/storybooks/enter_wonder_cover.png");
        } else {
            coverTexture = COVER_TEXTURE;
        }
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - 150) / 2;
        this.topPos = (this.height - 180) / 2;
        createOpenBookButton();
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        this.renderCover(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {

    }

    protected void createOpenBookButton() {
        this.openBookButton = this.addRenderableWidget(new CustomTexturedButton(this.leftPos + 22, this.topPos + 67, 107, 33, 149, 0, 37, coverTexture, 256, 256, (button) -> this.openBook()));
        this.openBookButton.setTooltip(Tooltip.create(Component.translatable("cover.title.open")));
    }

    protected void openBook() {
        if (this.triggerKey != null) {
            Minecraft.getInstance().setScreen(new StoryBookScreen(this.triggerKey));
        } else {
            Minecraft.getInstance()
                    .setScreen(new StoryBookScreen(
                            new StoryBookScreen.WrittenBookContents(bookStack), bookStack
                    ));
        }
    }

    public void renderCover(GuiGraphics context, int mouseX, int mouseY, float partialTick) {
        context.blit(coverTexture, this.leftPos, this.topPos, 150, 180, 0.0F, 0.0F, 150, 180, 256, 256);
    }

}
