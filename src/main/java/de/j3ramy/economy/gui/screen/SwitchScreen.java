package de.j3ramy.economy.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.j3ramy.economy.EconomyMod;
import de.j3ramy.economy.container.SwitchContainer;
import de.j3ramy.economy.gui.widgets.Button;
import de.j3ramy.economy.network.CSPacketSendServerData;
import de.j3ramy.economy.network.CSPacketSendSwitchData;
import de.j3ramy.economy.network.Network;
import de.j3ramy.economy.utils.Color;
import de.j3ramy.economy.utils.Math;
import de.j3ramy.economy.utils.Texture;
import de.j3ramy.economy.utils.data.SwitchData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class SwitchScreen extends ContainerScreen<SwitchContainer> {
    private final int TEXTURE_WIDTH = 256;
    private final int TEXTURE_HEIGHT = 169;
    //private TextFieldWidget ownerField;
    private int xPos;
    private int yPos;

    private SwitchData data = new SwitchData(new CompoundNBT());
    public void setData(SwitchData data) {
        this.data = data;
    }


    ModScreen screen;
    public SwitchScreen(SwitchContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);

        this.screen = new ModScreen();
    }


    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);

        this.screen.clearScreen();

        this.xPos = this.width / 2 - (TEXTURE_WIDTH / 2);
        this.yPos = this.height / 2 - 80;
    }

    private Button onButton, offButton;
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        this.playerInventoryTitleX = 1000;

        this.screen.addWidget(this.onButton = new Button(this.xPos + 7, this.yPos + 80, 35, 16,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".button.on").getString(), ()->{
            this.data.setOn(true);

            Network.INSTANCE.sendToServer(new CSPacketSendSwitchData(this.data, this.container.getTileEntity().getPos()));
        }));

        this.screen.addWidget(this.offButton = new Button(this.xPos + 7, this.yPos + 80 + 18 + 3, 35, 16,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".button.off").getString(), ()->{
            this.data.setOn(false);

            Network.INSTANCE.sendToServer(new CSPacketSendSwitchData(this.data, this.container.getTileEntity().getPos()));
        }));

        GlStateManager.pushMatrix();
        GlStateManager.scalef(.5f, .5f, .5f);

        drawCenteredString(matrixStack, this.font, "Input", (this.xPos + 25) * 2, (this.yPos + 7) * 2, Color.YELLOW);
        drawCenteredString(matrixStack, this.font, "Output", (this.xPos + 150) * 2, (this.yPos + 7) * 2, Color.YELLOW);

        GlStateManager.popMatrix();

        if(data.isOn()){
            this.drawInputSlot(matrixStack);
            this.drawOutputSlots(matrixStack);
        }

        this.update();
        this.screen.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void drawInputSlot(MatrixStack matrixStack){
        int xPos = this.xPos + 30;
        int yPos = this. yPos;

        switch(this.data.getPortState(0)){
            case CONNECTED_NO_INTERNET:
                AbstractGui.fill(matrixStack, xPos, yPos + 27, xPos + 2, yPos + 27 + 2, Color.RED_HEX);
                break;
            case CONNECTED:
                AbstractGui.fill(matrixStack, xPos, yPos + 27, xPos + 2, yPos + 27 + 2, Color.GREEN_HEX);
                break;
        }

        GlStateManager.pushMatrix();
        GlStateManager.scalef(.5f, .5f, .5f);

        BlockPos pos = this.data.getPort(0).getFrom();
        if(!Math.areBlockPosEqual(pos, BlockPos.ZERO)){
            //show component name
            drawCenteredString(matrixStack, this.font, this.data.getPort(0).getName(), (xPos - 5) * 2, (yPos + 17) * 2, Color.WHITE);
            //show component type
            drawCenteredString(matrixStack, this.font, this.data.getPort(0).getComponent().name(), (xPos - 5) * 2, (yPos + 47) * 2, Color.WHITE);
            //show pos
            drawCenteredString(matrixStack, this.font, " [" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]", (xPos - 5) * 2, (yPos + 55) * 2, Color.WHITE);
        }

        GlStateManager.popMatrix();
    }

    private void drawOutputSlots(MatrixStack matrixStack){
        int rows = 2;
        int columns = 5;
        int counter = 1;
        for(int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int xPos = this.xPos + 30 + 47 + 40 * j;
                int yPos = this.yPos + 65 * i;

                switch (this.data.getPortState(counter)) {
                    case CONNECTED_NO_INTERNET:
                        AbstractGui.fill(matrixStack, xPos, yPos + 27, xPos + 2, yPos + 27 + 2, Color.RED_HEX);
                        break;
                    case CONNECTED:
                        AbstractGui.fill(matrixStack, xPos, yPos + 27, xPos + 2, yPos + 27 + 2, Color.GREEN_HEX);
                        break;
                }

                GlStateManager.pushMatrix();
                GlStateManager.scalef(.5f, .5f, .5f);

                BlockPos pos = this.data.getPort(counter).getFrom();
                if (!Math.areBlockPosEqual(pos, BlockPos.ZERO)) {
                    //show component name
                    drawCenteredString(matrixStack, this.font, this.data.getPort(counter).getName(), (xPos - 5) * 2, (yPos + 17) * 2, Color.WHITE);
                    //show component type
                    drawCenteredString(matrixStack, this.font, this.data.getPort(counter).getComponent().name(), (xPos - 5) * 2, (yPos + 47) * 2, Color.WHITE);
                    //show pos
                    drawCenteredString(matrixStack, this.font, " [" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]", (xPos - 5) * 2, (yPos + 55) * 2, Color.WHITE);
                }

                GlStateManager.popMatrix();

                counter++;
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);

        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(Texture.SWITCH_GUI);

        this.blit(matrixStack, this.xPos , this.yPos, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    private int ticks = 0;
    private void update(){
        int slotUpdateInterval = 200;
        if(this.ticks % slotUpdateInterval == 0){
            Network.INSTANCE.sendToServer(new CSPacketSendSwitchData(this.data, this.container.getTileEntity().getPos()));
            this.ticks = 0;
        }

        this.ticks++;


        if(!this.data.isOn()){
            this.onButton.setEnabled(true);
            this.offButton.setEnabled(false);
        }
        else{
            this.onButton.setEnabled(false);
            this.offButton.setEnabled(true);
        }
    }
}
