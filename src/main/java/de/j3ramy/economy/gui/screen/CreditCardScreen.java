package de.j3ramy.economy.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.j3ramy.economy.container.CreditCardContainer;
import de.j3ramy.economy.utils.Color;
import de.j3ramy.economy.utils.Texture;
import de.j3ramy.economy.utils.data.BankAccountData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class CreditCardScreen extends ContainerScreen<CreditCardContainer> {
    private final int TEXTURE_WIDTH = 160;
    private final int TEXTURE_HEIGHT = 100;
    //private TextFieldWidget ownerField;
    private int xOffset;
    private int yOffset;

    private BankAccountData bankAccountData = new BankAccountData("John Doe");
    public void setCreditCardData(BankAccountData bankAccountData) {
        this.bankAccountData = bankAccountData;
    }

    public CreditCardScreen(CreditCardContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }


    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);

        this.xOffset = this.width / 2;
        this.yOffset = this.height / 2 - 75;

        //this.initOwnerTextField();
        //this.initSaveButton();
    }

    /*
    private void initOwnerTextField(){
        this.ownerField = new TextFieldWidget(this.font, this.xOffset - (TEXTURE_WIDTH / 2) + 12, this.yOffset + 65, 103, 12, new StringTextComponent(""));
        this.ownerField.setText(new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".placeholder.owner").getString());
        this.ownerField.setCanLoseFocus(true);
        this.ownerField.setTextColor(Color.WHITE);
        this.ownerField.setMaxStringLength(35);
        this.children.add(this.ownerField);
    }

     */

    /*
    private void initSaveButton(){
        this.addButton(this.saveButton = new Button( this.xOffset - 50, this.yOffset + 125, 100, 20, new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".button.save"), (onclick) ->{
            if(this.ownerField.getText().length() == 0)
                return;

            this.creditCardData = new CreditCardData(this.ownerField.getText());

            assert minecraft.player != null;
            minecraft.player.closeScreen();
        }));
    }

     */

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        this.playerInventoryTitleX = 1000;

        if(this.bankAccountData.isSet()){
            Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.bankAccountData.getOwner(), this.guiLeft + 20, this.yOffset + 66, Color.WHITE);
            Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.bankAccountData.getAccountNumber(), this.guiLeft + 20, this.yOffset + 80, Color.WHITE);
            Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.bankAccountData.getValidity(), this.guiLeft + 124, this.yOffset + 80, Color.WHITE);

            //this.saveButton.visible = false;
        }
        /*
        else{
            this.ownerField.render(matrixStack, mouseX, mouseY, partialTicks);
        }

         */

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);

        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(Texture.CC_GUI);

        this.blit(matrixStack, this.xOffset - (TEXTURE_WIDTH / 2), this.yOffset, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    //----------------- INPUT FIELD METHODS ---------------------
    /*
    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String s = this.ownerField.getText();
        this.ownerField.setText(s);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            assert this.minecraft != null;
            assert this.minecraft.player != null;
            this.minecraft.player.closeScreen();
        }

        return (this.ownerField.keyPressed(keyCode, scanCode, modifiers) || this.ownerField.canWrite()) ||
                super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void tick() {
        super.tick();
        this.ownerField.tick();
    }

     */
}
