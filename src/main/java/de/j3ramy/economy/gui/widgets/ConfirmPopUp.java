/*
* UPDATE:
* - Custom screen parent class
* */

package de.j3ramy.economy.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.j3ramy.economy.EconomyMod;
import de.j3ramy.economy.utils.Color;
import de.j3ramy.economy.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;

public class ConfirmPopUp extends Screen {

    public final int WIDTH = 150;
    public final int HEIGHT = 90;
    public final int BUTTON_WIDTH = 40;
    public final int BUTTON_HEIGHT = 14;

    public enum ColorType{
        DEFAULT,
        NOTICE,
        ERROR
    }

    public enum PopUpType{
        CONFIRM,
        ALERT
    }

    private final Point mousePosition;
    private final int leftPos;
    private final int topPos;
    private final ContainerScreen<?> screen;
    private final Button noButton;
    private final Button yesButton;

    private PopUpType type = PopUpType.ALERT;
    private ColorType colorType = ColorType.DEFAULT;
    private String title;
    private String content;
    private boolean isClosed;

    public ConfirmPopUp(ContainerScreen<?> screen, net.minecraft.client.gui.widget.button.Button.IPressable confirmAction){
        super(new StringTextComponent(""));

        this.screen = screen;
        this.mousePosition = new Point();
        this.isClosed = true;

        this.leftPos = screen.width / 2 - WIDTH / 2;
        this.topPos = screen.height / 2 - HEIGHT / 2;


        this.yesButton = new Button(this.leftPos + 20, topPos + 65, BUTTON_WIDTH, BUTTON_HEIGHT,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".button.yes"), confirmAction);

        this.noButton = new Button(this.leftPos + WIDTH - BUTTON_WIDTH - 20, topPos + 65, BUTTON_WIDTH, BUTTON_HEIGHT,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".button.no"), (click) -> {
            this.hide();
        });
    }

    public void setColorType(ColorType color) {
        this.colorType = color;
    }

    public void setType(PopUpType type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isHidden() {
        return this.isClosed;
    }

    public void updateMousePosition(int x, int y){

        this.mousePosition.x = x;
        this.mousePosition.y = y;

        this.yesButton.updateMousePosition(x, y);
        this.noButton.updateMousePosition(x, y);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
        if(this.isClosed)
            return;

        screen.renderBackground(matrixStack);

        //border
        int DEFAULT_COLOR = Color.DARK_GRAY_HEX;
        int NOTICE_COLOR = Color.ORANGE_HEX;
        int ERROR_COLOR = Color.RED_HEX;
        switch (this.colorType){
            case DEFAULT: AbstractGui.fill(matrixStack, leftPos, topPos, leftPos + WIDTH, topPos + HEIGHT, DEFAULT_COLOR); break;
            case NOTICE: AbstractGui.fill(matrixStack, leftPos, topPos, leftPos + WIDTH, topPos + HEIGHT, NOTICE_COLOR); break;
            case ERROR: AbstractGui.fill(matrixStack, leftPos, topPos, leftPos + WIDTH, topPos + HEIGHT, ERROR_COLOR);
        }

        //background
        int CONTENT_MARGIN = 5;
        int BACKGROUND_COLOR = Color.LIGHT_GRAY_HEX;
        AbstractGui.fill(matrixStack, leftPos + CONTENT_MARGIN, topPos + CONTENT_MARGIN + 10,
                leftPos + WIDTH - CONTENT_MARGIN, topPos + CONTENT_MARGIN + HEIGHT - 10,
                BACKGROUND_COLOR);

        //title text
        AbstractGui.drawCenteredString(matrixStack, screen.getMinecraft().fontRenderer, this.title, screen.width / 2, topPos + 4, Color.WHITE);

        //content text
        GlStateManager.pushMatrix();
        GlStateManager.scalef(.5f, .5f, .5f);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.content,
                (screen.width / 2f) * 2 - GuiUtils.getCenteredTextOffset(this.content.length()),
                (topPos + 37) * 2,
                Color.WHITE);
        GlStateManager.popMatrix();


        //button
        yesButton.render(matrixStack, mouseX, mouseY, partialTicks);
        noButton.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public void onClick(){
        this.yesButton.onClick();
        this.noButton.onClick();

    }

    public void hide(){
        this.isClosed = true;
    }

    public void show(){
        this.isClosed = false;
    }


}
