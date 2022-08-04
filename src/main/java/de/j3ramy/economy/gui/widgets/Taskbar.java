package de.j3ramy.economy.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.j3ramy.economy.gui.screen.ModScreen;
import de.j3ramy.economy.utils.Color;
import de.j3ramy.economy.utils.GuiUtils;
import de.j3ramy.economy.utils.Texture;
import de.j3ramy.economy.utils.data.NetworkComponentData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.nbt.CompoundNBT;

import java.util.ArrayList;

public class Taskbar extends Widget {
    public final float yCenter;
    private final ImageButton wifiButton;
    private final ScrollableList wifiList;
    private final ModScreen modScreen;
    private final boolean showTime;
    private ScrollableList osLogoList;
    private ImageButton osLogo;


    public Taskbar(int x, int y, int width, int height, boolean showOsButton, boolean showTime){
        super(x, y, width, height);

        this.showTime = showTime;
        this.modScreen = new ModScreen();

        this.yCenter = (float) (this.height) / 2;
        this.wifiButton = new ImageButton(0, this.topPos + (int)(this.yCenter - 4.5), 9, 9, 0, 0, 10, Texture.WIFI_BUTTON, (onClickWifi)->{});
        this.wifiList = new ScrollableList(0, this.topPos - this.height - 27, 85, 39, 13, Color.DARK_GRAY_HEX, Color.WHITE_HEX, Color.PURPLE_HEX);

        if(showOsButton) {
            this.modScreen.addImageButton(this.osLogo = new ImageButton(this.leftPos + 5, this.topPos + (int) (this.yCenter - 4.5), 9, 9, 0, 0, 10, Texture.OS_LOGO, (onClick) -> {
            }));
            this.modScreen.addList(this.osLogoList = new ScrollableList(this.leftPos + 2, this.topPos - 85 - 2, 65, 85, 13, Color.DARK_GRAY_HEX, Color.WHITE_HEX, Color.PURPLE_HEX));
            osLogoList.hide();
        }

        if(showTime){
            this.wifiButton.x = this.leftPos + this.width - 9 - 27 - 2;
            this.modScreen.addImageButton(this.wifiButton);
            this.wifiList.x = this.leftPos + this.width - 2 - 27 - 85;
            this.modScreen.addList(this.wifiList);
        }
        else{
            this.wifiButton.x = this.leftPos + this.width - 9 - 2;
            this.modScreen.addImageButton(this.wifiButton);
            this.wifiList.x = this.leftPos + this.width - 2 - 85;
            this.modScreen.addList(this.wifiList);
        }

        this.wifiList.hide();
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack);

        AbstractGui.fill(matrixStack, this.leftPos, this.topPos, this.leftPos + this.width, this.topPos + this.height, this.backgroundColor);

        if(this.showTime) {
            assert Minecraft.getInstance().world != null;
            Minecraft.getInstance().fontRenderer.drawString(matrixStack, GuiUtils.formatTime(Minecraft.getInstance().world.getDayTime()),
                    this.leftPos + this.width - 27, this.topPos + (int)(this.yCenter - 3.5), this.textColor);
        }

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
                this.wifiList.addToList(data.getName(), true, onClick-> System.out.println(data.getName()));
            }
        }
        else {
            this.wifiList.hide();
        }
    }

    private void osLogoClick() {
        if (this.osLogoList.isHidden()){
            this.osLogoList.show();
        }
        else {
            this.osLogoList.hide();
        }
    }

    public void onClick() {
        if (this.wifiButton.isHovered()){
            this.wifiClick();
        }

        if (this.osLogo != null && this.osLogo.isHovered()){
            this.osLogoClick();
        }
    }

    public void addToOsLogoList(String content, Button.IPressable onClick) {
        this.osLogoList.addToList(content, true, onClick);
    }

    public void hideOsLogoList() {
        this.osLogoList.hide();
        this.osLogoList.clearSelectedIndex();
    }
}
