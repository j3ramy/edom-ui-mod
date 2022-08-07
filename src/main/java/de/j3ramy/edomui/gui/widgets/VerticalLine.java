package de.j3ramy.edomui.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;

public class VerticalLine extends Widget {


    private final int thickness;
    private final int color;

    public VerticalLine(int x, int y, int thickness, int height, int color) {
        super(x, y, thickness, height);

        this.thickness = this.width;
        this.color = color;
    }

    public void render(MatrixStack matrixStack) {
        if(this.isHidden())
            return;

        AbstractGui.fill(matrixStack, this.leftPos, this.topPos, this.leftPos + this.thickness, this.topPos + this.height, this.color);
    }
}
