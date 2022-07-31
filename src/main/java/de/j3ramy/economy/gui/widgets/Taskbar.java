package de.j3ramy.economy.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.j3ramy.economy.gui.screen.ModScreen;
import de.j3ramy.economy.utils.Color;
import de.j3ramy.economy.utils.GuiUtils;
import de.j3ramy.economy.utils.NetworkComponentUtils;
import de.j3ramy.economy.utils.Texture;
import de.j3ramy.economy.utils.data.NetworkComponentData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;

public class Taskbar extends Widget {
    public static final int TEXT_Y_OFFSET = 2;
    public int backgroundColor = Color.DARK_GRAY_HEX;
    public int textColor = Color.WHITE;
    private ImageButton osLogo;
    private ImageButton wifiButton;
    private ScrollableList wifiList;
    private ModScreen modScreen;

    public Taskbar(int x, int y, int width){
        super(x, y, width, 15, new StringTextComponent(""));

        this.width = width;

        this.modScreen = new ModScreen();

        this.modScreen.addImageButton(this.osLogo = new ImageButton(this.x + 5, this.y + 3, 9, 9, 0, 0, 10, Texture.OS_LOGO, (onClick)->{}));
        this.modScreen.addImageButton(this.wifiButton = new ImageButton(this.x + this.width - 9 - 30 - 5, this.y + 3, 9, 9, 0, 0, 10, Texture.OS_LOGO, (onClickWifi)->{}));
        this.modScreen.addList(this.wifiList = new ScrollableList(this.x + 165, this.y - 103, 100, 100, 13));
        this.wifiList.hide();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        AbstractGui.fill(matrixStack, this.x, this.y, this.x + this.width, this.y + this.height, this.backgroundColor);
        this.modScreen.render(matrixStack, mouseX, mouseY, partialTicks);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, GuiUtils.formatTime(Minecraft.getInstance().world.getDayTime()), (this.x + this.width - 30), (this.y + 4), this.textColor);
    }
    private void wifiClick() {
        if (this.wifiList.isHidden()){
            this.wifiList.show();

            this.wifiList.clear();

            ArrayList<NetworkComponentData> wifis = new ArrayList<>();
            NetworkComponentData d1 = new NetworkComponentData(new CompoundNBT());
            d1.setName("D1 Router");
            wifis.add(d1);
            wifis.add(d1);
            for (NetworkComponentData data : wifis){
                this.wifiList.addToList(data.getName(), true, Color.RED_HEX, onClick->{System.out.println(data.getName());});
            }
        }
        else {
            this.wifiList.hide();
        }
    }

    public void onClick() {
        if (this.wifiButton.isHovered()){
            wifiClick();
        }

        if (this.osLogo.isHovered()){
            System.out.println("OS Logo doesn't have function yet");
            this.osLogo.changeFocus(false);
        }
    }
}
