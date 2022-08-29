
package de.j3ramy.edomui.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.j3ramy.edomui.utils.enums.PopUpColor;

public final class ConfirmPopUp extends PopUp {
    private final Button noButton, yesButton;

    public ConfirmPopUp(int x, int y, int width, int height, String title, String content, String yesButtonContent, String noButtonContent, PopUpColor type, Button.IClickable confirmAction){
        super(x, y, width, height, title, content, type);

        int buttonHeight = 14;
        int buttonWidth = 40;
        this.yesButton = new Button(this.leftPos + 20, this.topPos + this.height - buttonHeight - 20, buttonWidth, buttonHeight,
                yesButtonContent, confirmAction);

        this.noButton = new Button(this.leftPos + this.width - buttonWidth - 20, this.topPos + this.height - buttonHeight - 20, buttonWidth, buttonHeight,
                noButtonContent, ()->this.setHidden(true));
    }

    public void render(MatrixStack matrixStack){
        if(this.isHidden)
            return;

        super.render(matrixStack);

        //buttons
        yesButton.render(matrixStack);
        noButton.render(matrixStack);
    }

    @Override
    public void update(int x, int y) {
        super.update(x, y);

        this.yesButton.update(x, y);
        this.noButton.update(x, y);
    }

    @Override
    public void onClick(){
        if(this.isHidden())
            return;

        this.yesButton.onClick();
        this.noButton.onClick();
    }
}

