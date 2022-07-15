/*
* UPDATE LATER:
* - Two Font Sizes (as in GuiUtils) for button text
* - Umlaute äüö im Button anzeigen
* */
package de.j3ramy.economy.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.j3ramy.economy.utils.GuiUtils;
import de.j3ramy.economy.utils.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.text.ITextComponent;

import java.awt.*;

public class Button extends net.minecraft.client.gui.widget.button.Button {
    public static final int DEFAULT_COLOR = Color.DARK_GRAY_HEX;
    public static final int HOVER_COLOR = Color.LIGHT_GRAY_HEX;
    public static final int TEXT_COLOR = Color.WHITE;
    public static final int BORDER_COLOR_DEFAULT = Color.BLACK_HEX;
    public static final int BORDER_COLOR_HOVER = DEFAULT_COLOR;
    public static final int BORDER_THICKNESS = 1;
    public static final int TEXT_Y_OFFSET = 4;
    private final Point mousePosition;

    public Button(int x, int y, int width, int height, ITextComponent title, Button.IPressable onPress){
        super(x, y, width, height, title, onPress);

        this.mousePosition = new Point();
    }

    public String getText(){
        return this.getMessage().getString();
    }

    public void updateMousePosition(int mouseX, int mouseY){
        this.mousePosition.x = mouseX;
        this.mousePosition.y = mouseY;
    }

    @Override
    public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if(this.mousePosition == null)
            return;

        //render background and border
        if(isMouseOver(mousePosition.x, mousePosition.y) && this.active)
            this.onHover(matrixStack);
        else{
            AbstractGui.fill(matrixStack, this.x - BORDER_THICKNESS, this.y - BORDER_THICKNESS,
                    this.x + this.width + BORDER_THICKNESS, this.y + this.height + BORDER_THICKNESS,
                    BORDER_COLOR_DEFAULT);

            AbstractGui.fill(matrixStack, this.x, this.y, this.x + this.width, this.y + this.height, DEFAULT_COLOR);
        }

        //render text
        if(this.active){
            Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.getMessage().getString(),
                    this.x + this.width / 2f - GuiUtils.getCenteredTextOffset(this.getMessage().getString().length()),
                    this.y + this.height / 2f - TEXT_Y_OFFSET,
                    TEXT_COLOR);
        }
        else{
            Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.getMessage().getString(),
                    this.x + this.width / 2f - GuiUtils.getCenteredTextOffset(this.getMessage().getString().length()),
                    this.y + this.height / 2f - TEXT_Y_OFFSET,
                    HOVER_COLOR);
        }
    }

    public void onClick(){
        if(this.isMouseOver(this.mousePosition.x, this.mousePosition.y) && this.active)
            this.onPress();
    }


    private void onHover(MatrixStack matrixStack){
        AbstractGui.fill(matrixStack, this.x - BORDER_THICKNESS, this.y - BORDER_THICKNESS,
                this.x + this.width + BORDER_THICKNESS, this.y + this.height + BORDER_THICKNESS,
                BORDER_COLOR_HOVER);

        AbstractGui.fill(matrixStack, this.x, this.y, this.x + this.width, this.y + this.height, HOVER_COLOR);
    }
}
