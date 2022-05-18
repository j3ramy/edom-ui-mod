package de.j3ramy.edom.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.j3ramy.edom.EdomMod;
import de.j3ramy.edom.container.TimeTrackingContainer;
import de.j3ramy.edom.tileentity.TimeTrackingTile;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class TimeTrackingScreen extends ContainerScreen<TimeTrackingContainer> {

    private final ResourceLocation GUI = new ResourceLocation(EdomMod.MOD_ID, "textures/gui/time_tracking_terminal_gui.png");
    TimeTrackingTile te;

    public TimeTrackingScreen(TimeTrackingContainer screenContainer, PlayerInventory inv, ITextComponent title) {
        super(screenContainer, inv, title);

        this.te = (TimeTrackingTile) screenContainer.getTileEntity();
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
