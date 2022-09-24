package de.j3ramy.edomui.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.j3ramy.edomui.utils.Color;
import de.j3ramy.edomui.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;

public class Button extends Widget {
    protected final int yOffset;
    public int hoverBackgroundColor = Color.GRAY;
    public int hoverTextColor = Color.WHITE;
    public int hoverBorderColor = Color.DARK_GRAY;

    public int disabledBackgroundColor = Color.DARK_GRAY;
    public int disabledTextColor = Color.GRAY;
    public int disabledBorderColor = Color.BLACK;

    public String title;
    public boolean isEnabled = true, isCheckbox, isDropDownButton, isTitleHidden;
    protected IClickable clickAction;


    public Button(int x, int y, int width, int height, String title, IClickable clickAction){
        super(x, y, width, height);

        this.backgroundColor = Color.DARK_GRAY;
        this.borderColor = Color.BLACK;
        this.textColor = Color.WHITE;

        this.title = title;
        this.clickAction = clickAction;
        this.yOffset = GuiUtils.LETTER_HEIGHT / 2 + 1;
    }

    @Override
    public void render(MatrixStack matrixStack) {
        if(this.isHidden)
            return;

        super.render(matrixStack);

        if(this.isEnabled){
            if(this.isMouseOver()){
                AbstractGui.fill(matrixStack, this.leftPos - this.borderThickness, this.topPos - this.borderThickness,
                        this.leftPos + this.width + this.borderThickness, this.topPos + this.height + this.borderThickness,
                        this.hoverBorderColor);

                AbstractGui.fill(matrixStack, this.leftPos, this.topPos, this.leftPos + this.width, this.topPos + this.height, this.hoverBackgroundColor);

                if(!this.isCheckbox && !this.isTitleHidden)
                    Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.title,
                        this.isDropDownButton ? this.leftPos + 5 : this.leftPos + this.width / 2f - font.getStringWidth(this.title) / 2f,
                        this.topPos + this.height / 2f - yOffset,
                        this.hoverTextColor);
            }
            else{
                AbstractGui.fill(matrixStack, this.leftPos - this.borderThickness, this.topPos - this.borderThickness,
                        this.leftPos + this.width + this.borderThickness, this.topPos + this.height + this.borderThickness,
                        this.borderColor);

                AbstractGui.fill(matrixStack, this.leftPos, this.topPos, this.leftPos + this.width, this.topPos + this.height, this.backgroundColor);

                if(!this.isCheckbox && !this.isTitleHidden)
                    Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.title,
                        this.isDropDownButton ? this.leftPos + 5 : this.leftPos + this.width / 2f - font.getStringWidth(this.title) / 2f,
                        this.topPos + this.height / 2f - yOffset,
                        this.textColor);
            }
        }
        else{
            AbstractGui.fill(matrixStack, this.leftPos - this.borderThickness, this.topPos - this.borderThickness,
                    this.isDropDownButton ? this.leftPos + 5 : this.leftPos + this.width + this.borderThickness, this.topPos + this.height + this.borderThickness,
                    this.disabledBorderColor);

            AbstractGui.fill(matrixStack, this.leftPos, this.topPos, this.leftPos + this.width, this.topPos + this.height, this.disabledBackgroundColor);

            if(!this.isCheckbox && !this.isTitleHidden)
                Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.title,
                    this.leftPos + this.width / 2f - font.getStringWidth(this.title) / 2f,
                    this.topPos + this.height / 2f - yOffset,
                    this.disabledTextColor);
        }
    }

    public void onClick(){
        if(this.isHidden)
            return;

        if(this.isMouseOver() && this.isEnabled)
            this.onInteract();
    }

    protected boolean isMouseOver(){
        return this.mousePosition.x > this.leftPos && this.mousePosition.x < this.leftPos + this.width &&
                this.mousePosition.y > this.topPos && this.mousePosition.y < this.topPos + this.height;
    }

    public void onInteract() {
        this.clickAction.onInteract();
    }

    public interface IClickable{
        void onInteract();
    }
}
