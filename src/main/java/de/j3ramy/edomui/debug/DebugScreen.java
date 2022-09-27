package de.j3ramy.edomui.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.j3ramy.edomui.enums.TextFieldType;
import de.j3ramy.edomui.gui.screen.CustomScreen;
import de.j3ramy.edomui.gui.widgets.Checkbox;
import de.j3ramy.edomui.gui.widgets.TextField;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class DebugScreen extends ContainerScreen<DebugContainer> {
    public DebugScreen(DebugContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);

        CustomScreen.screen = new CustomScreen();
    }

    @Override
    protected void init() {
        super.init();

        TextField textField;
        CustomScreen.screen.addWidget(textField = new TextField(10, 10, 70, 14, "Password", TextFieldType.TEXT));
        textField.setText("Test1234567890ABCDEF");
        CustomScreen.screen.addWidget(new Checkbox(10, 30, 10, 10, "Test"));
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
