package de.j3ramy.economy.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

public class CenteredHorizontalLine extends Screen {

    public static int HEIGHT = 1;


    private final int screenWidth;
    private final int y;
    private final int lineWidth;
    private final int color;

    public CenteredHorizontalLine(int screenWidth, int y, int lineWidth, int color) {
        super(new StringTextComponent(""));

        this.screenWidth = screenWidth;
        this.y = y;
        this.lineWidth = lineWidth;
        this.color = color;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        int x = this.screenWidth / 2 - this.lineWidth / 2;
        AbstractGui.fill(matrixStack, x, this.y, x + this.lineWidth, this.y + HEIGHT, this.color);
    }
}
