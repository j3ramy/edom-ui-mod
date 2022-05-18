package de.j3ramy.edom.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.j3ramy.edom.EdomMod;
import de.j3ramy.edom.container.FireHoseBoxContainer;
import de.j3ramy.edom.tileentity.FireHoseBoxTile;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class FireHoseBoxScreen extends ContainerScreen<FireHoseBoxContainer> {

    private final ResourceLocation GUI = new ResourceLocation(EdomMod.MOD_ID, "textures/gui/fire_hose_box_gui.png");
    FireHoseBoxContainer container;

    public FireHoseBoxScreen(FireHoseBoxContainer screenContainer, PlayerInventory inv, ITextComponent title) {
        super(screenContainer, inv, title);

        this.container = screenContainer;
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

        FireHoseBoxTile te = (FireHoseBoxTile) container.getTileEntity();

        int yPos = j + 26 + te.getData().get(0); //get water level with sync array (in tile entity)
        this.blit(matrixStack, i + 156, yPos, 176, 0, 12, 1);

        //this.addButton(new Button(0, 0, 100, 20, new StringTextComponent("Test"), (button) -> te.getData().set(0, 0)));
    }
}
