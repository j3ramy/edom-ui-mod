package de.j3ramy.edomui.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.j3ramy.edomui.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.ImageButton;

import javax.annotation.Nullable;

public final class Tooltip extends Widget{

    private final String content;
    private final ImageButton imageButton;

    public Tooltip(String content, ImageButton button){
        super(0, 0, 0, 0);
        this.content = content;
        this.imageButton = button;
    }

    @Nullable
    public ImageButton getButton() {
        return this.imageButton;
    }

    public void render(MatrixStack matrixStack){
        if(!this.isHidden() && !this.content.isEmpty()){
            super.render(matrixStack);

            //border
            int textMargin = 2;
            int xOffset = 2 + textMargin;
            int yOffset = -8 - textMargin;

            int height = GuiUtils.LETTER_HEIGHT + textMargin * 2;

            //border
            AbstractGui.fill(matrixStack,
                    this.mousePosition.x + xOffset - this.borderThickness - textMargin,
                    this.mousePosition.y + yOffset - this.borderThickness - textMargin,
                    this.mousePosition.x + xOffset + font.getStringWidth(this.content) + this.borderThickness + textMargin + 1,
                    this.mousePosition.y + yOffset + height + this.borderThickness,
                    this.borderColor);

            //background
            AbstractGui.fill(matrixStack,
                    this.mousePosition.x + xOffset - textMargin,
                    this.mousePosition.y + yOffset - textMargin,
                    this.mousePosition.x + xOffset + font.getStringWidth(this.content) + textMargin + 1,
                    this.mousePosition.y + yOffset + height,
                    this.backgroundColor);

            //text
            Minecraft.getInstance().fontRenderer.drawString(
                    matrixStack, this.content,
                    this.mousePosition.x + xOffset + textMargin / 2f,
                    this.mousePosition.y + yOffset + textMargin / 2f,
                    this.textColor);
        }
    }

    @Override
    public void update(int x, int y) {
        super.update(x, y);

        if(this.imageButton != null){
            this.setHidden(!this.getButton().isMouseOver(this.mousePosition.x, this.mousePosition.y));
        }
    }
}
