package de.j3ramy.edomui.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.j3ramy.edomui.EdomUiMod;
import de.j3ramy.edomui.gui.screen.CustomScreen;
import de.j3ramy.edomui.gui.widgets.TextField;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class DebugScreen extends ContainerScreen<DebugContainer> {
    public DebugScreen(DebugContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);

        CustomScreen.screen = new CustomScreen();
    }

    @Override
    protected void init() {
        super.init();

        CustomScreen.screen.addWidget(new TextField(10, 10, 70, 14, "Search...", new ResourceLocation(EdomUiMod.MOD_ID, "textures/gui/widgets/checkbox_check.png")));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {

    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        CustomScreen.screen.render(matrixStack, mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            if(this.minecraft != null && this.minecraft.player != null)
                this.minecraft.player.closeScreen();
        }

        return true;
    }

    @Override
    public void onClose() {
        super.onClose();

        CustomScreen.screen = null;
    }
}
