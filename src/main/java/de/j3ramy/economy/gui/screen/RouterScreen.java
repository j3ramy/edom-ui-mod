package de.j3ramy.economy.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.j3ramy.economy.EconomyMod;
import de.j3ramy.economy.container.RouterContainer;
import de.j3ramy.economy.gui.widgets.AlertPopUp;
import de.j3ramy.economy.gui.widgets.Button;
import de.j3ramy.economy.network.CSPacketSendRouterData;
import de.j3ramy.economy.network.Network;
import de.j3ramy.economy.utils.Color;
import de.j3ramy.economy.utils.Texture;
import de.j3ramy.economy.utils.data.NetworkComponentData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class RouterScreen extends ContainerScreen<RouterContainer> {
    private final int TEXTURE_WIDTH = 162;
    private final int TEXTURE_HEIGHT = 110;
    //private TextFieldWidget ownerField;
    private int xPos;
    private int yPos;

    private final ModScreen screen;
    private TextFieldWidget nameField;
    private NetworkComponentData data;

    public void setData(NetworkComponentData data) {
        this.data = data;
    }

    public RouterScreen(RouterContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);

        this.screen = new ModScreen();
    }


    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);

        this.xPos = this.width / 2;
        this.yPos = this.height / 2;

        this.nameField = new TextFieldWidget(this.font, this.xPos - 50, this.yPos - 50, 100, 20, new StringTextComponent(""));
        this.nameField.setCanLoseFocus(true);
        this.nameField.setTextColor(Color.WHITE);
        this.nameField.setMaxStringLength(20);
        this.children.add(this.nameField);


        this.screen.addButton(new Button(this.xPos - 30, this.yPos - 20, 60, 18, new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".button.save"), (onclick)->{
            this.saveName();
        }));
    }

    private void saveName(){
        this.data.setName(this.nameField.getText().replace(' ', '_'));
        Network.INSTANCE.sendToServer(new CSPacketSendRouterData(this.data, this.container.getTileEntity().getPos()));
        this.screen.setAlertPopUp(new AlertPopUp(this, new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.title.changes_saved").getString(),
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.content.changes_saved").getString(), AlertPopUp.ColorType.DEFAULT));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        this.playerInventoryTitleX = 1000;

        drawCenteredString(matrixStack, this.font, new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".label.name", "Router"),
                (this.width / 2),
                (this.yPos - 80),
                Color.WHITE);

        this.nameField.render(matrixStack, mouseX, mouseY, partialTicks);
        this.screen.render(matrixStack, mouseX, mouseY, partialTicks);

        this.update();
    }

    private boolean isPasswordInitialSet = false;
    private void update() {
        if (!this.isPasswordInitialSet && this.data.isSet()) {
            this.nameField.setText(this.data.getName());
            this.isPasswordInitialSet = true;
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);


        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(Texture.BLANK_GUI);

        this.blit(matrixStack, this.xPos - (TEXTURE_WIDTH / 2), this.yPos, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        this.nameField.setText(this.nameField.getText());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            assert this.minecraft != null;
            assert this.minecraft.player != null;
            this.minecraft.player.closeScreen();
        }

        if(keyCode == 257){
            this.saveName();
        }
        
        return this.nameField.keyPressed(keyCode, scanCode, modifiers) || this.nameField.canWrite() || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void tick() {
        super.tick();
        this.nameField.tick();
    }


}
