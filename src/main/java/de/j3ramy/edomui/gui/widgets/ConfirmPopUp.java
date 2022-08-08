
package de.j3ramy.edomui.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.j3ramy.edomui.EdomUiMod;
import de.j3ramy.edomui.utils.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.text.TranslationTextComponent;

public class ConfirmPopUp extends Widget {

    public enum ColorType{
        DEFAULT,
        NOTICE,
        ERROR
    }

    private final Button noButton, yesButton;
    private final ColorType colorType;
    private final String title, content;


    public ConfirmPopUp(int x, int y, int width, int height, String title, String content, String yesButtonContent, String noButtonContent, ConfirmPopUp.ColorType type, Button.IClickable confirmAction){
        super(x, y, width, height);

        this.title = title;
        this.content = content;
        this.colorType = type;

        int BUTTON_HEIGHT = 14;
        int BUTTON_WIDTH = 40;
        this.yesButton = new Button(this.leftPos + 20, this.topPos + this.height - BUTTON_HEIGHT - 20, BUTTON_WIDTH, BUTTON_HEIGHT,
                yesButtonContent, confirmAction);

        this.noButton = new Button(this.leftPos + this.width - BUTTON_WIDTH - 20, this.topPos + this.height - BUTTON_HEIGHT - 20, BUTTON_WIDTH, BUTTON_HEIGHT,
                noButtonContent, ()->this.setHidden(true));
    }

    public void render(MatrixStack matrixStack){
        if(this.isHidden)
            return;

        this.renderBackground(matrixStack);

        //border
        int DEFAULT_COLOR = Color.DARK_GRAY;
        int NOTICE_COLOR = Color.ORANGE;
        int ERROR_COLOR = Color.RED;
        switch (this.colorType){
            case DEFAULT: AbstractGui.fill(matrixStack, leftPos, topPos, leftPos + this.width, topPos + this.height, DEFAULT_COLOR); break;
            case NOTICE: AbstractGui.fill(matrixStack, leftPos, topPos, leftPos + this.width, topPos + this.height, NOTICE_COLOR); break;
            case ERROR: AbstractGui.fill(matrixStack, leftPos, topPos, leftPos + this.width, topPos + this.height, ERROR_COLOR);
        }

        //background
        int CONTENT_MARGIN = 5;
        AbstractGui.fill(matrixStack, leftPos + CONTENT_MARGIN, topPos + CONTENT_MARGIN + 10,
                leftPos + this.width - CONTENT_MARGIN, topPos + CONTENT_MARGIN + this.height - 10,
                this.backgroundColor);

        //title text
        AbstractGui.drawCenteredString(matrixStack, this.font, this.title, this.leftPos + this.width / 2, topPos + 4, this.textColor);

        //content text
        GlStateManager.pushMatrix();
        GlStateManager.scalef(.5f, .5f, .5f);
        AbstractGui.drawCenteredString(matrixStack, this.font, "TEST", this.width / 2 * 2, this.height / 2 * 2 - 20, this.textColor);
        GlStateManager.popMatrix();


        //button
        yesButton.render(matrixStack);
        noButton.render(matrixStack);
    }

    @Override
    public void update(int x, int y) {
        if(this.isHidden())
            return;

        super.update(x, y);

        this.yesButton.update(x, y);
        this.noButton.update(x, y);
    }

    public void onClick(){
        if(this.isHidden())
            return;

        this.yesButton.onClick();
        this.noButton.onClick();
    }
}

