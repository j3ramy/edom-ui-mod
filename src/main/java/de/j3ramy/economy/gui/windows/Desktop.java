package de.j3ramy.economy.gui.windows;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.j3ramy.economy.gui.widgets.Widget;
import de.j3ramy.economy.utils.Color;
import net.minecraft.client.gui.AbstractGui;

public class Desktop extends Widget {

    public Desktop(int width, int height) {
        super(0, 0, width, height);

        this.leftPos = this.screenWidth / 2 - this.width / 2;
        this.topPos = this.screenHeight / 2 - this.height / 2;

        this.setBorderColor(Color.BLACK_HEX);
    }

    public void render(MatrixStack matrixStack) {
        //border
        AbstractGui.fill(matrixStack, this.leftPos - this.borderThickness, this.topPos - this.borderThickness,
                this.leftPos + this.width + this.borderThickness, this.topPos + this.height + this.borderThickness, this.borderColor);

        //background
        AbstractGui.fill(matrixStack, this.leftPos, this.topPos, this.leftPos + this.width, this.topPos + this.height, this.backgroundColor);
    }
}
