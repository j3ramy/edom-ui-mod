package de.j3ramy.edomui.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.j3ramy.edomui.gui.widgets.Button;
import de.j3ramy.edomui.gui.widgets.TextField;
import de.j3ramy.edomui.gui.widgets.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class CustomScreen extends Screen {
    public static CustomScreen screen;

    public final List<Widget> widgets = new ArrayList<>();
    protected final Point mousePosition = new Point();
    public boolean isHidden;

    public CustomScreen() {
        super(new StringTextComponent(""));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        this.render(matrixStack, mouseX, mouseY);
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY) {
        if(this.isHidden)
            return;

        for(Widget widget : this.widgets){
            if(widget != null && !(widget instanceof Tooltip)){
                widget.render(matrixStack);
                widget.update(mouseX, mouseY);
            }
        }

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
            if(widget != null)
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

    public void clearScreen(){
        this.widgets.clear();
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
