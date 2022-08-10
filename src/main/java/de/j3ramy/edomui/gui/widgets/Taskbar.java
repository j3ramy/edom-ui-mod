package de.j3ramy.edomui.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.j3ramy.edomui.gui.screen.CustomScreen;
import de.j3ramy.edomui.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;

public class Taskbar extends Widget {
    public final int yCenter;
    private final ImageButton wifiButton, osLogo;
    private final ScrollableList wifiList, osLogoList;
    private final CustomScreen CustomScreen;
    private final ArrayList<Button> osListElements = new ArrayList<>(), wifiListElements = new ArrayList<>();


    public Taskbar(int x, int y, int width, int height, ResourceLocation osButtonTexture, ResourceLocation wifiButtonTexture){
        super(x, y, width, height);

        this.CustomScreen = new CustomScreen();
        this.yCenter = this.height / 2;

        this.CustomScreen.addMcWidget(this.osLogo = new ImageButton(this.leftPos + 2, this.topPos + this.yCenter - 4,
                9, 9, 0, 0, 10, osButtonTexture, (onClick) -> {}));

        this.CustomScreen.addWidget(this.osLogoList = new ScrollableList(this.leftPos + 2, this.topPos - this.height - 2 - 85 + 13, 65, 85, 13));
        osLogoList.setHidden(true);

        this.CustomScreen.addMcWidget(this.wifiButton = new ImageButton(this.leftPos + this.width - 45, this.topPos + this.yCenter - 4,
                9, 9, 0, 0, 10, wifiButtonTexture, (onClickWifi)->{}));

        this.CustomScreen.addWidget(this.wifiList = new ScrollableList( this.leftPos + this.width - 45 - 85 + 9, this.topPos - this.height - 2 - 39 + 13, 85, 39, 13));
        this.wifiList.setHidden(true);
    }

    @Override
    public void render(MatrixStack matrixStack) {
        if(this.isHidden())
            return;

        super.render(matrixStack);

        AbstractGui.fill(matrixStack, this.leftPos, this.topPos, this.leftPos + this.width, this.topPos + this.height, this.backgroundColor);

        if(Minecraft.getInstance().world != null)
            Minecraft.getInstance().fontRenderer.drawString(matrixStack, GuiUtils.formatTime(Minecraft.getInstance().world.getDayTime()),
                    this.leftPos + this.width - 30, this.topPos + this.yCenter - 3, this.textColor);

        this.CustomScreen.render(matrixStack, this.mousePosition.x, this.mousePosition.y, 0);
    }

    public void onClick() {
        if(this.isHidden())
            return;

        if (this.wifiButton.isHovered()){
            if (this.wifiList.isHidden()){
                this.wifiList.setHidden(false);
                this.wifiList.clear();

                for(Button b : this.wifiListElements){
                    this.wifiList.addElement(b);
                }
            }
            else {
                this.wifiList.setHidden(true);
            }
        }

        if (this.osLogo.isHovered()){
            if (this.osLogoList.isHidden()){
                this.osLogoList.setHidden(false);
                this.osLogoList.clear();

                for(Button b : this.osListElements){
                    this.osLogoList.addElement(b);
                }
            }
            else {
                this.osLogoList.setHidden(true);
            }
        }
    }

    public void addToOsLogoList(Button button) {
        this.osLogoList.addElement(button);
    }

    public void addToWifiList(Button button) {
        this.osLogoList.addElement(button);
    }

    /*
    public void hideOsLogoList() {
        this.osLogoList.setHidden(true);
        this.osLogoList.clearSelectedIndex();
    }

     */


}
