package de.j3ramy.economy.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.j3ramy.economy.container.NetworkSocketContainer;
import de.j3ramy.economy.container.SwitchContainer;
import de.j3ramy.economy.utils.Texture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class NetworkSocketScreen extends ContainerScreen<NetworkSocketContainer> {
    private final int TEXTURE_WIDTH = 162;
    private final int TEXTURE_HEIGHT = 110;
    //private TextFieldWidget ownerField;
    private int xOffset;
    private int yOffset;

    public NetworkSocketScreen(NetworkSocketContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }


    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);

        this.xOffset = this.width / 2;
        this.yOffset = this.height / 2 - 65;

    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        this.playerInventoryTitleX = 1000;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);

        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(Texture.NETWORK_SOCKET_GUI);

        this.blit(matrixStack, this.xOffset - (TEXTURE_WIDTH / 2), this.yOffset, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }


}
