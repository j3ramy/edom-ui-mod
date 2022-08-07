package de.j3ramy.edomui.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;

public class HorizontalLine extends Widget {

    private final int thickness;
    private final int color;

    public HorizontalLine(int x, int y, int width, int thickness, int color) {
        super(x, y, width, thickness);

        this.thickness = this.height;
        this.color = color;
    }

    public void render(MatrixStack matrixStack) {
        if(this.isHidden())
            return;

        AbstractGui.fill(matrixStack, this.leftPos, this.topPos, this.leftPos + this.width, this.topPos + this.thickness, this.color);
    }
}
