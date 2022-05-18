package de.j3ramy.edom.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.j3ramy.edom.EdomMod;
import de.j3ramy.edom.container.MailboxContainer;
import de.j3ramy.edom.tileentity.MailboxTile;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class MailboxScreen extends ContainerScreen<MailboxContainer> {

    private final ResourceLocation GUI = new ResourceLocation(EdomMod.MOD_ID, "textures/gui/mailbox_gui.png");
    MailboxTile te;

    public MailboxScreen(MailboxContainer screenContainer, PlayerInventory inv, ITextComponent title) {
        super(screenContainer, inv, title);

        this.te = (MailboxTile) screenContainer.tileEntity;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);

        if(minecraft != null)
            this.minecraft.getTextureManager().bindTexture(GUI);

        int i = this.guiLeft;
        int j = this.guiTop;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
    }
}
