package de.j3ramy.edomui.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.j3ramy.edomui.interfaces.ITextFieldOnPressEnter;
import de.j3ramy.edomui.interfaces.ITextFieldOnTextChange;
import de.j3ramy.edomui.utils.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public final class TextField extends Widget implements ITextFieldOnTextChange, ITextFieldOnPressEnter {
    private final ITextFieldOnTextChange onTextChangeAction;
    private final ITextFieldOnPressEnter onPressEnterAction;
    private final ResourceLocation hintIcon;
    private final String placeholder;
    private final int hintIconMargin = 1;

    private int caretXPosition, caretPosition;
    private String visibleText = "";

    public int disabledBackgroundColor = Color.WHITE, disabledTextColor = Color.GRAY, disabledBorderColor = Color.DARK_GRAY;
    public int allSelectedTextColor = Color.YELLOW, allSelectedBackgroundColor = Color.DARK_GRAY, maxLength = 20;

    public StringBuilder text = new StringBuilder();
    public boolean isFocused, isEnabled = true, isSelectedAll = false;

    public TextField(int x, int y, int width, int height, String placeholderText, ResourceLocation hintIcon,
                     @Nullable ITextFieldOnTextChange onTextChangeAction, @Nullable ITextFieldOnPressEnter onPressEnterAction){
        super(x, y, width, height);

        this.placeholder = placeholderText;
        this.hintIcon = hintIcon;
        this.onTextChangeAction = onTextChangeAction;
        this.onPressEnterAction = onPressEnterAction;

        this.textColor = Color.DARK_GRAY;
    }

    public TextField(int x, int y, int width, int height, String placeholderText, @Nullable ITextFieldOnTextChange onTextChangeAction,
                     @Nullable ITextFieldOnPressEnter onPressEnterAction){
        this(x, y, width, height, placeholderText, null, onTextChangeAction, onPressEnterAction);
    }

    public TextField(int x, int y, int width, int height, String placeholderText, @Nullable ResourceLocation hintIcon){
        this(x, y, width, height, placeholderText, hintIcon, null, null);
    }

    public TextField(int x, int y, int width, int height, String placeholderText){
        this(x, y, width, height, placeholderText, null, null, null);
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

            AbstractGui.blit(matrixStack, this.leftPos + this.width - imageWidth - this.hintIconMargin, this.topPos + this.hintIconMargin,
                    0, 0, imageWidth - this.hintIconMargin * 2, this.height - hintIconMargin * 2,
                    imageWidth - this.hintIconMargin * 2, this.height - this.hintIconMargin * 2);


        }
    }

    private int maxCaretPosition;
    @Override
    public void update(int x, int y) {
        if(!this.isEnabled)
            return;

        super.update(x, y);

        if(this.doesTextFit(this.text.toString())){
            this.visibleText = this.text.toString();
            this.maxCaretPosition = this.caretPosition;
        }
        else if(this.isCaretAtEnd()){
            this.visibleText = this.text.substring(this.text.length() - this.maxCaretPosition);
        }

        this.caretXPosition = this.font.getStringWidth(this.visibleText);
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

    @Override
    public void onTextChange() {
        if(this.onTextChangeAction != null)
            this.onTextChangeAction.onTextChange();
    }

    @Override
    public void onPressEnter() {
        if(this.onPressEnterAction != null)
            this.onPressEnterAction.onPressEnter();
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

            //call onPressEnter-event when enter pressed
            if(keyCode == 257)
                this.onPressEnter();

            //remove letter when backspace
            if(keyCode == 259 && this.text.length() >= 1 && this.caretPosition > 0){
                this.removeLetter();
                this.onTextChange();
            }

            /*
            //when right arrow pressed move caret to the right
            if(keyCode == 262 && this.caretPosition < this.text.length()){
                this.caretPosition++;
            }

            //when left arrow pressed move caret to the left
            if(keyCode == 263 && this.caretPosition >= 1){
                this.caretPosition--;
            }

             */
        }
    }

    public void onCharTyped(char c){
        if(this.isEnabled && this.isFocused && !this.isHidden && this.text.length() < this.maxLength){
            if(this.isSelectedAll)
                this.clear();

            this.addLetter(c);
            this.onTextChange();
        }
    }

    private void addLetter(char c){
        if(this.isCaretAtEnd())
            this.text.append(c);
        else
            this.text.insert(this.caretPosition + 1, c);

        this.caretPosition++;
    }

    private void removeLetter(){
        this.text.deleteCharAt(this.caretPosition - 1);
        this.caretPosition--;
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

    private boolean doesTextFit(String text){
        int textLengthInPx = this.font.getStringWidth(text);

        return textLengthInPx < this.width - (this.hintIcon != null ? this.height - 3 - this.hintIconMargin : 3) - 4;
    }

    private boolean isCaretAtEnd(){
        return this.caretPosition == this.text.length();
    }
}

