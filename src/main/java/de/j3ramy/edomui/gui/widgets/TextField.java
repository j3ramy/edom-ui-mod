package de.j3ramy.edomui.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.j3ramy.edomui.utils.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;

public final class TextField extends Widget {
    public interface ITextField{
        void onTextChange();
    }

    private final ResourceLocation hintIcon;
    private final String placeholder;
    private final ITextField textChangeAction;

    private int caretXPosition, caretPosition;
    private String visibleText = "";

    public int disabledBackgroundColor = Color.WHITE, disabledTextColor = Color.GRAY, disabledBorderColor = Color.DARK_GRAY;
    public int allSelectedTextColor = Color.YELLOW, allSelectedBackgroundColor = Color.DARK_GRAY;

    public StringBuilder text = new StringBuilder();
    public boolean isFocused, isEnabled = true, isSelectedAll = false;

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

        //background
        AbstractGui.fill(matrixStack, this.leftPos - this.borderThickness, this.topPos - this.borderThickness,
                this.leftPos + this.width + this.borderThickness, this.topPos + this.height + this.borderThickness,
                this.isEnabled ? this.borderColor : this.disabledBorderColor);

        AbstractGui.fill(matrixStack, this.leftPos, this.topPos, this.leftPos + this.width, this.topPos + this.height,
                this.isEnabled ? this.backgroundColor : this.disabledBackgroundColor);

        //text
        if(this.isSelectedAll){
            AbstractGui.fill(matrixStack, this.leftPos + 1, this.topPos + 1, this.leftPos + this.width - 1, this.topPos + this.height - 1,
                    this.allSelectedBackgroundColor);

            Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.text.toString().isEmpty() ? this.placeholder : this.visibleText,
                    this.leftPos + 3, this.topPos + this.height / 2f - this.height / 4f, this.allSelectedTextColor);
        }
        else{
            Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.text.toString().isEmpty() ? this.placeholder : this.visibleText,
                    this.leftPos + 3, this.topPos + this.height / 2f - this.height / 4f,
                    this.text.toString().isEmpty() || !this.isEnabled ? this.disabledTextColor : this.textColor);
        }

        //caret
        if(this.isFocused){
            AbstractGui.fill(matrixStack, this.leftPos + 3 + this.caretXPosition, this.topPos + 3,
                    this.leftPos + 3 + this.caretXPosition + 1, this.topPos + this.height - 3,
                    this.textColor);
        }

        //hint icon
        if(this.hintIcon != null){
            int imageWidth = this.height;
            Minecraft.getInstance().getTextureManager().bindTexture(this.hintIcon);
            AbstractGui.blit(matrixStack, this.leftPos + this.width - imageWidth, this.topPos, 0, 0, imageWidth, this.height, imageWidth, this.height);
        }
    }

    @Override
    public void update(int x, int y) {
        if(!this.isEnabled)
            return;

        super.update(x, y);

        if(this.doesTextFit()){
            this.visibleText = this.text.toString();
        }
        else{
            this.visibleText = this.text.substring(this.caretPosition);
        }
    }

    @Override
    public void onClick() {
        if(!this.isEnabled)
            return;

        if(!this.isMouseOver()){
            this.isFocused = false;
        }
        else{
            super.onClick();
            this.isFocused = true;
        }
    }

    public void onKeyPressed(int keyCode){
        if(this.isEnabled && this.isFocused && !this.isHidden){
            //select complete text
            if(Screen.isSelectAll(keyCode) && this.text.length() > 0){
                this.isSelectedAll = true;
            }

            //if text all selected and backspace (259) or delete (261) pressed
            if(this.isSelectedAll && keyCode == 259 || keyCode == 261)
                this.clear();

            //remove letter when backspace
            if(keyCode == 259 && this.text.length() >= 1 && this.caretPosition > 0){
                this.removeLetter();
                this.onTextChange();
            }

            //when right arrow pressed move caret to the right
            if(keyCode == 262 && this.caretPosition < this.text.length())
                this.moveCaret(true);

            //when left arrow pressed move caret to the left
            if(keyCode == 263 && this.caretPosition >= 1)
                this.moveCaret(false);
        }
    }

    public void onCharTyped(char c){
        if(this.isEnabled && this.isFocused && !this.isHidden){
            if(this.isSelectedAll)
                this.clear();

            if(this.doesTextFit()){
                this.addLetter(c);
                this.onTextChange();
            }
        }
    }

    private void onTextChange() {
        if(this.textChangeAction != null)
            this.textChangeAction.onTextChange();
    }

    private void addLetter(char c){
        this.text.insert(this.caretPosition, c);
        this.moveCaret(true);
    }

    private void removeLetter(){
        this.moveCaret(false);
        this.text.deleteCharAt(this.caretPosition);
    }

    private void moveCaret(boolean moveRight){
        if(moveRight){
            this.caretXPosition += this.font.getStringWidth(Character.toString(this.text.charAt(this.text.length() - 1)));
            this.caretPosition++;
        }
        else{
            this.caretXPosition -= this.font.getStringWidth(Character.toString(this.text.charAt(this.caretPosition - 1)));
            this.caretPosition--;
        }

        this.isSelectedAll = false;
    }

    private void clear(){
        this.text = new StringBuilder();
        this.visibleText = "";
        this.caretPosition = 0;
        this.caretXPosition = 0;
        this.isSelectedAll = false;
    }

    private boolean isMouseOver(){
        return this.mousePosition.x > this.leftPos && this.mousePosition.x < this.leftPos + this.width &&
                this.mousePosition.y > this.topPos && this.mousePosition.y < this.topPos + this.height;
    }

    private boolean doesTextFit(){
        int textLengthInPx = this.font.getStringWidth(this.text.toString());

        return textLengthInPx < this.width - (this.hintIcon != null ? this.height : 0) - 10;
    }

    private boolean isCaretAtEnd(){
        return this.caretPosition == this.text.length();
    }

    private boolean isCaretAtStart(){
        return this.caretPosition == 0;
    }
}

