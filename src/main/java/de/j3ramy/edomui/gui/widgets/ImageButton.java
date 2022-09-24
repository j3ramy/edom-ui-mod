package de.j3ramy.edomui.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;

public final class ImageButton extends Button {

    private final ResourceLocation textureLoc;
    private final int uOffset, vOffset, textureWidth, textureHeight;
    public boolean isRenderDefaultBackground = true;

    public ImageButton(int x, int y, int width, int height, int uOffset, int vOffset, int textureWidth, int textureHeight, ResourceLocation textureLoc, Button.IClickable action) {
        super(x, y, width, height, "", action);

        this.uOffset = uOffset;
        this.vOffset = vOffset;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.textureLoc = textureLoc;
    }

    public ImageButton(int x, int y, int width, int height, ResourceLocation textureLoc, Button.IClickable action) {
        this(x, y, width, height, 0, 0, width, height, textureLoc, action);
    }

    @Override
    public void render(MatrixStack matrixStack) {
        if(this.isRenderDefaultBackground){
            super.render(matrixStack);
        }

        Minecraft.getInstance().getTextureManager().bindTexture(this.textureLoc);
        AbstractGui.blit(matrixStack, this.leftPos, this.topPos, this.uOffset, this.vOffset, this.width, this.height, this.textureWidth, this.textureHeight);
    }

}
