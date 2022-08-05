package de.j3ramy.economy.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.j3ramy.economy.EconomyMod;
import de.j3ramy.economy.container.CreditCardPrinterContainer;
import de.j3ramy.economy.network.CSPacketSendBankAccountData;
import de.j3ramy.economy.network.Network;
import de.j3ramy.economy.utils.Color;
import de.j3ramy.economy.utils.Texture;
import de.j3ramy.economy.utils.data.BankAccountData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;

public class CreditCartPrinterScreen extends ContainerScreen<CreditCardPrinterContainer> {

    public static final int MIN_OWNER_LENGTH = 6;
    private final int TEXTURE_WIDTH = 176;
    private final int TEXTURE_HEIGHT = 168;
    private int xOffset;
    private int yOffset;
    private TextFieldWidget ownerField;
    private ImageButton printButton;
    private final ModScreen screen;

    private boolean isInsertSlotEmpty = false;
    private boolean isResultSlotEmpty = false;


    public CreditCartPrinterScreen(CreditCardPrinterContainer screenContainer, PlayerInventory inv, ITextComponent title) {
        super(screenContainer, inv, title);

        this.screen = new ModScreen();
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);

        this.xOffset = this.width / 2;
        this.yOffset = this.height / 2 - 75;

        this.screen.clearScreen();

        /*
        this.createNewCardCheckbox = new CheckboxButton(0, 0, 20, 20, new StringTextComponent("TEST"), true);
        this.addButton(this.createNewCardCheckbox);
         */

        while(!this.isAccountNumberUnique(this.bankAccountData.getAccountNumber()))
            this.bankAccountData = new BankAccountData();

        initWidgets();
    }

    private void initWidgets(){
        this.screen.addMcWidget(this.ownerField = new TextFieldWidget(this.font, this.guiLeft + 65, this.yOffset + 11, 100, 14, new StringTextComponent("")));
        this.ownerField.setText(new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".placeholder.owner").getString());
        this.ownerField.setCanLoseFocus(true);
        this.ownerField.setTextColor(Color.WHITE);
        this.ownerField.setMaxStringLength(35);

        this.screen.addMcWidget(this.printButton = new ImageButton(this.guiLeft + 145, this.yOffset + 56, 20, 18, 0, 0, 19, Texture.PRINT_BUTTON, (button) -> {
            this.printCard();
        }));

        //this.screen.setTaskbar(new Taskbar(this.guiLeft + 62, this.yOffset + 64, 106, 13, Color.DARK_GRAY_HEX, false, false));
    }

    private BankAccountData bankAccountData = new BankAccountData();

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        if(!this.isInsertSlotEmpty && !this.bankAccountData.isSet()){
            this.bankAccountData = new BankAccountData("John Doe");
        }

        this.updateSlotStates();
        this.updateWidgets();
        this.drawPrinterScreen(matrixStack);

        this.screen.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void updateSlotStates(){
        this.isInsertSlotEmpty = this.container.tileEntity.getIntData().get(0) == 0;
        this.isResultSlotEmpty = this.container.tileEntity.getIntData().get(1) == 0;
    }

    private void updateWidgets(){
        this.ownerField.setEnabled(!this.isInsertSlotEmpty);
        this.printButton.visible = this.ownerField.getText().length() >= MIN_OWNER_LENGTH &&
                !this.isInsertSlotEmpty &&
                !this.ownerField.getText().equals(new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".placeholder.owner").getString()) ||
                !this.isResultSlotEmpty;
    }

    private void drawPrinterScreen(MatrixStack matrixStack){
        this.playerInventoryTitleX = 1000;
        this.titleX = 1000;

        GlStateManager.pushMatrix();
        GlStateManager.scalef(.5f, .5f, .5f);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".label.accNr").getString(), (this.guiLeft + 65) * 2, (this.yOffset + 31) * 2, Color.WHITE);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".label.validity").getString(), (this.guiLeft + 65) * 2, (this.yOffset + 38) * 2, Color.WHITE);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".label.pin").getString(), (this.guiLeft + 65) * 2, (this.yOffset + 45) * 2, Color.WHITE);

        if(this.bankAccountData.isSet()){
            Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.bankAccountData.getAccountNumber(), (this.guiLeft + 120) * 2, (this.yOffset + 31) * 2, Color.WHITE);
            Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.bankAccountData.getValidity(), (this.guiLeft + 120) * 2, (this.yOffset + 38) * 2, Color.WHITE);
            Minecraft.getInstance().fontRenderer.drawString(matrixStack, this.bankAccountData.getPin(), (this.guiLeft + 120) * 2, (this.yOffset + 45) * 2, Color.WHITE);
        }

        GlStateManager.popMatrix();
    }

    private void printCard(){
        this.bankAccountData.setOwner(this.ownerField.getText());
        Network.INSTANCE.sendToServer(new CSPacketSendBankAccountData(this.bankAccountData, this.container.tileEntity.getPos()));

        this.bankAccountData = new BankAccountData();
        this.ownerField.setText(new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".placeholder.owner").getString());
    }

    private List<String> accountNumbers = new ArrayList<>();

    public void setAccountNumbers(List<String> accountNumbers) {
        this.accountNumbers = accountNumbers;
    }

    private boolean isAccountNumberUnique(String accountNumber){
        return !accountNumbers.contains(accountNumber);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);

        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(Texture.CC_PRINTER_GUI);

        this.blit(matrixStack, this.xOffset - (TEXTURE_WIDTH / 2), this.yOffset, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            assert this.minecraft != null;
            assert this.minecraft.player != null;
            this.minecraft.player.closeScreen();
        }

        return true;
    }
}
