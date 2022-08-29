package de.j3ramy.edomui.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;

public class CenteredHorizontalLine extends Widget {

    private final int thickness, color;

    public CenteredHorizontalLine(int y, int width, int thickness, int color) {
        super(0, y, width, thickness);

        this.leftPos = this.screenWidth / 2 - this.width / 2;
        this.thickness = this.height;
        this.color = color;
    }

    public void render(MatrixStack matrixStack) {
        if(this.isHidden())
            return;

        AbstractGui.fill(matrixStack, this.leftPos, this.topPos, this.leftPos + this.width, this.topPos + this.thickness, this.color);
    }
}
