package de.j3ramy.economy.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.j3ramy.economy.utils.Color;
import de.j3ramy.economy.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.ImageButton;

import javax.annotation.Nullable;

public class Tooltip extends Widget{

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
        if(!this.isHidden() && this.content != null && !this.content.isEmpty()){
            //border
            AbstractGui.fill(matrixStack,
                    this.mousePosition.x - 3 + 5,
                    this.mousePosition.y - 3 - 7,
                    this.mousePosition.x + GuiUtils.getCenteredTextOffset(this.content.length()) + 3 + 5,
                    this.mousePosition.y + GuiUtils.LETTER_SIZE + 4 - 8,
                    this.borderColor);

            //background
            AbstractGui.fill(matrixStack,
                    this.mousePosition.x - 2 + 5,
                    this.mousePosition.y - 2 - 7,
                    this.mousePosition.x + GuiUtils.getCenteredTextOffset(this.content.length()) + 2 + 5,
                    this.mousePosition.y + GuiUtils.LETTER_SIZE + 3 - 8,
                    this.backgroundColor);

            GlStateManager.pushMatrix();
            GlStateManager.scalef(.5f, .5f, .5f);
            Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.content, (this.mousePosition.x + 1 + 5) * 2, (this.mousePosition.y - 6) * 2, this.textColor);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void update(int x, int y) {
        super.update(x, y);

        if(this.getButton() != null){
            this.setHidden(!this.getButton().isMouseOver(this.mousePosition.x, this.mousePosition.y));
        }
    }
}
