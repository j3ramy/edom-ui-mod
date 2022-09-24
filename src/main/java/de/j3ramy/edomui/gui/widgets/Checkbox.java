package de.j3ramy.edomui.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.j3ramy.edomui.EdomUiMod;
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
        super.render(matrixStack);

        if(this.isChecked){
            Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation(EdomUiMod.MOD_ID, "textures/gui/widgets/checkbox_check.png"));
            AbstractGui.blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.width, this.height, this.width, this.height);
        }
    }

}
