package de.j3ramy.edomui.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.j3ramy.edomui.interfaces.IWidget;
import de.j3ramy.edomui.utils.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;

import java.awt.*;

public class Widget implements IWidget {
    protected final int screenWidth;
    protected final int screenHeight;
    protected int width;
    protected int height;
    public int leftPos;
    public int topPos;

    protected int backgroundColor = Color.DARK_GRAY_HEX;
    protected int borderColor = Color.LIGHT_GRAY_HEX;
    protected int textColor = Color.WHITE_HEX;
    protected int borderThickness = 1;

    protected Point mousePosition = new Point();
    protected FontRenderer font;
    protected boolean isHidden;


    public Widget(int x, int y, int width, int height) {
        super();

        assert Minecraft.getInstance().currentScreen != null;
        this.screenWidth = Minecraft.getInstance().currentScreen.width;
        this.screenHeight = Minecraft.getInstance().currentScreen.height;
        this.font = Minecraft.getInstance().fontRenderer;

        this.width = width;
        this.height = height;
        this.leftPos = x;
        this.topPos = y;
    }


    public void setMousePosition(Point mousePosition) {
        this.mousePosition = mousePosition;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setBorderThickness(int borderThickness) {
        this.borderThickness = borderThickness;
    }

    public void setHidden(boolean hidden) {
        this.isHidden = hidden;
    }

    public boolean isHidden() {
        return this.isHidden;
    }

    @Override
    public void update(int x, int y) {
        this.mousePosition.x = x;
        this.mousePosition.y = y;
    }

    @Override
    public void render(MatrixStack matrixStack) {

    }

    protected void renderBackground(MatrixStack matrixStack){
        this.fillGradient(matrixStack, this.screenWidth, this.screenHeight);
    }

    protected void fillGradient(MatrixStack matrixStack, int x2, int y2) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        fillGradient(matrixStack.getLast().getMatrix(), bufferbuilder, x2, y2);
        tessellator.draw();
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    protected static void fillGradient(Matrix4f matrix, BufferBuilder builder, int x2, int y2) {
        float f = (float)(-1072689136 >> 24 & 255) / 255.0F;
        float f1 = (float)(-1072689136 >> 16 & 255) / 255.0F;
        float f2 = (float)(-1072689136 >> 8 & 255) / 255.0F;
        float f3 = (float)(-1072689136 & 255) / 255.0F;
        float f4 = (float)(-804253680 >> 24 & 255) / 255.0F;
        float f5 = (float)(-804253680 >> 16 & 255) / 255.0F;
        float f6 = (float)(-804253680 >> 8 & 255) / 255.0F;
        float f7 = (float)(-804253680 & 255) / 255.0F;
        builder.pos(matrix, (float)x2, (float) 0, (float) 0).color(f1, f2, f3, f).endVertex();
        builder.pos(matrix, (float) 0, (float) 0, (float) 0).color(f1, f2, f3, f).endVertex();
        builder.pos(matrix, (float) 0, (float)y2, (float) 0).color(f5, f6, f7, f4).endVertex();
        builder.pos(matrix, (float)x2, (float)y2, (float) 0).color(f5, f6, f7, f4).endVertex();
    }
}
