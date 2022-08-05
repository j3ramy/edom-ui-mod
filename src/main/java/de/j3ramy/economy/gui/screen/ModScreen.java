package de.j3ramy.economy.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.j3ramy.economy.events.ModEvents;
import de.j3ramy.economy.gui.widgets.*;
import de.j3ramy.economy.gui.widgets.Button;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class ModScreen extends Screen {
    private final List<Widget> widgets = new ArrayList<>();
    private final List<net.minecraft.client.gui.widget.Widget> mcWidgets = new ArrayList<>();
    private final Point mousePosition = new Point();
    private boolean disableScreen;


    public ModScreen() {
        super(new StringTextComponent(""));

        ModEvents.screens.add(this);
    }


    public void addWidget(Widget widget){
        this.widgets.add(widget);
    }
    public void addMcWidget(net.minecraft.client.gui.widget.Widget widget){
        this.mcWidgets.add(widget);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if(this.disableScreen)
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
                widget.update(mouseX, mouseY);
                widget.render(matrixStack);
            }
        }
    }

    private void update(int mouseX, int mouseY){
        if(this.disableScreen)
            return;

        this.mousePosition.x = mouseX;
        this.mousePosition.y = mouseY;

        if(this.isPopUpVisible())
            return;

        for(Widget widget : this.widgets){
            widget.update(mouseX, mouseY);

            if(widget instanceof Tooltip){
                Tooltip tooltip = (Tooltip) widget;

                if(tooltip.getButton() != null)
                    tooltip.setHidden(!tooltip.getButton().isMouseOver(mouseX, mouseY));
            }
        }
    }

    /*
    @throws ConcurrentModificationException if popup gets added inside a lambda expression
     */
    public void onClick(int mouseButton) {
        if (this.disableScreen)
            return;

        try {
            for (Widget widget : this.widgets) {
                if (widget != null && mouseButton == 0) {
                    if (this.isPopUpVisible() && widget instanceof AlertPopUp) {
                        ((AlertPopUp) widget).onClick();
                        return;
                    }

                    if (this.isPopUpVisible() && widget instanceof ConfirmPopUp) {
                        ((ConfirmPopUp) widget).onClick();
                        return;
                    }

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

            for (net.minecraft.client.gui.widget.Widget w : this.mcWidgets) {
                if (w != null) {
                    w.onClick(this.mousePosition.x, this.mousePosition.y);
                }
            }
        }
        catch (ConcurrentModificationException e){
            System.out.println(e + ": Ensure that your pop up windows are not added inside a lambda expression");
        }
        finally {

        }
    }

    public void onScroll(int scrollDelta){
        if(this.disableScreen)
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
        for(net.minecraft.client.gui.widget.Widget widget : this.mcWidgets){
            if(widget != null)
                widget.keyPressed(keyCode, -1, -1);
        }
    }

    public void onCharTyped(char c){
        for(net.minecraft.client.gui.widget.Widget widget : this.mcWidgets){
            if(widget != null)
                widget.keyPressed(c, -1, -1);
        }
    }

    public void clearScreen(){
        this.widgets.clear();
        this.mcWidgets.clear();
    }

    public void enable(){
        this.disableScreen = false;
    }

    public void disable(){
        this.disableScreen = true;
    }

    public boolean isPopUpVisible(){
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
}
