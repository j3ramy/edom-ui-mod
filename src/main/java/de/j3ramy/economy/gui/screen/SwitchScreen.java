package de.j3ramy.economy.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.j3ramy.economy.container.SwitchContainer;
import de.j3ramy.economy.network.CSPacketSendServerData;
import de.j3ramy.economy.network.CSPacketSendSwitchData;
import de.j3ramy.economy.network.Network;
import de.j3ramy.economy.utils.Color;
import de.j3ramy.economy.utils.Texture;
import de.j3ramy.economy.utils.data.SwitchData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;

public class SwitchScreen extends ContainerScreen<SwitchContainer> {
    private final int TEXTURE_WIDTH = 256;
    private final int TEXTURE_HEIGHT = 105;
    //private TextFieldWidget ownerField;
    private int xOffset;
    private int yOffset;

    private SwitchData data = new SwitchData(new CompoundNBT());
    public void setData(SwitchData data) {
        this.data = data;
    }


    public SwitchScreen(SwitchContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }


    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);

        this.xOffset = this.width / 2 - (TEXTURE_WIDTH / 2);
        this.yOffset = this.height / 2 - 65;

    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        this.playerInventoryTitleX = 1000;

        this.update();

        for(int i = 0; i < this.data.getPortStates().length; i++){

            int xPos = this.xOffset + (i == 0 ? 33 : 92 + (39 * (i - 1)));

            switch(this.data.getPortState(i)){
                case CONNECTED_NO_INTERNET:
                    AbstractGui.fill(matrixStack, xPos, this.yOffset + 27, xPos + 2, this.yOffset + 27 + 2, Color.RED_HEX);
                    break;
                case CONNECTED:
                    AbstractGui.fill(matrixStack, xPos, this.yOffset + 27, xPos + 2, this.yOffset + 27 + 2, Color.GREEN_HEX);
                    break;
            }
        }

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);

        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(Texture.SWITCH_GUI);

        this.blit(matrixStack, this.xOffset , this.yOffset, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    private int ticks = 0;
    private void update(){
        int slotUpdateInterval = 200;
        if(this.ticks % slotUpdateInterval == 0){
            Network.INSTANCE.sendToServer(new CSPacketSendSwitchData(this.data, this.container.getTileEntity().getPos()));
            this.ticks = 0;
        }

        this.ticks++;
    }
}
