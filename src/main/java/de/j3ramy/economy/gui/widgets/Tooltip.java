package de.j3ramy.economy.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.j3ramy.economy.utils.Color;
import de.j3ramy.economy.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;

public class Tooltip {

    private String content;
    public boolean isVisible = false;

    public Tooltip(String content){
        this.content = content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY){
        if(this.isVisible && this.content != null && !this.content.isEmpty()){
            //border
            AbstractGui.fill(matrixStack,
                    mouseX - 3,
                    mouseY - 3 - 7,
                    mouseX + (this.content.length() * GuiUtils.LETTER_SIZE / 2) + 10,
                    mouseY + GuiUtils.LETTER_SIZE + 4 - 8,
                    Color.BLACK_HEX);

            //background
            AbstractGui.fill(matrixStack,
                    mouseX - 2,
                    mouseY - 2 - 7,
                    mouseX + (this.content.length() * GuiUtils.LETTER_SIZE / 2) + 9,
                    mouseY + GuiUtils.LETTER_SIZE + 3 - 8,
                    Color.LIGHT_GRAY_HEX);

            GlStateManager.pushMatrix();
            GlStateManager.scalef(.5f, .5f, .5f);
            Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.content, (mouseX + 1) * 2, (mouseY - 6) * 2, Color.WHITE);
            GlStateManager.popMatrix();
        }
    }
}
