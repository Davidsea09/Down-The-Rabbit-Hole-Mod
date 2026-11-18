package net.emanueljdf09.dtrhmod.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class CustomTexturedButton extends Button {
    private final ResourceLocation texture;
    private final int u;
    private final int v;
    private final int hoveredVOffset;
    private final int textureWidth;
    private final int textureHeight;

    public CustomTexturedButton(int x, int y, int width, int height, int u, int v, int hoveredVOffset, ResourceLocation texture, int textureWidth, int textureHeight, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.hoveredVOffset = hoveredVOffset;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        int currentV = this.v;
        if (this.isHovered()) {
            currentV += this.hoveredVOffset;
        }

        context.blit(
                this.texture,
                this.getX(), this.getY(),
                this.width, this.height,
                (float) this.u, (float) currentV,
                this.width, this.height,
                this.textureWidth, this.textureHeight
        );
    }
}