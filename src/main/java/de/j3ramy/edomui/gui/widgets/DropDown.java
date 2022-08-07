
package de.j3ramy.edomui.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.j3ramy.edomui.utils.Color;
import de.j3ramy.edomui.utils.GuiUtils;
import net.minecraft.client.gui.AbstractGui;
import org.antlr.v4.runtime.misc.NotNull;

import java.awt.*;

public class DropDown extends Button {
    private static final int MAX_VISIBLE_LIST_ELEMENTS = 3;

    private final String[] options;
    private final DropDownOption[] optionFields;
    private final String placeholder;
    private final int maxWordLength;

    private int selectedIndex = -1, arrowColor = Color.WHITE_HEX;
    private boolean isUnfolded;


    public DropDown(@NotNull String[] options, int x, int y, int width, int height, String placeholder){
        super(x, y, width, height, placeholder, ()->{});

        this.options = options;
        this.optionFields = new DropDownOption[this.options.length];

        this.maxWordLength = (width - 26) / 6; //6 = width of letter; 26 = left-margin from arrow
        this.placeholder = placeholder;
        this.title = GuiUtils.getFormattedLabel(this.maxWordLength, this.placeholder);

        this.setIsDropdownButton(true);
    }

    public void setArrowColor(int arrowColor) {
        this.arrowColor = arrowColor;
    }

    public String[] getOptions(){
        return this.options;
    }

        public String getOption(int index){
        return this.options[index];
    }

    public int getSelectedIndex(){
        return this.selectedIndex;
    }

    public boolean isOptionSelected(){
        return this.selectedIndex != -1;
    }

    public boolean isUnfolded() {
        return this.isUnfolded;
    }

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
                optionFields[i] = new DropDownOption(this.leftPos, this.topPos, this.width, this.height, GuiUtils.getFormattedLabel(this.maxWordLength, this.options[i + startIndex]), i);
            }
        }
        else{
            for(int i = 0; i < this.options.length; i++){
                optionFields[i] = new DropDownOption(this.leftPos, this.topPos, this.width, this.height, GuiUtils.getFormattedLabel(this.maxWordLength, this.options[i + startIndex]), i);
            }
        }
    }

    @Override
    public void render(MatrixStack matrixStack) {
        if(this.isHidden())
            return;

        super.render(matrixStack);

        if(this.isUnfolded){
            renderArrowInverted(matrixStack, this.leftPos + this.width - 15, topPos + (this.height / 2) - 2);
            this.renderContextMenu(matrixStack);
        }
        else
            renderArrow(matrixStack, this.leftPos + this.width - 15, topPos + (this.height / 2) - 2);


    }

    @Override
    public void update(int x, int y) {
        super.update(x, y);

        for(DropDownOption option : this.optionFields)
            if(option != null) option.update(x, y);
    }

    private void renderArrow(MatrixStack matrixStack, int x, int y){
        AbstractGui.fill(matrixStack, x, y, x + 8, y + 1, !this.enabled ? Color.LIGHT_GRAY_HEX :  this.arrowColor);
        AbstractGui.fill(matrixStack, x + 1, y + 1, x + 7, y + 2, !this.enabled ? Color.LIGHT_GRAY_HEX : this.arrowColor);
        AbstractGui.fill(matrixStack, x + 2, y + 2, x + 6, y + 3, !this.enabled ? Color.LIGHT_GRAY_HEX : this.arrowColor);
        AbstractGui.fill(matrixStack, x + 3, y + 3, x + 5, y + 4, !this.enabled ? Color.LIGHT_GRAY_HEX : this.arrowColor);
    }

    private void renderArrowInverted(MatrixStack matrixStack, int x, int y){
        AbstractGui.fill(matrixStack, x + 3, y, x + 5, y + 1, !this.enabled ? Color.LIGHT_GRAY_HEX :  this.arrowColor);
        AbstractGui.fill(matrixStack, x + 2, y + 1, x + 6, y + 2, !this.enabled ? Color.LIGHT_GRAY_HEX : this.arrowColor);
        AbstractGui.fill(matrixStack, x + 1, y + 2, x + 7, y + 3, !this.enabled ? Color.LIGHT_GRAY_HEX : this.arrowColor);
        AbstractGui.fill(matrixStack, x, y + 3, x + 8, y + 4, !this.enabled ? Color.LIGHT_GRAY_HEX : this.arrowColor);
    }

    private  void renderContextMenu(MatrixStack matrixStack){
        for (DropDownOption optionField : this.optionFields) {
            if (optionField != null)
                optionField.render(matrixStack);
        }

        /*
        if(this.needsExpandedContextMenu()){
            //this.renderScrollIndicator(matrixStack);
        }

         */
    }

    /*
    private void renderScrollIndicator(MatrixStack matrixStack){
        int marginTop = 1;
        int marginLeft = 1;

        int trackWidth = 4;

        //track
        int trackHeight = topPos + this.height * (MAX_VISIBLE_LIST_ELEMENTS + 1) + (this.borderThickness * 2 * MAX_VISIBLE_LIST_ELEMENTS + 1) + marginTop;

        AbstractGui.fill(matrixStack,
                this.leftPos + this.width + this.borderThickness + marginLeft,
                topPos + this.height + this.borderThickness + marginTop,
                this.leftPos + this.width + trackWidth + this.borderThickness + marginLeft,
                trackHeight,
                DEFAULT_COLOR);

        //thumb
        int invisibleOptionCount = this.optionFields.length - MAX_VISIBLE_LIST_ELEMENTS;
        int thumbHeight = topPos + this.height + (trackHeight + this.height * invisibleOptionCount) / trackHeight; // LAST EDIT

        AbstractGui.fill(matrixStack,
                this.leftPos + this.width + this.borderThickness + marginLeft,
                topPos + this.height + this.borderThickness + marginTop,
                this.leftPos + this.width + trackWidth + this.borderThickness + marginLeft,
                thumbHeight,
                Color.WHITE_HEX);

    }

     */

    public void onClick(){
        if(this.isHidden())
            return;

        for(DropDownOption dropDownOption : this.optionFields){
            if(dropDownOption != null && this.isUnfolded && dropDownOption.isMouseOver()){
                this.selectedIndex = dropDownOption.index;
                this.title = GuiUtils.getFormattedLabel(this.maxWordLength, dropDownOption.title);
                this.isUnfolded = false;
            }
        }

        if(this.isMouseOver() && this.enabled){
            this.isUnfolded = !this.isUnfolded;

            if(this.isUnfolded)
                this.initContextMenu(0);
        }
    }

    private boolean isMouseOverContextMenu(){
        Rectangle menu = new Rectangle(this.leftPos, topPos, this.leftPos + this.width, topPos + this.height + (this.height * MAX_VISIBLE_LIST_ELEMENTS));
        return menu.contains(new Point(this.mousePosition.x, this.mousePosition.y));
    }

    private float currentScrollIndex = 0;
    public void onScroll(int scrollDelta){
        if(!this.isMouseOverContextMenu())
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

    //----------------------------------------------------------------------------------------------------------------------------------------------
    private static class DropDownOption extends Button {
        private final int index;

        private DropDownOption(int x, int y, int width, int height, String text, int index){
            super(x, y, width, height, text, () ->{});

            this.index = index;

            int xPos = this.leftPos - this.borderThickness;
            int w = this.width + 2 * this.borderThickness;
            int h = this.height + 2 * this.borderThickness;
            int yPos = topPos + h + h * this.index;

            this.leftPos = xPos;
            this.width = w;
            this.topPos = yPos;
            this.height = h;

            this.setBorderThickness(0);
            this.setIsDropdownButton(true);
            this.setHoverBackgroundColor(Color.LIGHT_GRAY_HEX);
        }

        @Override
        public void render(MatrixStack matrixStack){
            super.render(matrixStack);
        }
    }


}
