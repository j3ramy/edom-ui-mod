package de.j3ramy.edomui.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.j3ramy.edomui.EdomUiMod;
import de.j3ramy.edomui.gui.screen.CustomScreen;
import de.j3ramy.edomui.utils.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;

public final class Checkbox extends Button {
    private boolean isChecked;

    public Checkbox(int x, int y, int width, int height, String title) {
        super(x, y, width, height, title, ()->{});

        this.clickAction = () -> this.setChecked(!this.isChecked());
        this.setIsCheckbox(true);

        this.setTextColor(Color.DARK_GRAY);
    }

    public void setChecked(boolean checked) {
        this.isChecked = checked;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void render(MatrixStack matrixStack) {
        super.render(matrixStack);

        if(this.enabled){
            if(this.isMouseOver()){
                AbstractGui.fill(matrixStack, this.leftPos, this.topPos, this.leftPos + this.width, this.topPos + this.height, this.hoverBackgroundColor);

                GlStateManager.pushMatrix();
                GlStateManager.scalef(.5f, .5f, .5f);
                Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.title, (this.leftPos + this.width + 5) * 2, (this.topPos + this.height / 3f) * 2, this.textColor);
                GlStateManager.popMatrix();
            }
            else{
                AbstractGui.fill(matrixStack, this.leftPos, this.topPos, this.leftPos + this.width, this.topPos + this.height, this.backgroundColor);

                GlStateManager.pushMatrix();
                GlStateManager.scalef(.5f, .5f, .5f);
                Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.title, (this.leftPos + this.width + 5) * 2, (this.topPos + this.height / 3f) * 2, this.textColor);
                GlStateManager.popMatrix();
            }
        }
        else{
            AbstractGui.fill(matrixStack, this.leftPos, this.topPos, this.leftPos + this.width, this.topPos + this.height, this.disabledBackgroundColor);

            GlStateManager.pushMatrix();
            GlStateManager.scalef(.5f, .5f, .5f);
            Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.title, (this.leftPos + this.width + 5) * 2, (this.topPos + this.height / 3f) * 2, this.disabledTextColor);
            GlStateManager.popMatrix();
        }

        if(this.isChecked()){
            Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation(EdomUiMod.MOD_ID, "textures/gui/widgets/checkbox_check.png"));
            AbstractGui.blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.width, this.height, this.width, this.height);
        }
    }

}
