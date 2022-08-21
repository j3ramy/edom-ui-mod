package de.j3ramy.edomui.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.j3ramy.edomui.gui.widgets.*;
import de.j3ramy.edomui.gui.widgets.Button;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class CustomScreen extends Screen {
    protected final List<Widget> widgets = new ArrayList<>();
    protected final List<net.minecraft.client.gui.widget.Widget> mcWidgets = new ArrayList<>();
    protected final Point mousePosition = new Point();
    protected boolean isDisabled, isInteractable = true;


    public CustomScreen() {
        super(new StringTextComponent(""));
        //ModEvents.screens.add(this);
    }


    public void addWidget(Widget widget){
        this.widgets.add(widget);
    }
    public void addMcWidget(net.minecraft.client.gui.widget.Widget widget){
        this.mcWidgets.add(widget);
    }

    public List<Widget> getWidgets() {
        return this.widgets;
    }

    public List<net.minecraft.client.gui.widget.Widget> getMcWidgets() {
        return this.mcWidgets;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if(this.isDisabled)
            return;

        this.update(mouseX, mouseY);
        super.render(matrixStack, this.mousePosition.x, this.mousePosition.y, partialTicks);

        for(net.minecraft.client.gui.widget.Widget widget : this.mcWidgets){
            if(widget != null){
                widget.render(matrixStack, mouseX, mouseY, partialTicks);
            }
        }

        for(Widget widget : this.widgets){
            if(widget != null){
                widget.render(matrixStack);
                widget.update(mouseX, mouseY);
            }
        }
    }

    private void update(int mouseX, int mouseY){
        if(this.isDisabled || !this.isInteractable)
            return;

        this.mousePosition.x = mouseX;
        this.mousePosition.y = mouseY;

        for(Widget widget : this.widgets){
            widget.update(mouseX, mouseY);
        }

        if(!this.isInteractable){
            for(net.minecraft.client.gui.widget.Widget widget : this.mcWidgets){
                if(widget != null){
                    widget.active = false;
                }
            }

            for(Widget widget : this.widgets){
                if(widget instanceof Button){
                    ((Button) widget).setEnabled(false);
                }
            }
        }
        else{
            for(net.minecraft.client.gui.widget.Widget widget : this.mcWidgets){
                if(widget != null){
                    widget.active = true;
                }
            }

            for(Widget widget : this.widgets){
                if(widget instanceof Button){
                    ((Button) widget).setEnabled(true);
                }
            }
        }
    }

    /*
    * @throws ConcurrentModificationException if popup gets added inside a lambda expression
     */
    public void onClick(int mouseButton) {
        if(this.isDisabled || !this.isInteractable)
            return;

        try {
            for (Widget widget : this.widgets) {
                if (mouseButton == 0) {
                    if (this.isPopUpVisible() || this.isDropDownUnfolded()) {
                        if (widget instanceof AlertPopUp) {
                            ((AlertPopUp) widget).onClick();
                        }

                        if (widget instanceof ConfirmPopUp) {
                            ((ConfirmPopUp) widget).onClick();
                        }

                        if (widget instanceof DropDown) {
                            ((DropDown) widget).onClick();
                        }
                    }
                    else{
                        if (widget instanceof ScrollableList) {
                            ((ScrollableList) widget).onClick();
                        }

                        if (widget instanceof ScrollableTable) {
                            ((ScrollableTable) widget).onClick();
                        }

                        if (widget instanceof Button) {
                            ((Button) widget).onClick();
                        }
                    }
                }
            }

            for (net.minecraft.client.gui.widget.Widget w : this.mcWidgets) {
                if (w instanceof ImageButton) {
                    if(w.isMouseOver(this.mousePosition.x, this.mousePosition.y)){
                        w.onClick(this.mousePosition.x, this.mousePosition.y);
                    }

                }

                if (w instanceof TextFieldWidget) {
                    ((TextFieldWidget) w).setFocused2(false);

                    if(w.isMouseOver(this.mousePosition.x, this.mousePosition.y)){
                        w.mouseClicked(this.mousePosition.x, this.mousePosition.y, mouseButton);
                    }
                }
            }
        }
        catch (ConcurrentModificationException e){
            System.out.println(e.getMessage() + ": Ensure that your pop up windows are not added inside a lambda expression");
        }
    }

    public void onScroll(int scrollDelta){
        if(this.isDisabled || !this.isInteractable)
            return;

        if(this.isPopUpVisible())
            return;

        for (Widget widget : this.widgets) {
            if (widget != null) {
                if (widget instanceof ScrollableList){
                    ((ScrollableList) widget).onScroll(scrollDelta);
                    return;
                }

                if (widget instanceof ScrollableTable){
                    ((ScrollableTable) widget).onScroll(scrollDelta);
                    return;
                }

                if (widget instanceof DropDown){
                    ((DropDown) widget).onScroll(scrollDelta);
                    return;
                }
            }
        }
    }

    public void onKeyPressed(int keyCode){
        if(this.isDisabled || !this.isInteractable)
            return;

        for(net.minecraft.client.gui.widget.Widget widget : this.mcWidgets){
            if(widget instanceof TextFieldWidget)
                widget.keyPressed(keyCode, -1, -1);
        }
    }

    public void onCharTyped(char c){
        if(this.isDisabled || !this.isInteractable)
            return;

        for(net.minecraft.client.gui.widget.Widget widget : this.mcWidgets){
            if(widget instanceof TextFieldWidget)
                widget.charTyped(c, -1);
        }
    }

    public void clearScreen(){
        this.widgets.clear();
        this.mcWidgets.clear();
    }

    public void setDisabled(boolean disabled) {
        this.isDisabled = disabled;
    }

    public void setInteractable(boolean interactable) {
        this.isInteractable = interactable;
    }

    private boolean isPopUpVisible(){
        for(Widget widget : this.widgets) {
            if (widget instanceof AlertPopUp && !widget.isHidden()) {
                return true;
            }

            if (widget instanceof ConfirmPopUp && !widget.isHidden()) {
                return true;
            }

            if (widget instanceof ProgressPopUp && !widget.isHidden()) {
                return true;
            }
        }

        return false;
    }

    private boolean isDropDownUnfolded(){
        for(Widget widget : this.widgets) {
            if (widget instanceof DropDown && !widget.isHidden()) {
                return ((DropDown) widget).isUnfolded();
            }
        }

        return false;
    }
}
