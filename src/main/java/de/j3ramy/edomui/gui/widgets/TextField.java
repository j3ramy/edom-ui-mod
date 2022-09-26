package de.j3ramy.edomui.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.j3ramy.edomui.Main;
import de.j3ramy.edomui.enums.TextFieldType;
import de.j3ramy.edomui.interfaces.ITextFieldOnPressEnter;
import de.j3ramy.edomui.interfaces.ITextFieldOnTextChange;
import de.j3ramy.edomui.utils.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.*;

public final class TextField extends Widget implements ITextFieldOnTextChange, ITextFieldOnPressEnter {
    private final static ResourceLocation showPasswordIcon = new ResourceLocation(Main.MOD_ID,"textures/gui/icons/show.png");
    private final static ResourceLocation hidePasswordIcon = new ResourceLocation(Main.MOD_ID,"textures/gui/icons/hide.png");

    private final ITextFieldOnTextChange onTextChangeAction;
    private final ITextFieldOnPressEnter onPressEnterAction;
    private final TextFieldType type;
    private final String placeholder;
    private final int fieldIconMargin = 1;

    private StringBuilder text = new StringBuilder();
    private ResourceLocation fieldIcon;
    private int caretXPosition, caretPosition;
    private String visibleText = "";

    public int disabledBackgroundColor = Color.GRAY, disabledTextColor = Color.GRAY, disabledBorderColor = Color.BLACK;
    public int allSelectedTextColor = Color.YELLOW, allSelectedBackgroundColor = Color.DARK_GRAY, maxLength = 20;
    public boolean isFocused, isEnabled = true, isSelectedAll = false, isPasswordVisible;

    public String getText(){
        return this.text.toString();
    }

    public void setText(String text){
        this.text = new StringBuilder(text);
        this.onTextChange();
    }

    public TextField(int x, int y, int width, int height, String placeholderText, ResourceLocation fieldIcon,
                     @Nullable ITextFieldOnTextChange onTextChangeAction, @Nullable ITextFieldOnPressEnter onPressEnterAction, TextFieldType type){
        super(x, y, width, height);

        this.type = type;
        this.placeholder = placeholderText;
        this.onTextChangeAction = onTextChangeAction;
        this.onPressEnterAction = onPressEnterAction;

        this.textColor = Color.DARK_GRAY;

        if(type == TextFieldType.PASSWORD){
            this.isPasswordVisible = false;
            this.fieldIcon = showPasswordIcon;
        }
        else{
            this.fieldIcon = fieldIcon;
        }
    }

    public TextField(int x, int y, int width, int height, String placeholderText, @Nullable ITextFieldOnTextChange onTextChangeAction,
                     @Nullable ITextFieldOnPressEnter onPressEnterAction, TextFieldType type){
        this(x, y, width, height, placeholderText, null, onTextChangeAction, onPressEnterAction, type);
    }

    public TextField(int x, int y, int width, int height, String placeholderText, @Nullable ResourceLocation fieldIcon, TextFieldType type){
        this(x, y, width, height, placeholderText, fieldIcon, null, null, type);
    }

    public TextField(int x, int y, int width, int height, String placeholderText, TextFieldType type){
        this(x, y, width, height, placeholderText, null, null, null, type);
    }

    public TextField(int x, int y, int width, int height, String placeholderText){
        this(x, y, width, height, placeholderText, null, null, null, TextFieldType.TEXT);
    }

    private Rectangle iconArea;
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

        //icon
        if(this.fieldIcon != null){
            this.iconArea = new Rectangle(this.leftPos + this.width - this.height - this.fieldIconMargin,
                    this.topPos + this.fieldIconMargin, this.height - this.fieldIconMargin * 2, this.height - fieldIconMargin * 2);

            Minecraft.getInstance().getTextureManager().bindTexture(this.fieldIcon);

            AbstractGui.blit(matrixStack, this.iconArea.x, this.iconArea.y, 0, 0, this.iconArea.width, this.iconArea.height,
                    this.iconArea.width, this.iconArea.height);
        }
    }

    private int maxCaretPosition;
    @Override
    public void update(int x, int y) {
        if(!this.isEnabled)
            return;

        super.update(x, y);

        this.caretXPosition = this.font.getStringWidth(this.visibleText);

        this.updateVisibleText();
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

        if(this.type == TextFieldType.PASSWORD && this.iconArea.contains(new Point(this.mousePosition.x, this.mousePosition.y))){
            this.isPasswordVisible = !this.isPasswordVisible;
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

    private void updateVisibleText(){
        if(this.doesTextFit(this.text.toString())){
            this.visibleText = this.text.toString();
            this.maxCaretPosition = this.caretPosition;
        }
        else if(this.isCaretAtEnd()){
            this.visibleText = this.text.substring(this.text.length() - this.maxCaretPosition);
        }

        if(this.type == TextFieldType.PASSWORD){
            this.updatePasswordText();
        }
    }

    private void updatePasswordText(){
        this.fieldIcon = this.isPasswordVisible ? hidePasswordIcon : showPasswordIcon;

        if(this.isPasswordVisible)
            this.visibleText = this.text.substring(this.text.length() - this.maxCaretPosition);
        else{
            StringBuilder starText = new StringBuilder();
            for(int i = 0; i < this.visibleText.length(); i++){
                starText.append("*");
            }

            this.visibleText = starText.toString();
        }
    }

    private void addLetter(char c){
        if(this.type == TextFieldType.NUMBER && !Character.isDigit(c))
            return;

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

        return textLengthInPx < this.width - (this.fieldIcon != null ? this.height - 3 - this.fieldIconMargin : 3) - (this.type == TextFieldType.PASSWORD ? 6 : 4);
    }

    private boolean isCaretAtEnd(){
        return this.caretPosition == this.text.length();
    }
}

