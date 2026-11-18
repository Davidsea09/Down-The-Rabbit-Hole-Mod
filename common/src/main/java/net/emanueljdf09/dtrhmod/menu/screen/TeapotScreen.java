package net.emanueljdf09.dtrhmod.menu.screen;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.menu.handler.TeapotScreenHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class TeapotScreen extends AbstractContainerScreen<TeapotScreenHandler> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/gui/teapot_screen.png");

    public TeapotScreen(TeapotScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);

        this.imageWidth = 176;
        this.imageHeight = 166;

        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        renderTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        context.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderProgressArrow(context, x, y);
    }

    private void renderProgressArrow(GuiGraphics context, int x, int y) {
        if (menu.isCrafting()) {
            context.blit(TEXTURE, x + 91, y + 35, 176, 14, menu.getScaledProgress(24), 17);
        }
    }
}

