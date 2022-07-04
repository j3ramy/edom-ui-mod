package de.j3ramy.economy.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.j3ramy.economy.container.MoneyChangerContainer;
import de.j3ramy.economy.tileentity.MoneyChangerTile;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class MoneyChangerScreen extends ContainerScreen<MoneyChangerContainer> {

    private final ResourceLocation GUI = new ResourceLocation(de.j3ramy.economy.EconomyMod.MOD_ID, "textures/gui/money_changer_gui.png");
    MoneyChangerTile te;

    public MoneyChangerScreen(MoneyChangerContainer screenContainer, PlayerInventory inv, ITextComponent title) {
        super(screenContainer, inv, title);

        this.te = (MoneyChangerTile) screenContainer.tileEntity;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);

        this.playerInventoryTitleX = 1000;
        this.titleX = 1000;

        //this.addButton(new Button(240, 104, 60, 18, new StringTextComponent("Wechseln"), (onclick) -> {
        //    te.changeMoney();
        //}));
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
