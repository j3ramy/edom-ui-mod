
package de.j3ramy.economy.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.j3ramy.economy.utils.Color;
import de.j3ramy.economy.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.antlr.v4.runtime.misc.NotNull;

import java.awt.*;

public class DropDown extends Widget {
    public static final int DEFAULT_COLOR = Color.DARK_GRAY_HEX;
    public static final int HOVER_COLOR = Color.LIGHT_GRAY_HEX;
    public static final int TEXT_COLOR = Color.WHITE;
    public static final int BORDER_COLOR_DEFAULT = Color.BLACK_HEX;
    public static final int BORDER_COLOR_HOVER = DEFAULT_COLOR;
    public static final int BORDER_THICKNESS = 1;
    public static final int TEXT_Y_OFFSET = 4;
    public static final int MAX_VISIBLE_LIST_ELEMENTS = 3;


    private final Point mousePosition;
    private final String[] options;
    private final DropDownOption[] optionFields;
    private final String placeholder;
    private final int maxWordLength;
    private int selectedIndex = -1;
    private boolean isDisabled;
    private boolean isUnfolded;


    public DropDown(@NotNull String[] options, int x, int y, int width, int height, String placeholder){
        super(x, y, width, height, new StringTextComponent(""));

        this.mousePosition = new Point();
        this.options = options;
        this.optionFields = new DropDownOption[this.options.length];

        this.maxWordLength = (width - 26) / 6; //6 = width of letter; 26 = left-margin from arrow
        this.placeholder = placeholder;
        this.setMessage(GuiUtils.getFormattedLabel(this.maxWordLength, this.placeholder));
    }

    public boolean isOptionSelected(){
        return this.selectedIndex != -1;
    }

    public boolean isDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
        return this.isDisabled;
    }

    /*
    public String[] getOptions(){
        return this.options;
    }

        public String getOption(int index){
        return this.options[index];
    }

    public int getSelectedIndex(){
        return this.selectedIndex;
    }

     */

    public String getPlaceholder() {
        return this.placeholder;
    }

    public String getSelectedText(){
        if(this.selectedIndex == -1)
            return this.placeholder;

        return this.options[this.selectedIndex];
    }

    private boolean needsExpandedContextMenu(){
        return this.options.length > MAX_VISIBLE_LIST_ELEMENTS;
    }

    private void initContextMenu(int startIndex){
        if(this.needsExpandedContextMenu()){
            for(int i = 0; i < MAX_VISIBLE_LIST_ELEMENTS; i++){
                optionFields[i] = new DropDownOption(this.x, this.y, this.width, this.height, GuiUtils.getFormattedLabel(this.maxWordLength, this.options[i + startIndex]), i);
            }
        }
        else{
            for(int i = 0; i < this.options.length; i++){
                optionFields[i] = new DropDownOption(this.x, this.y, this.width, this.height, GuiUtils.getFormattedLabel(this.maxWordLength, this.options[i + startIndex]), i);
            }
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if(this.mousePosition == null)
            return;

        //render background and border
        if(isMouseOver(mousePosition.x, mousePosition.y) && !this.isDisabled)
            this.onHover(matrixStack);
        else{
            AbstractGui.fill(matrixStack, this.x - BORDER_THICKNESS, this.y - BORDER_THICKNESS,
                    this.x + this.width + BORDER_THICKNESS, this.y + this.height + BORDER_THICKNESS,
                    BORDER_COLOR_DEFAULT);

            AbstractGui.fill(matrixStack, this.x, this.y, this.x + this.width, this.y + this.height, DEFAULT_COLOR);
        }

        //render text
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.getMessage().getString(),
                this.x + 5,
                this.y + this.height / 2f - TEXT_Y_OFFSET,
                (!this.isDisabled && this.selectedIndex != -1 || isMouseOver(mousePosition.x, mousePosition.y) && this.selectedIndex == -1 && !this.isDisabled) ? TEXT_COLOR : HOVER_COLOR);

        if(this.isUnfolded){
            drawArrowInverted(matrixStack, this.x + this.width - 15, this.y + (this.height / 2) - 2);
            this.drawContextMenu(matrixStack);
        }
        else
            drawArrow(matrixStack, this.x + this.width - 15, this.y + (this.height / 2) - 2);
    }

    private void drawArrow(MatrixStack matrixStack, int x, int y){
        AbstractGui.fill(matrixStack, x, y, x + 8, y + 1, this.isDisabled ? Color.LIGHT_GRAY_HEX : Color.WHITE_HEX);
        AbstractGui.fill(matrixStack, x + 1, y + 1, x + 7, y + 2, this.isDisabled ? Color.LIGHT_GRAY_HEX : Color.WHITE_HEX);
        AbstractGui.fill(matrixStack, x + 2, y + 2, x + 6, y + 3, this.isDisabled ? Color.LIGHT_GRAY_HEX : Color.WHITE_HEX);
        AbstractGui.fill(matrixStack, x + 3, y + 3, x + 5, y + 4, this.isDisabled ? Color.LIGHT_GRAY_HEX : Color.WHITE_HEX);
    }

    private void drawArrowInverted(MatrixStack matrixStack, int x, int y){
        AbstractGui.fill(matrixStack, x + 3, y, x + 5, y + 1, this.isDisabled ? Color.LIGHT_GRAY_HEX : Color.WHITE_HEX);
        AbstractGui.fill(matrixStack, x + 2, y + 1, x + 6, y + 2, this.isDisabled ? Color.LIGHT_GRAY_HEX : Color.WHITE_HEX);
        AbstractGui.fill(matrixStack, x + 1, y + 2, x + 7, y + 3, this.isDisabled ? Color.LIGHT_GRAY_HEX : Color.WHITE_HEX);
        AbstractGui.fill(matrixStack, x, y + 3, x + 8, y + 4, this.isDisabled ? Color.LIGHT_GRAY_HEX : Color.WHITE_HEX);
    }

    private  void drawContextMenu(MatrixStack matrixStack){
        for (DropDownOption optionField : this.optionFields) {
            if (optionField != null)
                optionField.draw(matrixStack);
        }

        if(this.needsExpandedContextMenu()){
            //this.drawScrollIndicator(matrixStack);
        }
    }

    private void drawScrollIndicator(MatrixStack matrixStack){
        int marginTop = 1;
        int marginLeft = 1;

        int trackWidth = 4;

        //track
        int trackHeight = this.y + this.height * (MAX_VISIBLE_LIST_ELEMENTS + 1) + (BORDER_THICKNESS * 2 * MAX_VISIBLE_LIST_ELEMENTS + 1) + marginTop;

        AbstractGui.fill(matrixStack,
                this.x + this.width + BORDER_THICKNESS + marginLeft,
                this.y + this.height + BORDER_THICKNESS + marginTop,
                this.x + this.width + trackWidth + BORDER_THICKNESS + marginLeft,
                trackHeight,
                DEFAULT_COLOR);

        //thumb
        int invisibleOptionCount = this.optionFields.length - MAX_VISIBLE_LIST_ELEMENTS;
        int thumbHeight = this.y + this.height + (trackHeight + this.height * invisibleOptionCount) / trackHeight; // LAST EDIT

        AbstractGui.fill(matrixStack,
                this.x + this.width + BORDER_THICKNESS + marginLeft,
                this.y + this.height + BORDER_THICKNESS + marginTop,
                this.x + this.width + trackWidth + BORDER_THICKNESS + marginLeft,
                thumbHeight,
                Color.WHITE_HEX);

    }

    public void update(){
        for (DropDownOption optionField : this.optionFields) {
            if(optionField != null)
                optionField.updateMousePosition(this.mousePosition.x, this.mousePosition.y);
        }
    }

    public void updateMousePosition(int mouseX, int mouseY){
        this.mousePosition.x = mouseX;
        this.mousePosition.y = mouseY;
    }

    public void onClick(){
        for(DropDownOption dropDownOption : this.optionFields){
            if(dropDownOption != null && this.isUnfolded && dropDownOption.isMouseOver(this.mousePosition.x, this.mousePosition.y)){
                this.selectedIndex = dropDownOption.getIndex();
                this.setMessage(GuiUtils.getFormattedLabel(this.maxWordLength, dropDownOption.getString()));
                this.isUnfolded = false;
            }
        }

        if(this.isMouseOver(this.mousePosition.x, this.mousePosition.y) && !this.isDisabled){
            this.isUnfolded = !this.isUnfolded;

            if(this.isUnfolded)
                this.initContextMenu(0);
        }
    }

    private boolean isMouseOverContextMenu(int x, int y){
        Rectangle menu = new Rectangle(this.x, this.y, this.x + this.width, this.y + this.height + (this.height * MAX_VISIBLE_LIST_ELEMENTS));
        return menu.contains(new Point(x, y));
    }

    private float currentScrollIndex = 0;
    public void onScroll(int scrollDelta){
        if(!this.isMouseOverContextMenu(this.mousePosition.x, this.mousePosition.y))
            return;

        if(!this.needsExpandedContextMenu())
            return;

        if(this.isUnfolded){
            int maxScrollIndex = this.options.length - MAX_VISIBLE_LIST_ELEMENTS;

            if(this.currentScrollIndex < maxScrollIndex)
                if(scrollDelta == -1)
                    this.currentScrollIndex += 0.5f;

            if(this.currentScrollIndex > 0)
                if(scrollDelta == 1)
                    this.currentScrollIndex -= 0.5f;

            if(this.currentScrollIndex % 1 == 0)
                this.initContextMenu((int) this.currentScrollIndex);
        }
    }

    private void onHover(MatrixStack matrixStack){
        AbstractGui.fill(matrixStack, this.x - BORDER_THICKNESS, this.y - BORDER_THICKNESS,
                this.x + this.width + BORDER_THICKNESS, this.y + this.height + BORDER_THICKNESS,
                BORDER_COLOR_HOVER);

        AbstractGui.fill(matrixStack, this.x, this.y, this.x + this.width, this.y + this.height, HOVER_COLOR);
    }

    //----------------------------------------------------------------------------------------------------------------------------------------------
    private static class DropDownOption extends Button {
        private final Point mousePosition;
        private final int index;

        public int getIndex() {
            return this.index;
        }

        public String getString(){
            return this.getMessage().getString();
        }

        public DropDownOption(int x, int y, int width, int height, ITextComponent text, int index){
            super(x, y, width, height, text, (onclick) ->{});

            this.mousePosition = new Point();
            this.index = index;

            int xPos = this.x - BORDER_THICKNESS;
            int w = this.width + 2 * BORDER_THICKNESS;
            int h = this.height + 2 * BORDER_THICKNESS;
            int yPos = this.y + h + h * this.index;

            this.x = xPos;
            this.width = w;
            this.y = yPos;
            this.height = h;
        }

        public void draw(MatrixStack matrixStack){
            AbstractGui.fill(matrixStack, this.x, this.y, this.x + this.width, this.y + this.height, this.isMouseOver(this.mousePosition.x, this.mousePosition.y) ? HOVER_COLOR : DEFAULT_COLOR);

            Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.getMessage().getString(), this.x + 6,
                    this.y + this.height / 2f - TEXT_Y_OFFSET, Color.WHITE);
        }

        public void updateMousePosition(int mouseX, int mouseY){
            this.mousePosition.x = mouseX;
            this.mousePosition.y = mouseY;
        }

    }


}
