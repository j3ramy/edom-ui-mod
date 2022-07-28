
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

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ScrollableList extends Widget {
    public static final int TEXT_COLOR = Color.WHITE;
    public static final int TEXT_Y_OFFSET = 4;

    private final Point mousePosition;
    private final List<ListOption> contents = new ArrayList<>();
    private final List<ListOption> contentFields = new ArrayList<>();
    private final int maxVisibleListElements;
    private final int maxWordLength;
    private final int elementHeight;
    private int selectedIndex = -1;
    private boolean isHidden;


    public ScrollableList(int x, int y, int width, int height, int elementHeight){
        super(x, y, width, height, new StringTextComponent(""));

        this.mousePosition = new Point();
        this.maxVisibleListElements = this.height / elementHeight;
        this.maxWordLength = (width - 20) / 6; //6 = width of letter;
        this.elementHeight = elementHeight;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    public ListOption getEntry(int index){
        return this.contents.get(index);
    }

    public void addToList(String content, boolean isClickable, int backgroundColor, Button.IPressable onClick){
        this.contents.add(new ListOption(this.x, this.y, this.width, this.elementHeight, GuiUtils.getFormattedLabel(this.maxWordLength, content), isClickable, backgroundColor, onClick));

        this.initList(0);
    }

    private boolean needsScrolling(){
        return this.contents.size() > this.maxVisibleListElements;
    }

    private void initList(int startIndex){
        this.contentFields.clear();

        if(this.needsScrolling()){
            for(int i = 0; i < this.maxVisibleListElements; i++){
                this.contentFields.add(this.contents.get(i + startIndex));
                this.contentFields.get(i).setIndex(i);
            }
        }
        else{
            for(int i = 0; i < this.contents.size(); i++){
                this.contentFields.add(this.contents.get(i + startIndex));
                this.contentFields.get(i).setIndex(i);
            }
        }
    }

    public void clear(){
        this.contents.clear();
        this.contentFields.clear();
        this.clearSelectedIndex();
    }

    public void hide(){
        this.isHidden = true;
    }

    public void show(){
        this.isHidden = false;
    }

    public void clearSelectedIndex(){
        this.selectedIndex = -1;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if(this.mousePosition == null || this.isHidden)
            return;

        AbstractGui.fill(matrixStack, this.x, this.y, this.x + this.width, this.y + this.height, Color.DARK_GRAY_HEX);
        this.drawContent(matrixStack);
    }

    private void drawContent(MatrixStack matrixStack){
        for (int i = 0; i < this.contentFields.size(); i++) {
            if (this.contentFields.get(i) != null){
                this.contentFields.get(i) .draw(matrixStack);
                this.contentFields.get(i).setBackgroundColor(i == this.selectedIndex ? Color.LIGHT_GRAY_HEX : Color.DARK_GRAY_HEX);
            }
        }
    }

    public void update(){
        for (ListOption contentField : this.contentFields) {
            if(contentField != null)
                contentField.updateMousePosition(this.mousePosition.x, this.mousePosition.y);
        }
    }

    public void updateMousePosition(int mouseX, int mouseY){
        this.mousePosition.x = mouseX;
        this.mousePosition.y = mouseY;
    }

    public void onClick(){
        if(this.isHidden)
            return;

        for(int i = 0; i < this.contentFields.size(); i++){
            if(this.contentFields.get(i) != null && this.contentFields.get(i).isMouseOver(this.mousePosition.x, this.mousePosition.y)){
                this.selectedIndex = i;
                this.contentFields.get(i).onClick();
            }
        }
    }

    private boolean isMouseOverList(){
        Rectangle list = new Rectangle(this.x, this.y, this.x + this.width, this.y + this.height);
        return list.contains(new Point(this.mousePosition.x, this.mousePosition.y));
    }

    private float currentScrollIndex = 0;
    public void onScroll(int scrollDelta){
        if(!this.isMouseOverList() || this.isHidden)
            return;

        if(!this.needsScrolling())
            return;

        int maxScrollIndex = this.contents.size() - this.maxVisibleListElements;

        if(this.currentScrollIndex < maxScrollIndex)
            if(scrollDelta == -1)
                this.currentScrollIndex += 0.5f;

        if(this.currentScrollIndex > 0)
            if(scrollDelta == 1)
                this.currentScrollIndex -= 0.5f;

        if(this.currentScrollIndex % 1 == 0)
            this.initList((int) this.currentScrollIndex);
    }



    //----------------------------------------------------------------------------------------------------------------------------------------------
    private static class ListOption extends Button {
        private static final int DIVIDE_BORDER_THICKNESS = 1;
        private static final int DIVIDE_BORDER_COLOR = Color.LIGHT_GRAY_HEX;

        private int backgroundColor;
        private final Point mousePosition;
        private final boolean isClickable;
        private final int initialYPos;

        public void setIndex(int index){

            this.y = this.initialYPos + this.height * index;
        }

        public void setBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
        }


        public ListOption(int x, int y, int width, int height, ITextComponent content, boolean isClickable, int backgroundColor,  Button.IPressable onclick){
            super(x, y, width, height, content , onclick);

            this.initialYPos = y;
            this.mousePosition = new Point();
            this.isClickable = isClickable;
            this.backgroundColor = backgroundColor;
        }

        public void draw(MatrixStack matrixStack){
            AbstractGui.fill(matrixStack,
                    this.x,
                    this.y,
                    this.x + this.width,
                    this.y + this.height,
                    this.isClickable && this.isMouseOver(this.mousePosition.x, this.mousePosition.y) ? Color.LIGHT_GRAY_HEX : this.backgroundColor);


            Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.getMessage().getString(), this.x + 6,
                    this.y + this.height / 2f - TEXT_Y_OFFSET, TEXT_COLOR);


            AbstractGui.fill(matrixStack, this.x, this.y + this.height - DIVIDE_BORDER_THICKNESS, this.x + this.width, this.y + this.height, DIVIDE_BORDER_COLOR);
        }


        public void updateMousePosition(int mouseX, int mouseY){
            this.mousePosition.x = mouseX;
            this.mousePosition.y = mouseY;
        }

        public void onClick(){
            if(!this.isClickable)
                return;

            this.onPress();
        }


    }


}
