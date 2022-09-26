package de.j3ramy.edomui.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.j3ramy.edomui.Main;
import de.j3ramy.edomui.utils.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;

public final class Checkbox extends Button {
    public boolean isChecked;

    public Checkbox(int x, int y, int width, int height, String title) {
        super(x, y, width, height, title, ()->{});

        this.clickAction = () -> this.isChecked = !this.isChecked;
        this.isCheckbox = true;

        this.textColor = Color.DARK_GRAY;
    }

    @Override
    public void render(MatrixStack matrixStack) {
        if(this.isHidden)
            return;

        super.render(matrixStack);

        GlStateManager.pushMatrix();
        GlStateManager.scalef(.5f, .5f, .5f);
        if(this.isEnabled){
            Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.title, (this.leftPos + this.width + 5) * 2, (this.topPos + this.height / 3f) * 2, this.textColor);
        }
        else{
            Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.title, (this.leftPos + this.width + 5) * 2, (this.topPos + this.height / 3f) * 2, this.disabledTextColor);
        }
        GlStateManager.popMatrix();

        if(this.isChecked){
            Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation(Main.MOD_ID, "textures/gui/icons/check.png"));
            AbstractGui.blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.width, this.height, this.width, this.height);
        }
    }
}
