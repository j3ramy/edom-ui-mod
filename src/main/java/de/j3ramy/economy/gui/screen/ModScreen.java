package de.j3ramy.economy.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.j3ramy.economy.events.ModEvents;
import de.j3ramy.economy.gui.widgets.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;

public class ModScreen extends Screen {
    private final List<PopUpWindow> popUpWindows = new ArrayList<>();
    private final List<Button> buttons = new ArrayList<>();
    private final List<DropDown> dropDowns = new ArrayList<>();
    private final List<Tooltip> tooltips = new ArrayList<>();
    private final List<ScrollableTable> tables = new ArrayList<>();
    private final List<ScrollableList> scrollableList = new ArrayList<>();
    private final List<HorizontalLine> horizontalLines = new ArrayList<>();
    private final List<CenteredHorizontalLine> centeredHorizontalLines = new ArrayList<>();
    private final List<VerticalLine> verticalLines = new ArrayList<>();

    public void addPopUpWindow(PopUpWindow popUpWindow){
        this.popUpWindows.add(popUpWindow);
    }

    public void addButton(Button button){
        this.buttons.add(button);
    }

    public void addDropDown(DropDown dropDown){
        this.dropDowns.add(dropDown);
    }

    public void addTooltip(Tooltip tooltip){
        this.tooltips.add(tooltip);
    }

    public void addTable(ScrollableTable table){
        this.tables.add(table);
    }

    public void addList(ScrollableList scrollableList){
        this.scrollableList.add(scrollableList);
    }

    public void addHorizontalLine(HorizontalLine horizontalLine){
        this.horizontalLines.add(horizontalLine);
    }

    public void addCenteredHorizontalLine(CenteredHorizontalLine centeredHorizontalLine){
        this.centeredHorizontalLines.add(centeredHorizontalLine);
    }

    public void addVerticalLine(VerticalLine verticalLine){
        this.verticalLines.add(verticalLine);
    }

    public ModScreen() {
        super(new StringTextComponent(""));

        ModEvents.screens.add(this);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        for(Button button : this.buttons){
            button.updateMousePosition(mouseX, mouseY);
            button.render(matrixStack, mouseX, mouseY, partialTicks);
        }

        for(Tooltip tooltip : this.tooltips){
            tooltip.render(matrixStack, mouseX, mouseY);
        }

        for(DropDown dropDown : this.dropDowns){
            dropDown.updateMousePosition(mouseX, mouseY);
            dropDown.render(matrixStack, mouseX, mouseY, partialTicks);
            dropDown.update();
        }

        for(ScrollableTable table : this.tables){
            table.updateMousePosition(mouseX, mouseY);
            table.render(matrixStack, mouseX, mouseY, partialTicks);
            table.update();
        }

        for(ScrollableList scrollableList : this.scrollableList){
            scrollableList.updateMousePosition(mouseX, mouseY);
            scrollableList.render(matrixStack, mouseX, mouseY, partialTicks);
            scrollableList.update();
        }

        for(HorizontalLine horizontalLine : this.horizontalLines){
            horizontalLine.render(matrixStack, mouseX, mouseY, partialTicks);
        }

        for(CenteredHorizontalLine centeredHorizontalLine : this.centeredHorizontalLines){
            centeredHorizontalLine.render(matrixStack, mouseX, mouseY, partialTicks);
        }

        for(VerticalLine verticalLine : this.verticalLines){
            verticalLine.render(matrixStack, mouseX, mouseY, partialTicks);
        }

        for(PopUpWindow popUpWindow : this.popUpWindows){
            popUpWindow.updateMousePosition(mouseX, mouseY);
            popUpWindow.render(matrixStack, mouseX, mouseY, partialTicks);
        }
    }

    public void onClick(){

        for(DropDown dropDown : this.dropDowns){
            dropDown.onClick();
        }

        for(ScrollableList scrollableList : this.scrollableList){
            scrollableList.onClick();
        }

        for(ScrollableTable scrollableTable : this.tables){
            scrollableTable.onClick();
        }

        for(Button button : this.buttons){
            button.onClick();
        }

        for(PopUpWindow popUpWindow : this.popUpWindows){
            popUpWindow.onClick();
        }
    }

    public void onScroll(int scrollDelta){
        for(DropDown dropDown : this.dropDowns){
            dropDown.onScroll(scrollDelta);
        }

        for(ScrollableList scrollableList : this.scrollableList){
            scrollableList.onScroll(scrollDelta);
        }

        for(ScrollableTable scrollableTable : this.tables){
            scrollableTable.onScroll(scrollDelta);
        }
    }
}
