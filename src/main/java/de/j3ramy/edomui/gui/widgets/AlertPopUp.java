package de.j3ramy.edomui.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.j3ramy.edomui.utils.enums.PopUpColor;

public final class AlertPopUp extends PopUp {

    private final Button closeButton;

    public AlertPopUp(int x, int y, int width, int height, String title, String content, String closeButtonContent, PopUpColor type){
        super(x, y, width, height, title, content, type);


        int buttonHeight = 14;
        int buttonWidth = 60;
        this.closeButton = new Button((this.leftPos + this.width / 2) - buttonWidth / 2, this.topPos + this.height - buttonHeight - 20,
                buttonWidth, buttonHeight, closeButtonContent, ()->this.isHidden = true);
    }

    @Override
    public void render(MatrixStack matrixStack){
        if(this.isHidden)
            return;

        super.render(matrixStack);

        //button
        closeButton.render(matrixStack);
    }

    @Override
    public void update(int x, int y) {
        super.update(x, y);

        this.closeButton.update(x, y);
    }

    @Override
    public void onClick(){
        super.onClick();

        this.closeButton.onClick();
    }

}

