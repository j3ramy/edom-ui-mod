package de.j3ramy.economy.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.j3ramy.economy.utils.screen.Color;
import de.j3ramy.economy.utils.screen.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;

public class Tooltip {

    private String content;
    public boolean isVisible = true;

    public Tooltip(String content){
        this.content = content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void show(){
        this.isVisible = true;
    }

    public void hide(){
        this.isVisible = false;
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY){
        if(this.isVisible){
            //border
            AbstractGui.fill(matrixStack,
                    mouseX - 3,
                    mouseY - 3 - 7,
                    mouseX + (this.content.length() * GuiUtils.LETTER_SIZE / 2) + 6,
                    mouseY + GuiUtils.LETTER_SIZE + 4 - 8,
                    Color.BLACK_HEX);

            //background
            AbstractGui.fill(matrixStack,
                    mouseX - 2,
                    mouseY - 2 - 7,
                    mouseX + (this.content.length() * GuiUtils.LETTER_SIZE / 2) + 5,
                    mouseY + GuiUtils.LETTER_SIZE + 3 - 8,
                    Color.LIGHT_GRAY_HEX);

            GlStateManager.pushMatrix();
            GlStateManager.scalef(.5f, .5f, .5f);
            Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.content, (mouseX + 1) * 2, (mouseY - 6) * 2, Color.WHITE);
            GlStateManager.popMatrix();
        }
    }
}
