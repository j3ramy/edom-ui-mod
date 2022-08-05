
package de.j3ramy.economy.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.j3ramy.economy.EconomyMod;
import de.j3ramy.economy.utils.Color;
import de.j3ramy.economy.utils.GuiUtils;
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


    public ConfirmPopUp(int x, int y, int width, int height, String title, String content, ConfirmPopUp.ColorType type, Button.IClickable confirmAction){
        super(x, y, width, height);

        this.title = title;
        this.content = content;
        this.colorType = type;

        int BUTTON_HEIGHT = 14;
        int BUTTON_WIDTH = 40;
        this.yesButton = new Button(this.leftPos + 20, this.topPos + this.height - BUTTON_HEIGHT - 20, BUTTON_WIDTH, BUTTON_HEIGHT,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".button.yes").getString(), confirmAction);

        this.noButton = new Button(this.leftPos + this.width - BUTTON_WIDTH - 20, this.topPos + this.height - BUTTON_HEIGHT - 20, BUTTON_WIDTH, BUTTON_HEIGHT,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".button.no").getString(), ()->this.setHidden(true));
    }

    public void render(MatrixStack matrixStack){
        if(this.isHidden)
            return;

        this.renderBackground(matrixStack);

        //border
        int DEFAULT_COLOR = Color.DARK_GRAY_HEX;
        int NOTICE_COLOR = Color.ORANGE_HEX;
        int ERROR_COLOR = Color.RED_HEX;
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
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.content,
                (this.leftPos + this.width / 2f) * 2 - GuiUtils.getCenteredTextOffset(this.content.length()),
                (topPos + 30) * 2,
                this.textColor);
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

