package de.j3ramy.edomui.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.j3ramy.edomui.EdomUiMod;
import de.j3ramy.edomui.utils.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;

public class AlertPopUp extends Widget {
    public enum ColorType{
        DEFAULT,
        NOTICE,
        ERROR
    }

    private final Button closeButton;
    private final ColorType colorType;
    private final String title, content;


    public AlertPopUp(int x, int y, int width, int height, String title, String content, String closeButtonContent, AlertPopUp.ColorType type){
        super(x, y, width, height);

        this.mousePosition = new Point();

        this.title = title;
        this.content = content;
        this.colorType = type;

        int BUTTON_HEIGHT = 14;
        int BUTTON_WIDTH = 60;
        this.closeButton = new Button((this.leftPos + this.width / 2) - BUTTON_WIDTH / 2, this.topPos + this.height - BUTTON_HEIGHT - 20, BUTTON_WIDTH, BUTTON_HEIGHT,
                closeButtonContent, ()->setHidden(true));
    }


    @Override
    public void render(MatrixStack matrixStack){
        if(this.isHidden)
            return;

        super.render(matrixStack);
        this.renderBackground(matrixStack);

        //border
        int DEFAULT_COLOR = Color.DARK_GRAY;
        int NOTICE_COLOR = Color.ORANGE;
        int ERROR_COLOR = Color.RED;
        switch (this.colorType){
            case DEFAULT: AbstractGui.fill(matrixStack, leftPos, topPos, leftPos + this.width, topPos + this.height, DEFAULT_COLOR); break;
            case NOTICE: AbstractGui.fill(matrixStack, leftPos, topPos, leftPos + this.width, topPos + height, NOTICE_COLOR); break;
            case ERROR: AbstractGui.fill(matrixStack, leftPos, topPos, leftPos + this.width, topPos + height, ERROR_COLOR);
        }

        //background
        int CONTENT_MARGIN = 5;
        AbstractGui.fill(matrixStack, leftPos + CONTENT_MARGIN, topPos + CONTENT_MARGIN + 10,
                leftPos + this.width - CONTENT_MARGIN, topPos + CONTENT_MARGIN + this.height - 10,
                this.backgroundColor);

        //title text
        AbstractGui.drawCenteredString(matrixStack, this.font, this.title, (this.leftPos + this.width / 2), topPos + 4, this.textColor);

        //content text
        GlStateManager.pushMatrix();
        GlStateManager.scalef(.5f, .5f, .5f);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.content,
                (this.leftPos + this.width / 2f - (font.getStringWidth(this.title) / 2f) / 2f),
                (topPos + 30) * 2,
                this.textColor);
        GlStateManager.popMatrix();


        //button
        closeButton.render(matrixStack);
    }

    @Override
    public void update(int x, int y) {
        if(this.isHidden())
            return;

        super.update(x, y);

        this.closeButton.update(x, y);
    }

    public void onClick(){
        this.closeButton.onClick();
    }

}

