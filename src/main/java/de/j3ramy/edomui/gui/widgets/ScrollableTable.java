
package de.j3ramy.edomui.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.j3ramy.edomui.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class ScrollableTable extends Widget {
    private final int maxVisibleListElements, elementHeight;
    private final boolean isFixedAttributeRow;
    private final List<TableRow> contents = new ArrayList<>(), contentFields = new ArrayList<>();
    private int selectedIndex = -1;


    public ScrollableTable(int x, int y, int width, int height, int elementHeight, boolean fixedAttributeRow){
        super(x, y, width, height);

        this.maxVisibleListElements = elementHeight != 0 ? this.height / elementHeight : 0;
        this.elementHeight = elementHeight;
        this.isFixedAttributeRow = fixedAttributeRow;
    }

    public void setAttributeRow(ArrayList<String> columnNames, int backgroundColor, int hoverColor){
        if(!this.contents.isEmpty())
            this.contents.set(0, new TableRow(this.leftPos, this.topPos, this.width, this.elementHeight, columnNames, false, backgroundColor, hoverColor, ()->{}));
        else
            this.addRow(columnNames, false, backgroundColor, hoverColor, ()->{});
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
            if(row.isMouseOver())
                return row;
        }

        return null;
    }

    public boolean isHovered(){
        Rectangle table = new Rectangle(this.leftPos, this.topPos, this.leftPos + this.width, this.topPos + this.height);
        return table.contains(new Point(this.mousePosition.x, this.mousePosition.y));
    }

    private boolean needsScrolling(){
        return this.contents.size() > this.maxVisibleListElements;
    }

    public void addRow(ArrayList<String> rowContent, boolean isClickable, int backgroundColor, int hoverColor, Button.IClickable onClick){
        this.contents.add(new TableRow(this.leftPos, this.topPos, this.width, this.elementHeight, rowContent, isClickable, backgroundColor, hoverColor,  onClick));

        this.initList(0);
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

    public void clear(){
        this.contents.clear();
        this.contentFields.clear();
        this.clearSelectedIndex();
    }

    @Override
    public void render(MatrixStack matrixStack) {
        if(this.isHidden())
            return;

        super.render(matrixStack);
        AbstractGui.fill(matrixStack, this.leftPos - this.borderThickness, this.topPos - this.borderThickness,
                this.leftPos + this.width + this.borderThickness, this.topPos + this.height + this.borderThickness, this.borderColor);

        AbstractGui.fill(matrixStack, this.leftPos, this.topPos, this.leftPos + this.width, this.topPos + this.height, this.backgroundColor);

        this.renderContent(matrixStack);
        this.renderToolTip(matrixStack);
    }

    private void renderToolTip(MatrixStack matrixStack){
        if(this.isHovered()){
            if(this.getHoveredRow() != null){
                Tooltip tooltip = new Tooltip(this.getHoveredRow().getHoveredColumnText(), null);
                tooltip.update(this.mousePosition.x, this.mousePosition.y);
                tooltip.render(matrixStack);
            }
        }
    }

    private void renderContent(MatrixStack matrixStack){
        for (int i = 0; i < this.contentFields.size(); i++) {
            if (this.contentFields.get(i) != null){
                this.contentFields.get(i).render(matrixStack);
                if(i != 0)
                    this.contentFields.get(i).setBackgroundColor(i == this.selectedIndex ? this.contentFields.get(i).hoverBackgroundColor : this.contentFields.get(i).oldBackground);
            }
        }
    }

    @Override
    public void update(int mouseX, int mouseY){
        if(this.isHidden())
            return;

        super.update(mouseX, mouseY);
        for (TableRow contentField : this.contentFields) {
            if(contentField != null)
                contentField.update(mouseX, mouseY);
        }
    }

    public void onClick(){
        if(this.isHidden())
            return;

        for(int i = 1; i < this.contentFields.size(); i++){
            if(contentFields.get(i) != null && contentFields.get(i).isMouseOver()){
                this.selectedIndex = i;
                contentFields.get(i).onClick();
            }
        }
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
        private final boolean isClickable;
        private final int initialYPos;
        private final ArrayList<String> content;
        private final int columnWidth;
        private final int maxWordLength;
        private final int oldBackground;

        public TableRow(int x, int y, int width, int height, ArrayList<String> content, boolean isClickable, int backgroundColor, int hoverColor, IClickable onclick){
            super(x, y, width, height , "", onclick);

            this.initialYPos = y;
            this.mousePosition = new Point();
            this.isClickable = isClickable;
            this.columnWidth = this.width / content.size();
            this.content = content;
            this.maxWordLength = (this.columnWidth * 2 - 12) / GuiUtils.LETTER_WIDTH; //GuiUtils.LETTER_SIZE = width of letter;

            this.setBackgroundColor(backgroundColor);
            this.setHoverBackgroundColor(hoverColor);
            this.setHoverBorderColor(this.borderColor);

            this.oldBackground = this.backgroundColor;
        }

        public void setBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        public void setIndex(int index){
            this.topPos = this.initialYPos + this.height * index;
        }

        public ArrayList<String> getContent() {
            return this.content;
        }

        public String getHoveredColumnText(){
            return this.content.get(this.getHoveredColumn());
        }

        private int getHoveredColumn(){
            for(int i = this.content.size() - 1; i >= 0; i--){
                Rectangle column = new Rectangle(this.leftPos + i * this.columnWidth, this.topPos, this.leftPos + i * this.columnWidth + this.columnWidth, this.topPos + this.height);
                if(column.contains(this.mousePosition))
                    return i;
            }

            return 0;
        }

        public void render(MatrixStack matrixStack){
            super.render(matrixStack);
            this.drawRow(matrixStack);
        }

        private void drawRow(MatrixStack matrixStack){
            for(int i = 0; i < this.content.size(); i++){
                //content
                GlStateManager.pushMatrix();
                GlStateManager.scalef(.5f, .5f, .5f);
                Minecraft.getInstance().fontRenderer.drawString(matrixStack, GuiUtils.getFormattedLabel(this.maxWordLength, this.content.get(i)), (this.leftPos + 3 + this.columnWidth * i) * 2,
                        (this.topPos + this.height / 2f - this.yOffset / 2f) * 2,  this.isMouseOver() && this.isClickable ? this.textColor : this.hoverTextColor);

                GlStateManager.popMatrix();

                //Right border
                if(i != 0)
                    AbstractGui.fill(matrixStack, this.leftPos + this.columnWidth * i, this.topPos, this.leftPos + this.columnWidth * i + DIVIDE_BORDER_THICKNESS, this.topPos + this.height,
                            this.borderColor);
            }
        }

        public void onClick(){
            if(!this.isClickable || !this.isMouseOver())
                return;

            super.onClick();
        }
    }
}
