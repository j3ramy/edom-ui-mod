package de.j3ramy.economy.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.j3ramy.economy.utils.Color;
import de.j3ramy.economy.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;

public class Button extends Widget {
    protected final int yOffset;
    protected int hoverBackgroundColor = Color.DARK_GRAY_HEX;
    protected int hoverTextColor = this.textColor;
    protected int hoverBorderColor = Color.WHITE_HEX;

    protected int disabledBackgroundColor = Color.DARK_GRAY_HEX;
    protected int disabledTextColor = Color.LIGHT_GRAY_HEX;
    protected int disabledBorderColor = Color.LIGHT_GRAY_HEX;

    protected String title;
    protected boolean enabled = true, isDropDownButton;
    protected final IClickable clickAction;


    public Button(int x, int y, int width, int height, String title, IClickable clickAction){
        super(x, y, width, height);

        this.title = title;
        this.clickAction = clickAction;
        this.yOffset = GuiUtils.LETTER_HEIGHT / 2 + 1;
    }


    public void setHoverBackgroundColor(int hoverBackgroundColor) {
        this.hoverBackgroundColor = hoverBackgroundColor;
    }

    public void setHoverBorderColor(int hoverBorderColor) {
        this.hoverBorderColor = hoverBorderColor;
    }

    public void setHoverTextColor(int hoverTextColor) {
        this.hoverTextColor = hoverTextColor;
    }

    public void setDisabledBackgroundColor(int disabledBackgroundColor) {
        this.disabledBackgroundColor = disabledBackgroundColor;
    }

    public void setDisabledBorderColor(int disabledBorderColor) {
        this.disabledBorderColor = disabledBorderColor;
    }

    public void setDisabledTextColor(int disabledTextColor) {
        this.disabledTextColor = disabledTextColor;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getTitle() {
        return this.title;
    }

    protected void setIsDropdownButton(boolean isDropdownButton){
        this.isDropDownButton = isDropdownButton;
    }

    @Override
    public void render(MatrixStack matrixStack) {
        if(this.isHidden())
            return;

        super.render(matrixStack);

        if(this.enabled){
            if(this.isMouseOver()){
                AbstractGui.fill(matrixStack, this.leftPos - this.borderThickness, this.topPos - this.borderThickness,
                        this.leftPos + this.width + this.borderThickness, this.topPos + this.height + this.borderThickness,
                        this.hoverBorderColor);

                AbstractGui.fill(matrixStack, this.leftPos, this.topPos, this.leftPos + this.width, this.topPos + this.height, this.hoverBackgroundColor);

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

            Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.title,
                    this.leftPos + this.width / 2f - font.getStringWidth(this.title) / 2f,
                    this.topPos + this.height / 2f - yOffset,
                    this.disabledTextColor);
        }
    }

    public void onClick(){
        if(this.isHidden())
            return;

        if(this.isMouseOver() && this.enabled)
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
