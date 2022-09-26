package de.j3ramy.edomui.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;

public final class Image extends Widget {

    private final ResourceLocation textureLoc;

    public Image(int x, int y, int width, int height, ResourceLocation textureLoc){
        super(x, y, width, height);

        this.textureLoc = textureLoc;
    }

    @Override
    public void render(MatrixStack matrixStack){
        if(this.isHidden)
            return;

        super.render(matrixStack);

        if(this.textureLoc != null){
            Minecraft.getInstance().getTextureManager().bindTexture(this.textureLoc);
            AbstractGui.blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.width, this.height, this.width, this.height);
        }
    }

    @Override
    public void update(int x, int y) {
        super.update(x, y);


    }

}

