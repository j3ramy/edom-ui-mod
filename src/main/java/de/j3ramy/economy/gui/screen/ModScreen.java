package de.j3ramy.economy.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.j3ramy.economy.events.ModEvents;
import de.j3ramy.economy.gui.widgets.Button;
import de.j3ramy.economy.gui.widgets.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModScreen extends Screen {
    private boolean disableScreen;
    private final List<AlertPopUp> alertPopUps = new ArrayList<>();
    private final List<ConfirmPopUp> confirmPopUps = new ArrayList<>();
    private final List<ProgressPopUp> progressPopUps = new ArrayList<>();
    private Taskbar taskbar;
    private final List<Button> buttons = new ArrayList<>();
    private final List<ImageButton> imageButtons = new ArrayList<>();
    private final List<DropDown> dropDowns = new ArrayList<>();
    private final List<Tooltip> tooltips = new ArrayList<>();
    private final List<ScrollableTable> tables = new ArrayList<>();
    private final List<ScrollableList> scrollableList = new ArrayList<>();
    private final List<HorizontalLine> horizontalLines = new ArrayList<>();
    private final List<TextFieldWidget> textFields = new ArrayList<>();
    private final List<CenteredHorizontalLine> centeredHorizontalLines = new ArrayList<>();
    private final List<VerticalLine> verticalLines = new ArrayList<>();

    public void addAlertPopUp(AlertPopUp alertPopUp){
        this.alertPopUps.add(alertPopUp);
    }
    public void addProgressPopUp(ProgressPopUp progressPopUp){
        this.progressPopUps.add(progressPopUp);
    }
    public void addConfirmPopUp(ConfirmPopUp confirmPopUp){
        this.confirmPopUps.add(confirmPopUp);
    }
    public void setTaskbar(Taskbar taskbar) {
        this.taskbar = taskbar;
    }
    public void addButton(Button button){
        this.buttons.add(button);
    }
    public void addImageButton(ImageButton button){
        this.imageButtons.add(button);
    }
    public void addTextField(TextFieldWidget textField){
        this.textFields.add(textField);
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

    private final Point mousePosition = new Point();

    public ModScreen() {
        super(new StringTextComponent(""));

        ModEvents.screens.add(this);
    }

    private void update(int mouseX, int mouseY){
        this.mousePosition.x = mouseX;
        this.mousePosition.y = mouseY;

        for(Tooltip tooltip : this.tooltips){
            if(tooltip.getButton() != null)
                tooltip.isVisible = tooltip.getButton().isMouseOver(mouseX, mouseY);
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if(this.disableScreen)
            return;

        this.update(mouseX, mouseY);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        for(Button button : this.buttons){
            if(button != null){
                button.update(mouseX, mouseY);
                button.render(matrixStack);
            }
        }

        for(ImageButton button : this.imageButtons){
            if(button != null){
                button.render(matrixStack, mouseX, mouseY, partialTicks);
            }
        }

        for(DropDown dropDown : this.dropDowns){
            if(dropDown != null){
                dropDown.update(mouseX, mouseY);
                dropDown.render(matrixStack);
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

        for(TextFieldWidget textField : this.textFields){
            if(textField != null){
                textField.render(matrixStack, mouseX, mouseY, partialTicks);
                //textField.tick();
            }
        }

        for(HorizontalLine horizontalLine : this.horizontalLines){
            if(horizontalLine != null){
                horizontalLine.render(matrixStack);
            }

        }

        for(CenteredHorizontalLine centeredHorizontalLine : this.centeredHorizontalLines){
            if(centeredHorizontalLine != null){
                centeredHorizontalLine.render(matrixStack);
            }

        }

        for(VerticalLine verticalLine : this.verticalLines){
            if(verticalLine != null){
                verticalLine.render(matrixStack);
            }

        }

        if(this.taskbar != null){
            this.taskbar.render(matrixStack, mouseX, mouseY, partialTicks);
        }

        for(AlertPopUp alertPopUp : this.alertPopUps){
            if(alertPopUp != null){
                alertPopUp.update(mouseX, mouseY);
                alertPopUp.render(matrixStack);
            }
        }

        for(ConfirmPopUp confirmPopUp : this.confirmPopUps){
            if(confirmPopUp != null){
                confirmPopUp.update(mouseX, mouseY);
                confirmPopUp.render(matrixStack);
            }
        }

        for(ProgressPopUp progressPopUp : this.progressPopUps){
            if(progressPopUp != null){
                progressPopUp.update(mouseX, mouseY);
                progressPopUp.render(matrixStack);
            }
        }

        if(!this.isPopUpVisible()){
            for(Tooltip tooltip : this.tooltips){
                if(tooltip != null)
                    tooltip.render(matrixStack, mouseX, mouseY);
            }
        }
    }

    public void onClick(int mouseButton){
        if(this.disableScreen)
            return;

        if(this.isPopUpVisible() && mouseButton == 0){
            for(AlertPopUp alertPopUp : this.alertPopUps) {
                if (alertPopUp != null) {
                    alertPopUp.onClick();
                }
            }

            for(ConfirmPopUp confirmPopUp : this.confirmPopUps) {
                if (confirmPopUp != null) {
                    confirmPopUp.onClick();
                }
            }

            return;
        }

        for(DropDown dropDown : this.dropDowns){
            if(dropDown != null && mouseButton == 0)
                dropDown.onClick();
        }

        for(ScrollableList scrollableList : this.scrollableList){
            if(scrollableList != null && mouseButton == 0)
                scrollableList.onClick();
        }

        for(ScrollableTable scrollableTable : this.tables){
            if(scrollableTable != null && mouseButton == 0)
                scrollableTable.onClick();
        }

        for(TextFieldWidget textField : this.textFields){
            if(textField != null)
                textField.mouseClicked(this.mousePosition.x, this.mousePosition.y, mouseButton);
        }

        for(Button button : this.buttons){
            if(button != null && mouseButton == 0)
                button.onClick();
        }

        for(ImageButton button : this.imageButtons){
            if(button != null && mouseButton == 0){
                if(button.isMouseOver(this.mousePosition.x, this.mousePosition.y))
                    button.onClick(this.mousePosition.x, this.mousePosition.y);
            }
        }

        if(this.taskbar != null && mouseButton == 0)
            this.taskbar.onClick();
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

    public void onKeyPressed(int keyCode){
        for(TextFieldWidget textField : this.textFields){
            if(textField != null)
                textField.keyPressed(keyCode, -1, -1);
        }
    }

    public void onCharTyped(char c){
        for(TextFieldWidget textField : this.textFields){
            if(textField != null)
                textField.charTyped(c, -1);
        }
    }

    public void clearScreen(){
        this.confirmPopUps.clear();
        this.alertPopUps.clear();
        this.progressPopUps.clear();
        this.buttons.clear();
        this.imageButtons.clear();
        this.dropDowns.clear();
        this.textFields.clear();
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

    public boolean isPopUpVisible(){
        for(AlertPopUp alertPopUp : this.alertPopUps) {
            if (alertPopUp != null && !alertPopUp.isHidden()) {
                return true;
            }
        }

        for(ConfirmPopUp confirmPopUp : this.confirmPopUps) {
            if (confirmPopUp != null && !confirmPopUp.isHidden()) {
                return true;
            }
        }

        for(ProgressPopUp progressPopUp : this.progressPopUps) {
            if (progressPopUp != null && !progressPopUp.isHidden()) {
                return true;
            }
        }

        return false;
    }
}
