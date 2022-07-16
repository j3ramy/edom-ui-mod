package de.j3ramy.economy.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.j3ramy.economy.events.ModEvents;
import de.j3ramy.economy.gui.widgets.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;

public class ModScreen extends Screen {
    private boolean disableScreen;
    private AlertPopUp alertPopUp;
    private ConfirmPopUp confirmPopUp;
    private final List<Button> buttons = new ArrayList<>();
    private final List<DropDown> dropDowns = new ArrayList<>();
    private final List<Tooltip> tooltips = new ArrayList<>();
    private final List<ScrollableTable> tables = new ArrayList<>();
    private final List<ScrollableList> scrollableList = new ArrayList<>();
    private final List<HorizontalLine> horizontalLines = new ArrayList<>();
    private final List<CenteredHorizontalLine> centeredHorizontalLines = new ArrayList<>();
    private final List<VerticalLine> verticalLines = new ArrayList<>();

    public void setAlertPopUp(AlertPopUp alertPopUp){
        this.alertPopUp = alertPopUp;
    }
    public void setConfirmPopUp(ConfirmPopUp confirmPopUp){
        this.confirmPopUp = confirmPopUp;
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
        if(this.disableScreen)
            return;

        super.render(matrixStack, mouseX, mouseY, partialTicks);

        for(Button button : this.buttons){
            if(button != null){
                button.updateMousePosition(mouseX, mouseY);
                button.render(matrixStack, mouseX, mouseY, partialTicks);
            }
        }

        for(DropDown dropDown : this.dropDowns){
            if(dropDown != null){
                dropDown.updateMousePosition(mouseX, mouseY);
                dropDown.render(matrixStack, mouseX, mouseY, partialTicks);
                dropDown.update();
            }

        }

        for(ScrollableTable table : this.tables){
            if(table != null){
                table.updateMousePosition(mouseX, mouseY);
                table.render(matrixStack, mouseX, mouseY, partialTicks);
                table.update();
            }

        }

        for(ScrollableList scrollableList : this.scrollableList){
            if(scrollableList != null){
                scrollableList.updateMousePosition(mouseX, mouseY);
                scrollableList.render(matrixStack, mouseX, mouseY, partialTicks);
                scrollableList.update();
            }

        }

        for(HorizontalLine horizontalLine : this.horizontalLines){
            if(horizontalLine != null){
                horizontalLine.render(matrixStack, mouseX, mouseY, partialTicks);
            }

        }

        for(CenteredHorizontalLine centeredHorizontalLine : this.centeredHorizontalLines){
            if(centeredHorizontalLine != null){
                centeredHorizontalLine.render(matrixStack, mouseX, mouseY, partialTicks);
            }

        }

        for(VerticalLine verticalLine : this.verticalLines){
            if(verticalLine != null){
                verticalLine.render(matrixStack, mouseX, mouseY, partialTicks);
            }

        }

        if(this.alertPopUp != null){
            this.alertPopUp.updateMousePosition(mouseX, mouseY);
            this.alertPopUp.render(matrixStack, mouseX, mouseY, partialTicks);
        }

        if(this.confirmPopUp != null){
            this.confirmPopUp.updateMousePosition(mouseX, mouseY);
            this.confirmPopUp.render(matrixStack, mouseX, mouseY, partialTicks);
        }

        for(Tooltip tooltip : this.tooltips){
            if(tooltip != null)
                tooltip.render(matrixStack, mouseX, mouseY);
        }
    }

    public void onClick(){
        if(this.disableScreen)
            return;

        for(DropDown dropDown : this.dropDowns){
            if(dropDown != null)
                dropDown.onClick();
        }

        for(ScrollableList scrollableList : this.scrollableList){
            if(scrollableList != null)
                scrollableList.onClick();
        }

        for(ScrollableTable scrollableTable : this.tables){
            if(scrollableTable != null)
                scrollableTable.onClick();
        }

        for(Button button : this.buttons){
            if(button != null)
                button.onClick();
        }

        if(this.alertPopUp != null)
            this.alertPopUp.onClick();

        if(this.confirmPopUp != null)
            this.confirmPopUp.onClick();
    }

    public void onScroll(int scrollDelta){
        if(this.disableScreen)
            return;

        for(DropDown dropDown : this.dropDowns){
            if(dropDown != null)
                dropDown.onScroll(scrollDelta);
        }

        for(ScrollableList scrollableList : this.scrollableList){
            if(scrollableList != null)
                scrollableList.onScroll(scrollDelta);
        }

        for(ScrollableTable scrollableTable : this.tables){
            if(scrollableTable != null)
                scrollableTable.onScroll(scrollDelta);
        }
    }

    public void clearScreen(){
        this.confirmPopUp = null;
        this.alertPopUp = null;
        this.buttons.clear();
        this.dropDowns.clear();
        this.tooltips.clear();
        this.tables.clear();
        this.scrollableList.clear();
        this.horizontalLines.clear();
        this.centeredHorizontalLines.clear();
        this.verticalLines.clear();
    }

    public void enable(){
        this.disableScreen = false;
    }

    public void disable(){
        this.disableScreen = true;
    }
}
