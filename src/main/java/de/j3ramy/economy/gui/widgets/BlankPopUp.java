package de.j3ramy.economy.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.j3ramy.economy.EconomyMod;
import de.j3ramy.economy.utils.Color;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;

public class BlankPopUp extends Screen {
    private static final int BUTTON_WIDTH = 60;
    private static final int BUTTON_HEIGHT = 14;

    private final int width;
    private final int height;
    private final Point mousePosition;
    private final int leftPos;
    private final int topPos;
    private final ContainerScreen<?> screen;
    private final Button submitButton;
    private final Button cancelButton;

    private final String title;
    private boolean isHidden;

    public BlankPopUp(ContainerScreen<?> screen, int width, int height, ITextComponent title, ITextComponent submitButtonLabel, net.minecraft.client.gui.widget.button.Button.IPressable submitAction){
        super(title);

        this.screen = screen;
        this.mousePosition = new Point();

        this.width = width;
        this.height = height;
        this.title = title.getString();

        this.leftPos = screen.width / 2 - this.width / 2;
        this.topPos = screen.height / 2 - this.height / 2;

        this.submitButton = new Button(this.leftPos + 20, topPos + this.height - BUTTON_HEIGHT - 20, BUTTON_WIDTH, BUTTON_HEIGHT,
                submitButtonLabel, submitAction);

        this.cancelButton = new Button(this.leftPos + this.width - BUTTON_WIDTH - 20, topPos + this.height - BUTTON_HEIGHT - 20, BUTTON_WIDTH, BUTTON_HEIGHT,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".button.cancel"), (click) -> this.hide());
    }

    public boolean isHidden() {
        return this.isHidden;
    }

    public void updateMousePosition(int x, int y){

        this.mousePosition.x = x;
        this.mousePosition.y = y;

        this.submitButton.updateMousePosition(x, y);
        this.cancelButton.updateMousePosition(x, y);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
        if(this.isHidden)
            return;

        screen.renderBackground(matrixStack);

        //border
        int DEFAULT_COLOR = Color.DARK_GRAY_HEX;
        AbstractGui.fill(matrixStack, leftPos, topPos, leftPos + this.width, topPos + this.height, DEFAULT_COLOR);


        //background
        int CONTENT_MARGIN = 5;
        int BACKGROUND_COLOR = Color.LIGHT_GRAY_HEX;
        AbstractGui.fill(matrixStack, leftPos + CONTENT_MARGIN, topPos + CONTENT_MARGIN + 10,
                leftPos + this.width - CONTENT_MARGIN, topPos + CONTENT_MARGIN + this.height - 10,
                BACKGROUND_COLOR);

        //title text
        AbstractGui.drawCenteredString(matrixStack, screen.getMinecraft().fontRenderer, this.title, screen.width / 2, topPos + 4, Color.WHITE);

        this.submitButton.render(matrixStack, mouseX, mouseY, partialTicks);
        this.cancelButton.render(matrixStack, mouseX, mouseY, partialTicks);

        this.tick();
    }

    public void onClick(){
        this.submitButton.onClick();
        this.cancelButton.onClick();
    }

    public void hide(){
        this.isHidden = true;
    }
    public void show(){
        this.isHidden = false;
    }
}

