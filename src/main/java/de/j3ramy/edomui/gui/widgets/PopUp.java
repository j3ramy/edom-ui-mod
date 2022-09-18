package de.j3ramy.edomui.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.j3ramy.edomui.utils.Color;
import de.j3ramy.edomui.utils.enums.PopUpColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;

public class PopUp extends Widget {

    protected PopUpColor popUpColor;
    protected String title, content;
    protected int defaultColor, noticeColor, errorColor;

    public PopUp(int x, int y, int width, int height, String title, String content, PopUpColor popUpColor) {
        super(x, y, width, height);

        this.title = title;
        this.content = content;
        this.popUpColor = popUpColor;

        this.backgroundColor = Color.WHITE;
        this.textColor = Color.DARK_GRAY;
        this.defaultColor = Color.DARK_GRAY;
        this.noticeColor = Color.ORANGE;
        this.errorColor = Color.RED;
    }

    public void setDefaultColor(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    public void setNoticeColor(int noticeColor) {
        this.noticeColor = noticeColor;
    }

    public void setErrorColor(int errorColor) {
        this.errorColor = errorColor;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPopUpColor(PopUpColor popUpColor) {
        this.popUpColor = popUpColor;
    }

    public void render(MatrixStack matrixStack){
        if(this.isHidden)
            return;

        this.renderBackground(matrixStack);

        switch (this.popUpColor){
            case DEFAULT: AbstractGui.fill(matrixStack, this.leftPos, this.topPos, this.leftPos + this.width, this.topPos + this.height, this.defaultColor); break;
            case NOTICE: AbstractGui.fill(matrixStack, this.leftPos, this.topPos, this.leftPos + this.width, this.topPos + this.height, this.noticeColor); break;
            case ERROR: AbstractGui.fill(matrixStack, this.leftPos, this.topPos, this.leftPos + this.width, this.topPos + this.height, this.errorColor);
        }

        //background
        int contentMargin = 5;
        AbstractGui.fill(matrixStack, this.leftPos + contentMargin, this.topPos + contentMargin,
                this.leftPos + this.width - contentMargin, this.topPos + contentMargin + this.height - 10,
                this.backgroundColor);

        //title text
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.title, this.leftPos + this.width / 2f - font.getStringWidth(this.title) / 2f,
                this.topPos + 8 + contentMargin, this.textColor);

        //content text
        GlStateManager.pushMatrix();
        GlStateManager.scalef(.5f, .5f, .5f);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.content, (this.leftPos + this.width / 2f - font.getStringWidth(this.content) / 4f) * 2,
                (this.topPos + this.height / 2f) * 2 - 20, this.textColor);
        GlStateManager.popMatrix();
    }

    @Override
    public void update(int x, int y) {
        if(this.isHidden)
            return;

        super.update(x, y);
    }
}
