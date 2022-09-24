package de.j3ramy.edomui.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.j3ramy.edomui.gui.widgets.Widget;
import de.j3ramy.edomui.utils.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;

public final class TextField extends Widget {
    public interface ITextField{
        void onTextChange();
    }

    private final ResourceLocation hintIcon;
    private final String placeholder;
    private final ITextField textChangeAction;

    private int caretXPosition = 0, caretPosition;

    public int disabledBackgroundColor = Color.WHITE;
    public int disabledTextColor = Color.GRAY;
    public int disabledBorderColor = Color.DARK_GRAY;

    public StringBuilder text = new StringBuilder("");
    public boolean isFocused, isEnabled = true;

    public TextField(int x, int y, int width, int height, String placeholderText, ResourceLocation hintIcon, ITextField textChangeAction){
        super(x, y, width, height);

        this.placeholder = placeholderText;
        this.hintIcon = hintIcon;
        this.textChangeAction = textChangeAction;

        this.textColor = Color.DARK_GRAY;
    }

    public TextField(int x, int y, int width, int height, String placeholderText, ResourceLocation hintIcon){
        this(x, y, width, height, placeholderText, hintIcon, null);
    }

    public TextField(int x, int y, int width, int height, String placeholderText, ITextField textChangeAction){
        this(x, y, width, height, placeholderText, null, textChangeAction);
    }

    public TextField(int x, int y, int width, int height, String placeholderText){
        this(x, y, width, height, placeholderText, null, null);
    }

    @Override
    public void render(MatrixStack matrixStack){
        if(this.isHidden)
            return;

        super.render(matrixStack);

        AbstractGui.fill(matrixStack, this.leftPos - this.borderThickness, this.topPos - this.borderThickness,
                this.leftPos + this.width + this.borderThickness, this.topPos + this.height + this.borderThickness,
                this.isEnabled ? this.borderColor : this.disabledBorderColor);

        AbstractGui.fill(matrixStack, this.leftPos, this.topPos, this.leftPos + this.width, this.topPos + this.height,
                this.isEnabled ? this.backgroundColor : this.disabledBackgroundColor);

        Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.text.toString().isEmpty() ? this.placeholder : this.text.toString(),
                this.leftPos + 3, this.topPos + this.height / 2f - this.height / 4f,
                this.text.toString().isEmpty() || !this.isEnabled ? this.disabledTextColor : this.textColor);

        if(this.isFocused){
            AbstractGui.fill(matrixStack, this.leftPos + 3 + this.caretXPosition, this.topPos + 3,
                    this.leftPos + 3 + this.caretXPosition + 1, this.topPos + this.height - 3,
                    this.textColor);
        }
    }

    @Override
    public void update(int x, int y) {
        super.update(x, y);
    }

    @Override
    public void onClick() {
        if(!this.isMouseOver()){
            this.isFocused = false;
        }
        else{
            super.onClick();
            this.isFocused = true;
        }
    }

    public void onKeyPressed(int keyCode){
        if(this.isFocused && !this.isHidden){
            if(keyCode == 259 && this.text.length() >= 1){
                this.removeLetter();
            }

            //when right arrow pressed move caret to the right
            if(keyCode == 262 && this.caretPosition < this.font.getStringWidth(this.text.toString()) && this.caretPosition < this.text.length())
                this.moveCaret(true);

            //when left arrow pressed move caret to the left
            if(keyCode == 263 && this.caretPosition >= 1)
                this.moveCaret(false);
        }
    }

    public void onCharTyped(char c){
        if(this.isFocused && !this.isHidden){
            this.onTextChange(c);
        }
    }

    private void onTextChange(char c) {
        if(this.textChangeAction != null)
            this.textChangeAction.onTextChange();

       this.addLetter(c);
    }

    private void addLetter(char c){
        this.text.insert(this.caretPosition, c);
        this.moveCaret(true);
    }

    private void removeLetter(){
        //PROBLEM: When caret moved to left and delete until beginning -> crash because index is one too high
        this.moveCaret(false);
        this.text.deleteCharAt(this.caretPosition);
    }

    private void moveCaret( boolean moveRight){
        if(moveRight){
            this.caretXPosition += this.font.getStringWidth(Character.toString(this.text.charAt(this.text.length() - 1)));
            this.caretPosition++;
        }
        else{
            //int index = this.text.length() == 1 ? 0 : this.text.length() - 1;
            //this.caretXPosition -= this.font.getStringWidth(Character.toString(this.text.charAt(index)));
            System.out.println(this.text.length() + " | " + this.caretPosition);
            this.caretXPosition -= this.font.getStringWidth(Character.toString(this.text.charAt(this.caretPosition - 1)));
            this.caretPosition--;
        }

        System.out.println(this.caretPosition);
    }

    private boolean isMouseOver(){
        return this.mousePosition.x > this.leftPos && this.mousePosition.x < this.leftPos + this.width &&
                this.mousePosition.y > this.topPos && this.mousePosition.y < this.topPos + this.height;
    }

    private boolean fitsTextInField(){
        int textLengthInPx = this.font.getStringWidth(this.text.toString());

        return textLengthInPx < this.width - this.height - 3;
    }

}

