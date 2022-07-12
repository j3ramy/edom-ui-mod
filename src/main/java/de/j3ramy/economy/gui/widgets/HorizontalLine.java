package de.j3ramy.economy.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

public class HorizontalLine extends Screen {

    public static int HEIGHT = 1;

    private final int x;
    private final int y;
    private final int lineWidth;
    private final int color;

    public HorizontalLine(int x, int y, int lineWidth, int color) {
        super(new StringTextComponent(""));

        this.x = x;
        this.y = y;
        this.lineWidth = lineWidth;
        this.color = color;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        AbstractGui.fill(matrixStack, this.x, this.y, this.x + this.lineWidth, this.y + HEIGHT, this.color);
    }
}
