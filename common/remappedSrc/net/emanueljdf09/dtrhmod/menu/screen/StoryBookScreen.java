package net.emanueljdf09.dtrhmod.menu.screen;

import com.google.common.collect.ImmutableList;

import com.mojang.datafixers.util.Pair;
import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.fabric.item.ModItems;
import net.emanueljdf09.dtrhmod.fabric.util.CustomTexturedButton;
import net.emanueljdf09.dtrhmod.fabric.util.ModUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.component.DataComponents;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.text.*;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WrittenBookContent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class StoryBookScreen extends Screen {

    public static final Contents EMPTY_PROVIDER = new Contents() {
        @Override
        public int getPageCount() {
            return 0;
        }

        @Override
        public FormattedText getPageUnchecked(int index) {
            return FormattedText.EMPTY;
        }
    };
    public static ResourceLocation bookTexture;
    public static final ResourceLocation BOOK_TEXTURE = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/gui/storybooks/book_middle.png");
    public static final int TEXT_LEFT_X = 22;
    public static final int TEXT_RIGHT_X = 159;
    public static final int TEXT_Y = 21;
    public static final int TEXT_WIDTH = 114;
    public static final int TEXT_HEIGHT = 128;
    public static final int MAX_LINES = 14;
    protected static final int WIDTH = 295;
    protected static final int HEIGHT = 180;
    public Contents contents;
    private Pair<List<FormattedCharSequence>, List<FormattedCharSequence>> cachedPage;
    private int cachedSpread;
    protected int leftPos;
    protected int topPos;
    private Button nextPageButton;
    private Button previousPageButton;
    protected int currentSpread;

    public StoryBookScreen(Contents contents, ItemStack stack) {
        super(GameNarrator.NO_TITLE);
        this.contents = contents;
        this.cachedPage = Pair.of(Collections.emptyList(), Collections.emptyList());
        this.cachedSpread = -1;

        if (stack.is(ModItems.AURORA_STORYBOOK)) {
            bookTexture = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/gui/storybooks/aurora_open.png");
        } else if (stack.is(ModItems.CINDERELLA_STORYBOOK)) {
            bookTexture = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/gui/storybooks/cinder_open.png");
        } else if (stack.is(ModItems.RED_RIDING_HOOD_STORYBOOK)) {
            bookTexture = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/gui/storybooks/lrrh_open.png");
        } else if (stack.is(ModItems.THE_LITTLE_MERMAID_STORYBOOK)) {
            bookTexture = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/gui/storybooks/seashell_open.png");
        } else if (stack.is(ModItems.JACK_AND_THE_BEANSTALK_STORYBOOK)) {
            bookTexture = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/gui/storybooks/jb_open.png");
        } else if (stack.is(ModItems.RAPUNZEL_STORYBOOK)) {
            bookTexture = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/gui/storybooks/hair_open.png");
        } else if (stack.is(ModItems.THREE_LITTLE_PIGS_STORYBOOK)) {
            bookTexture = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/gui/storybooks/tlp_open.png");
        } else if (stack.is(ModItems.SNOW_WHITE_STORYBOOK)) {
            bookTexture = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/gui/storybooks/sw_open.png");
        } else {
            bookTexture = BOOK_TEXTURE;
        }
    }

    private String triggerKey = null;

    public StoryBookScreen(String triggerKey) {
        super(GameNarrator.NO_TITLE);
        this.cachedPage = Pair.of(Collections.emptyList(), Collections.emptyList());
        this.cachedSpread = -1;

        if ("wonderland_intro".equals(triggerKey)) {
            bookTexture = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/gui/storybooks/enter_wonder_open.png");
            this.contents = createTranslatableContents("storybook.wonderland.intro.text");
        } else if ("mad_hatter_intro".equals(triggerKey)) {
            bookTexture = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/gui/storybooks/enter_wonder_open.png");
            this.contents = createTranslatableContents("storybook.mad_hatter.intro.text");
        } else {
            bookTexture = BOOK_TEXTURE;
            this.contents = EMPTY_PROVIDER;
        }
    }



    protected static Contents createTranslatableContents(String textKey) {
        String rawText = Language.getInstance().getOrDefault(textKey);
        Font renderer = Minecraft.getInstance().font;

        List<FormattedText> formattedPages = new ArrayList<>();
        for (String page : ModUtils.splitIntoPages(rawText, TEXT_WIDTH, MAX_LINES)) {
            formattedPages.add(Component.literal(page));
        }
        if (formattedPages.isEmpty()) {
            formattedPages.add(Component.empty());
        }

        return new Contents() {
            @Override
            public int getPageCount() {
                return formattedPages.size();
            }

            @Override
            public FormattedText getPageUnchecked(int index) {
                return formattedPages.get(index);
            }
        };
    }


    public Contents getBookAccess() {
        return this.contents;
    }

    public boolean setPage(int index) {
        index = Mth.clamp(index, 0, this.getBookAccess().getPageCount() - 1);
        int spreadIndex = (int)((float)index / 2.0F);
        if (spreadIndex != this.currentSpread) {
            this.currentSpread = spreadIndex;
            this.updatePageButtons();
            this.cachedSpread = -1;
            return true;
        } else {
            return false;
        }
    }

    protected boolean jumpToPage(int page) {
        return this.setPage(page);
    }

    @Override
    protected void init() {
        this.addPageButtons();
    }

    protected void addPageButtons() {
        this.leftPos = (this.width - 295) / 2;
        this.topPos = (this.height - 180) / 2;
        createWidgets();
        updatePageButtons();
    }

    protected void createWidgets() {
        this.createPrevPageButton();
        this.createNextPageButton();
        updatePageButtons();
    }

    protected void createNextPageButton() {
        this.nextPageButton = this.addRenderableWidget(new CustomTexturedButton(this.leftPos + 270, this.topPos + 156, 13, 15, 308, 0, 15, bookTexture, 512, 512,  (button) -> this.goToNextPage()));
        nextPageButton.setTooltip(Tooltip.create(Component.translatable("spectatorMenu.next_page")));
    }

    protected void createPrevPageButton() {
        this.previousPageButton = this.addRenderableWidget(new CustomTexturedButton(this.leftPos + 12, this.topPos + 156, 13, 15, 295, 0, 15, bookTexture, 512, 512, (button) -> this.goToPreviousPage()));
        previousPageButton.setTooltip(Tooltip.create(Component.translatable("spectatorMenu.previous_page")));
    }

    private int getPageCount() {
        return this.contents.getPageCount();
    }

    public int getSpreadCount() {
        int totalTextPages = this.getPageCount();
        if (totalTextPages <= 0) return 1;

        int finalPageIdx = totalTextPages - 1;
        int finalTextSpread = finalPageIdx / 2;
        boolean endedOnLeftPage = (finalPageIdx % 2 == 0);

        if (!endedOnLeftPage) {
            return finalTextSpread + 2;
        }

        return finalTextSpread + 1;
    }

    protected boolean goToPreviousPage() {
        if (this.currentSpread > 0) {
            this.currentSpread--;
            this.playPageTurnSound(0.8F);
            this.updatePageButtons();
            return true;
        }
        return false;
    }

    protected boolean goToNextPage() {
        if (this.currentSpread < this.getSpreadCount() - 1) {
            this.currentSpread++;
            this.playPageTurnSound(1.0F);
            this.updatePageButtons();
            return true;
        }
        return false;
    }

    private void updatePageButtons() {
        this.nextPageButton.visible = this.currentSpread < this.getSpreadCount() - 1;
        this.previousPageButton.visible = this.currentSpread > 0;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!(this.getFocused() instanceof EditBox)) {
            if (Minecraft.getInstance().options.keyInventory.matches(keyCode, scanCode)) {
                this.onClose();
                return true;
            }

            if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_PAGE_UP
                    || Minecraft.getInstance().options.keyLeft.matches(keyCode, scanCode)) {
                this.goToPreviousPage();
                return true;
            }

            if (keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == GLFW.GLFW_KEY_PAGE_DOWN
                    || Minecraft.getInstance().options.keyRight.matches(keyCode, scanCode)) {
                this.goToNextPage();
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        this.updatePageButtons();
        this.renderBackground(context, mouseX, mouseY, delta);
        this.renderBook(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        this.renderPageNumbers(context, mouseX, mouseY, delta, this.currentSpread);
        this.updateAndCacheContentsIfNeeded();
        this.renderPageContents(context, this.cachedPage.getFirst(), this.leftPos + 22, this.topPos + 21);
        this.renderPageContents(context, this.cachedPage.getSecond(), this.leftPos + 159, this.topPos + 21);

        this.renderLastPages(context);

        Style style = this.getClickedComponentStyleAt(mouseX, mouseY);
        if (style != null) {
            context.renderComponentHoverEffect(this.font, style, mouseX, mouseY);
        }
    }

    public void renderBook(GuiGraphics context, int mouseX, int mouseY, float partialTick) {
        context.blit(bookTexture, this.leftPos, this.topPos, 295, 180, 0.0F, 0.0F, 295, 180, 512, 512);
    }

    @Override
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {

    }

    public void renderLastPages(GuiGraphics context) {
        int totalTextPages = this.getPageCount();
        if (totalTextPages <= 0) return;

        int finalPageIdx = totalTextPages - 1;
        int finalTextSpread = finalPageIdx / 2;
        boolean endedOnLeftPage = (finalPageIdx % 2 == 0);

        if (endedOnLeftPage) {
            if (this.currentSpread == finalTextSpread) {
                int endX = this.leftPos + 180;
                int endY = this.topPos + 50;
                context.blit(bookTexture, endX, endY, 372, 57, 72, 60, 512, 512);
            }
        } else {
            if (this.currentSpread == finalTextSpread + 1) {
                int endX = this.leftPos + 42;
                int endY = this.topPos + 50;
                context.blit(bookTexture, endX, endY, 372, 57, 72, 60, 512, 512);
            }
        }
    }

    public void renderPageNumbers(GuiGraphics context, int mouseX, int mouseY, float partialTick, int currentSpread) {
        this.renderLeftPageNumber(context, mouseX, mouseY, partialTick, currentSpread);
        this.renderRightPageNumber(context, mouseX, mouseY, partialTick, currentSpread);
    }
    protected void renderLeftPageNumber(GuiGraphics context, int mouseX, int mouseY, float partialTick, int currentSpread) {
        String leftPageNumber = Integer.toString(currentSpread * 2 + 1);
        context.drawString(this.font, leftPageNumber, this.leftPos + 69 + (8 - this.font.width(leftPageNumber) / 2), this.topPos + 157, 0x000000, false);
    }

    protected void renderRightPageNumber(GuiGraphics context, int mouseX, int mouseY, float partialTick, int currentSpread) {
        String rightPageNumber = Integer.toString(currentSpread * 2 + 2);
        context.drawString(this.font, rightPageNumber, this.leftPos + 208 + (8 - this.font.width(rightPageNumber) / 2), this.topPos + 157, 0x000000, false);
    }

    protected void updateAndCacheContentsIfNeeded() {
        if (this.cachedSpread != this.currentSpread) {
            FormattedText leftFormattedText = this.getBookAccess().getPage(this.currentSpread * 2);
            FormattedText rightFormattedText = this.getBookAccess().getPageCount() > this.currentSpread * 2 + 1
                    ? this.getBookAccess().getPage(this.currentSpread * 2 + 1)
                    : Component.empty();
            this.cachedPage = Pair.of(
                    this.font.split(leftFormattedText, TEXT_WIDTH),
                    this.font.split(rightFormattedText, TEXT_WIDTH)
            );
            this.cachedSpread = this.currentSpread;
        }
    }

    protected void renderPageContents(GuiGraphics guiGraphics, List<FormattedCharSequence> lines, int x, int y) {
        Objects.requireNonNull(this.font);
        int maxLines = Math.min(MAX_LINES, lines.size());

        for (int i = 0; i < maxLines; ++i) {
            FormattedCharSequence text = lines.get(i);
            Font renderer = this.font;
            guiGraphics.drawString(renderer, text, x, y + i * 9, 0x000000, false);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            Style style = this.getClickedComponentStyleAt(mouseX, mouseY);
            if (style != null && this.handleComponentClicked(style)) {
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean handleComponentClicked(Style style) {
        assert style != null;
        ClickEvent clickEvent = style.getClickEvent();
        if (clickEvent == null) {
            return false;
        } else if (clickEvent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
            String string = clickEvent.getValue();

            try {
                int i = Integer.parseInt(string) - 1;
                return this.jumpToPage(i);
            } catch (Exception var5) {
                return false;
            }
        } else {
            boolean bl = super.handleComponentClicked(style);
            if (bl && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                this.closeScreen();
            }

            return bl;
        }
    }

    protected void closeScreen() {
        assert this.minecraft != null;
        this.minecraft.setScreen(null);
    }

    protected void playPageTurnSound(float pitch) {
        Minecraft.getInstance().getSoundManager()
                .play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, pitch, 1.0F));
    }

    @Nullable
    public Style getClickedComponentStyleAt(double mouseX, double mouseY) {
        if (!(mouseY < (double) (this.topPos + 21)) && !(mouseY >= (double) (this.topPos + 21 + 128))) {
            boolean isOverRightPage;

            if (mouseX >= (double) (this.leftPos + 159) && mouseX < (double) (this.leftPos + 159 + 114)) {
                isOverRightPage = true;
            } else {
                if (!(mouseX >= (double) (this.leftPos + 22)) || !(mouseX < (double) (this.leftPos + 22 + 114))) {
                    return null;
                }
                isOverRightPage = false;
            }

            List<FormattedCharSequence> pageContents = isOverRightPage
                    ? this.cachedPage.getSecond()
                    : this.cachedPage.getFirst();

            if (pageContents.isEmpty()) {
                return null;
            } else {
                int x = (int) mouseX - (this.leftPos + (isOverRightPage ? 159 : 22));
                int y = (int) mouseY - (this.topPos + 21);
                Objects.requireNonNull(this.font);

                int linesCount = Math.min(MAX_LINES, pageContents.size());

                if (y < 9 * linesCount + linesCount) {
                    int clickedLine = y / 9;
                    if (clickedLine >= 0 && clickedLine < pageContents.size()) {
                        FormattedCharSequence text = pageContents.get(clickedLine);
                        return this.font.getSplitter().componentStyleAtWidth(text, x);
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    @Environment(EnvType.CLIENT)
    public interface Contents {
        int getPageCount();

        FormattedText getPageUnchecked(int index);

        default FormattedText getPage(int index) {
            return index >= 0 && index < this.getPageCount() ? this.getPageUnchecked(index) : FormattedText.EMPTY;
        }
    }

    @Environment(EnvType.CLIENT)
    public static class WrittenBookContents implements Contents {
        private final List<FormattedText> pages;

        public WrittenBookContents(ItemStack stack) {
            this.pages = getPages(stack);
        }

        private static List<FormattedText> getPages(ItemStack stack) {
            WrittenBookContent content = stack.get(DataComponents.WRITTEN_BOOK_CONTENT);
            if (content != null) {
                List<FormattedText> list = new ArrayList<>();
                for (var pagePair : content.pages()) {
                    Component pageText = pagePair.raw();
                    list.add(pageText != null ? pageText : FormattedText.EMPTY);
                }
                return list;
            }
            return ImmutableList.of(Component.translatable("book.invalid.tag").withStyle(ChatFormatting.DARK_RED));
        }

        @Override
        public int getPageCount() {
            return this.pages.size();
        }

        @Override
        public @NotNull FormattedText getPageUnchecked(int index) {
            return index >= 0 && index < this.pages.size() ? this.pages.get(index) : FormattedText.EMPTY;
        }
    }
}