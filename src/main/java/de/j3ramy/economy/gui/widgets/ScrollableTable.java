
package de.j3ramy.economy.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.j3ramy.economy.utils.Color;
import de.j3ramy.economy.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ScrollableTable extends Widget {
    public static final int TEXT_COLOR = Color.WHITE;
    public static final int TEXT_Y_OFFSET = 2;
    private static final int BORDER_WIDTH = 1;

    private final Point mousePosition;
    private final List<TableRow> contents = new ArrayList<>();
    private final List<TableRow> contentFields = new ArrayList<>();
    private final int maxVisibleListElements;
    private final int elementHeight;
    private final boolean isFixedAttributeRow;
    private int selectedIndex = -1;
    private final int backgroundColor;
    private final int borderColor;
    private final int elementColor;
    private final int headRowColor;

    public ScrollableTable(int x, int y, int width, int height, int elementHeight, boolean fixedAttributeRow, int backgroundColor, int borderColor, int elementColor, int headRowColor){
        super(x, y, width, height, new StringTextComponent(""));

        this.mousePosition = new Point();
        this.maxVisibleListElements = this.height / elementHeight;
        this.elementHeight = elementHeight;
        this.isFixedAttributeRow = fixedAttributeRow;
        this.backgroundColor = backgroundColor;
        this.borderColor = borderColor;
        this.elementColor = elementColor;
        this.headRowColor = headRowColor;
    }

    public void setAttributeColumns(ArrayList<String> columnNames){
        if(!this.contents.isEmpty())
            this.contents.set(0, new TableRow(this.x, this.y, this.width, this.elementHeight, columnNames, false, this.headRowColor, (click)->{}));
        else
            this.addRow(columnNames, false, this.headRowColor, (click)->{});
    }

    @Nullable
    public TableRow getSelectedRow(){
        if(this.selectedIndex == -1 || this.selectedIndex == 0)
            return null;

        return this.contents.get(selectedIndex);
    }

    @Nullable
    public TableRow getHoveredRow(){
        for(TableRow row : this.contentFields){
            if(row.isHovered())
                return row;
        }

        return null;
    }

    public void addRow(ArrayList<String> rowContent, boolean isClickable, int backgroundColor, Button.IPressable onClick){
        this.contents.add(new TableRow(this.x, this.y, this.width, this.elementHeight, rowContent, isClickable, backgroundColor, onClick));

        this.initList(0);
    }

    private boolean needsScrolling(){
        return this.contents.size() > this.maxVisibleListElements;
    }

    private void initList(int startIndex){
        this.contentFields.clear();

        if(this.isFixedAttributeRow) this.contentFields.add(this.contents.get(0));

        if(this.needsScrolling()){
            for(int i = this.isFixedAttributeRow ? 1 : 0; i < this.maxVisibleListElements; i++){
                this.contentFields.add(this.contents.get(i + startIndex));
                this.contentFields.get(i).setIndex(i);
            }
        }
        else{
            for(int i = this.isFixedAttributeRow ? 1 : 0; i < this.contents.size(); i++){
                this.contentFields.add(this.contents.get(i + startIndex));
                this.contentFields.get(i).setIndex(i);
            }
        }
    }

    public void clearSelectedIndex(){
        this.selectedIndex = -1;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if(this.mousePosition == null)
            return;

        AbstractGui.fill(matrixStack, this.x - BORDER_WIDTH, this.y - BORDER_WIDTH,
                this.x + this.width + BORDER_WIDTH, this.y + this.height + BORDER_WIDTH, this.borderColor);
        AbstractGui.fill(matrixStack, this.x, this.y, this.x + this.width, this.y + this.height, this.backgroundColor);
        this.drawContent(matrixStack);
        this.drawToolTip(matrixStack);
    }

    private void drawToolTip(MatrixStack matrixStack){
        if(this.isHovered()){
            if(this.getHoveredRow() != null){
                Tooltip tooltip = new Tooltip(this.getHoveredRow().getHoveredColumnText(), null);
                tooltip.render(matrixStack, this.mousePosition.x, this.mousePosition.y);
            }
        }
    }

    private void drawContent(MatrixStack matrixStack){
        for (int i = 0; i < this.contentFields.size(); i++) {
            if (this.contentFields.get(i) != null){
                this.contentFields.get(i) .draw(matrixStack);

                if(i != 0)
                    this.contentFields.get(i).setBackgroundColor(i == this.selectedIndex ? Color.LIGHT_GRAY_HEX : this.elementColor);
            }
        }
    }

    public void update(){
        for (TableRow contentField : this.contentFields) {
            if(contentField != null)
                contentField.updateMousePosition(this.mousePosition.x, this.mousePosition.y);
        }
    }

    public void updateMousePosition(int mouseX, int mouseY){
        this.mousePosition.x = mouseX;
        this.mousePosition.y = mouseY;
    }

    public void onClick(){
        for(int i = 1; i < this.contentFields.size(); i++){
            if(contentFields.get(i) != null && contentFields.get(i).isHovered()){
                this.selectedIndex = i;
                contentFields.get(i).onClick();
            }
        }
    }

    public void clear(){
        this.contents.clear();
        this.contentFields.clear();
        this.clearSelectedIndex();
    }

    public boolean isHovered(){
        Rectangle table = new Rectangle(this.x, this.y, this.x + this.width, this.y + this.height);
        return table.contains(new Point(this.mousePosition.x, this.mousePosition.y));
    }

    private float currentScrollIndex = 0;
    public void onScroll(int scrollDelta){
        if(!this.isHovered())
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
    public static class TableRow extends Button {
        private static final int DIVIDE_BORDER_THICKNESS = 1;

        private int backgroundColor;
        private final Point mousePosition;
        private final boolean isClickable;
        private final int initialYPos;
        private final ArrayList<String> content;
        private final int columnWidth;
        private final int maxWordLength;

        public void setBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        public void setIndex(int index){
            this.y = this.initialYPos + this.height * index;
        }

        public ArrayList<String> getContent() {
            return this.content;
        }

        public String getHoveredColumnText(){
            return this.content.get(this.getHoveredColumn());
        }

        public TableRow(int x, int y, int width, int height, ArrayList<String> content, boolean isClickable, int backgroundColor, IPressable onclick){
            super(x, y, width, height, new StringTextComponent("") , onclick);

            this.initialYPos = y;
            this.mousePosition = new Point();
            this.isClickable = isClickable;
            this.columnWidth = this.width / content.size();
            this.content = content;
            this.maxWordLength = (this.columnWidth * 2 - 12) / GuiUtils.LETTER_SIZE; //GuiUtils.LETTER_SIZE = width of letter;
            this.backgroundColor = backgroundColor;
        }

        public void draw(MatrixStack matrixStack){
            AbstractGui.fill(matrixStack, this.x, this.y, this.x + this.width, this.y + this.height, this.isHovered() && this.isClickable ? Color.LIGHT_GRAY_HEX : this.backgroundColor);

            this.drawRow(matrixStack);

            AbstractGui.fill(matrixStack, this.x, this.y + this.height - DIVIDE_BORDER_THICKNESS, this.x + this.width, this.y + this.height,
                    Color.LIGHT_GRAY_HEX);
        }


        private int getHoveredColumn(){
            for(int i = this.content.size() - 1; i >= 0; i--){
                Rectangle column = new Rectangle(this.x + i * this.columnWidth, this.y, this.x + i * this.columnWidth + this.columnWidth, this.y + this.height);
                if(column.contains(this.mousePosition))
                    return i;
            }

            return 0;
        }

        private void drawRow(MatrixStack matrixStack){
            for(int i = 0; i < this.content.size(); i++){
                //content
                GlStateManager.pushMatrix();
                GlStateManager.scalef(.5f, .5f, .5f);
                Minecraft.getInstance().fontRenderer.drawString(matrixStack, GuiUtils.getFormattedLabel(this.maxWordLength, this.content.get(i)).getText(), (this.x + 3 + this.columnWidth * i) * 2,
                        (this.y + this.height / 2f - TEXT_Y_OFFSET) * 2,  this.isHovered() && this.isClickable ? Color.DARK_GRAY_HEX : TEXT_COLOR);
                GlStateManager.popMatrix();

                //Right border
                if(i != 0)
                    AbstractGui.fill(matrixStack, this.x + this.columnWidth * i, this.y, this.x + this.columnWidth * i + DIVIDE_BORDER_THICKNESS, this.y + this.height,
                            Color.LIGHT_GRAY_HEX);
            }
        }

        public void updateMousePosition(int mouseX, int mouseY){
            this.mousePosition.x = mouseX;
            this.mousePosition.y = mouseY;
        }

        public boolean isHovered(){
            return this.isMouseOver(this.mousePosition.x, this.mousePosition.y);
        }

        public void onClick(){
            if(!this.isClickable)
                return;

            this.onPress();
        }


    }


}
