package de.j3ramy.economy.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.j3ramy.economy.utils.Color;
import de.j3ramy.economy.utils.GuiUtils;
import de.j3ramy.economy.utils.Texture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.text.StringTextComponent;

public class Taskbar extends Widget {
    public static final int TEXT_Y_OFFSET = 2;
    public int backgroundColor = Color.DARK_GRAY_HEX;
    public int textColor = Color.WHITE;
    private ImageButton osLogo;

    public Taskbar(int x, int y, int width){
        super(x, y, width, 15, new StringTextComponent(""));

        this.osLogo = new ImageButton(this.x + 5, this.y + 3, 9, 9, 0, 0, 10, Texture.OS_LOGO, (onClick)->{System.out.println("E");});
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        AbstractGui.fill(matrixStack, this.x, this.y, this.x + this.width, this.y + this.height, backgroundColor);
        osLogo.render(matrixStack, mouseX, mouseY, partialTicks);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, GuiUtils.formatTime(Minecraft.getInstance().world.getDayTime()), (this.x + this.width - 20), (this.y + 5), this.textColor);
    }
}
