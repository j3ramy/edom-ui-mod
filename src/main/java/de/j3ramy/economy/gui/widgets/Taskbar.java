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
    private final ImageButton wifiButton;
    private final ScrollableList wifiList;
    private final ModScreen modScreen;
    private final boolean showTime;

    public Taskbar(int x, int y, int width, int height, int backgroundColor, boolean showOsButton, boolean showTime){
        super(x, y, width, height, new StringTextComponent(""));

        this.width = width;
        this.height = height;
        this.backgroundColor = backgroundColor;
        this.showTime = showTime;

        this.modScreen = new ModScreen();

        if(showOsButton)
            this.modScreen.addImageButton(this.osLogo = new ImageButton(this.x + 5, this.y + 3, 9, 9, 0, 0, 10, Texture.OS_LOGO, (onClick)->{}));

        if(showTime){
            this.modScreen.addImageButton(this.wifiButton = new ImageButton(this.x + this.width - 44, this.y + 3, 9, 9, 0, 0, 10, Texture.OS_LOGO, (onClickWifi)->{}));
            this.modScreen.addList(this.wifiList = new ScrollableList(this.x + this.width - 35 - 85, this.y - this.height - 27, 85, 39, 13, Color.DARK_GRAY_HEX, Color.WHITE_HEX, Color.GREEN_HEX));
        }
        else{
            this.modScreen.addImageButton(this.wifiButton = new ImageButton(this.x + this.width - 13, this.y + 3, 9, 9, 0, 0, 10, Texture.OS_LOGO, (onClickWifi)->{}));
            this.modScreen.addList(this.wifiList = new ScrollableList(this.x + this.width - 4 - 85, this.y - this.height - 27, 85, 39, 13, Color.DARK_GRAY_HEX, Color.WHITE_HEX, Color.GREEN_HEX));
        }

        this.wifiList.hide();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        AbstractGui.fill(matrixStack, this.x, this.y, this.x + this.width, this.y + this.height, this.backgroundColor);

        if(this.showTime)
            Minecraft.getInstance().fontRenderer.drawString(matrixStack, GuiUtils.formatTime(Minecraft.getInstance().world.getDayTime()), (this.x + this.width - 30), (this.y + 5), this.textColor);

        this.modScreen.render(matrixStack, mouseX, mouseY, partialTicks);
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
                this.wifiList.addToList(data.getName(), true, onClick->{System.out.println(data.getName());});
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

        if (this.osLogo != null && this.osLogo.isHovered()){
            System.out.println("OS Logo doesn't have function yet");
            this.osLogo.changeFocus(false);
        }
    }
}
