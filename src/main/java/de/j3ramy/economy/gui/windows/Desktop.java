package de.j3ramy.economy.gui.windows;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;

public class Desktop {

    private final int w;
    private final int h;
    private final int borderThickness;
    private final int desktopColor;
    private final int borderColor;

    public Desktop(int width, int height, int borderThickness, int desktopColor, int borderColor) {
        this.w = width;
        this.h = height;
        this.borderThickness = borderThickness;
        this.desktopColor = desktopColor;
        this.borderColor = borderColor;
    }

    public void render(ContainerScreen<?> screen, MatrixStack matrixStack) {
        int x = screen.width / 2 - this.w / 2;
        int y = screen.height / 2 - this.h / 2;

        //border
        AbstractGui.fill(matrixStack, x - this.borderThickness, y - borderThickness,
                x + this.w + borderThickness, y + this.h + borderThickness, this.borderColor);

        //background
        AbstractGui.fill(matrixStack, x, y, x + this.w, y + this.h, this.desktopColor);
    }
}
