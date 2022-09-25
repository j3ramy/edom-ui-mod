package de.j3ramy.edomui.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.j3ramy.edomui.gui.widgets.*;
import de.j3ramy.edomui.gui.widgets.Button;
import de.j3ramy.edomui.gui.widgets.TextField;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class CustomScreen extends Screen {
    public static CustomScreen screen;

    public final List<Widget> widgets = new ArrayList<>();
    public final List<net.minecraft.client.gui.widget.Widget> mcWidgets = new ArrayList<>();
    protected final Point mousePosition = new Point();
    public boolean isHidden;

    public CustomScreen() {
        super(new StringTextComponent(""));
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY) {
        if(this.isHidden)
            return;

        super.render(matrixStack, this.mousePosition.x, this.mousePosition.y, -1);

        for(Widget widget : this.widgets){
            if(widget != null && !(widget instanceof Tooltip)){
                widget.render(matrixStack);
                widget.update(mouseX, mouseY);
            }
        }

        /*
        for(net.minecraft.client.gui.widget.Widget widget : this.mcWidgets){
            if(widget != null){
                widget.render(matrixStack, mouseX, mouseY);
            }
        }

         */

        for(Widget widget : this.widgets){
            if(widget instanceof Tooltip){
                widget.render(matrixStack);
            }
        }
    }

    public void update(int mouseX, int mouseY){
        if(this.isHidden)
            return;

        this.mousePosition.x = mouseX;
        this.mousePosition.y = mouseY;

        for(Widget widget : this.widgets){
            widget.update(mouseX, mouseY);
        }
    }

    public void onClick(int mouseButton) {
        if(this.isHidden)
            return;

        try {
            for (Widget widget : this.widgets) {
                if (mouseButton == 0) {
                    if (this.isPopUpVisible() || this.isDropDownUnfolded()) {
                        if (widget instanceof AlertPopUp) {
                            widget.onClick();
                        }

                        if (widget instanceof ConfirmPopUp) {
                            widget.onClick();
                        }

                        if (widget instanceof DropDown) {
                            widget.onClick();
                        }
                    }
                    else{
                        if (widget instanceof ScrollableList) {
                            widget.onClick();
                        }

                        if (widget instanceof ScrollableTable) {
                            widget.onClick();
                        }

                        if (widget instanceof Button) {
                            widget.onClick();
                        }

                        if (widget instanceof TextField) {
                            widget.onClick();
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
        catch (ConcurrentModificationException ignored){}
    }

    public void onScroll(int scrollDelta){
        if(this.isHidden)
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
        if(this.isHidden)
            return;

        /*
        for(net.minecraft.client.gui.widget.Widget widget : this.mcWidgets){
            if(widget instanceof TextFieldWidget)
                widget.keyPressed(keyCode, -1, -1);
        }

         */

        for(Widget widget : this.widgets)
        {
            if(widget instanceof TextField){
                ((TextField) widget).onKeyPressed(keyCode);
            }
        }
    }

    public void onCharTyped(char c){
        if(this.isHidden)
            return;

        /*
        for(net.minecraft.client.gui.widget.Widget widget : this.mcWidgets){
            if(widget instanceof TextFieldWidget){
                if(widget.isFocused()){
                    widget.charTyped(c, -1);
                }
            }
        }

         */

        for(Widget widget : this.widgets)
        {
            if(widget instanceof TextField){
                ((TextField) widget).onCharTyped(c);
            }
        }
    }

    public void addWidget(Widget widget){
        this.widgets.add(widget);
    }

    public void addMcWidget(net.minecraft.client.gui.widget.Widget widget){
        this.mcWidgets.add(widget);
    }

    public void clearScreen(){
        this.widgets.clear();
        this.mcWidgets.clear();
    }

    public boolean isPopUpVisible(){
        for(Widget widget : this.widgets) {
            if (widget instanceof AlertPopUp && !widget.isHidden) {
                return true;
            }

            if (widget instanceof ConfirmPopUp && !widget.isHidden) {
                return true;
            }

            if (widget instanceof ProgressPopUp && !widget.isHidden) {
                return true;
            }
        }

        return false;
    }

    public boolean isDropDownUnfolded(){
        for(Widget widget : this.widgets) {
            if (widget instanceof DropDown && !widget.isHidden && (((DropDown) widget).isUnfolded())){
                return true;
            }
        }

        return false;
    }
}
