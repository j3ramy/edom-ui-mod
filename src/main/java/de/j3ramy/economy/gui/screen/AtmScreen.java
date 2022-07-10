package de.j3ramy.economy.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.j3ramy.economy.EconomyMod;
import de.j3ramy.economy.container.AtmContainer;
import de.j3ramy.economy.item.*;
import de.j3ramy.economy.tileentity.AtmTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.lang3.math.NumberUtils;

public class AtmScreen extends ContainerScreen<AtmContainer> {

    private final ResourceLocation GUI = new ResourceLocation(EconomyMod.MOD_ID, "textures/gui/atm_gui.png");
    private final PlayerEntity player = Minecraft.getInstance().player;
    AtmTile te;

    private final int pinLength = 4;
    private final int infoColorError = 15087158;
    private final int infoColorSuccess = 5498422;
    private final int infoColorDefault = 16777215;

    private String OWNER = "";
    private final String PIN;
    private final int BALANCE;
    private final boolean ACCESS_DENIED;
    private final String ACCOUNT_NUMBER;

    private String pinPlaceholder = "";
    private String pin = "";
    private String info = "Geben Sie Ihre PIN ein";
    private int infoColor = infoColorDefault;

    private enum AtmState{
        PIN_INPUT, MAIN_MENU, DEPOSIT_MENU, WITHDRAW_MENU
    }
    private AtmState state = AtmState.PIN_INPUT;
    private int depositValue = 0;

    public AtmScreen(AtmContainer screenContainer, PlayerInventory inv, ITextComponent title) {
        super(screenContainer, inv, title);

        this.te = (AtmTile) screenContainer.tileEntity;

        CompoundNBT nbt = inv.getCurrentItem().hasTag() ? inv.getCurrentItem().getTag() : null;
        if(nbt != null)
            OWNER = nbt.getString("owner");

            ACCOUNT_NUMBER = "";
            PIN = "";
            BALANCE = 0;
            ACCESS_DENIED = false;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);

        this.playerInventoryTitleX = 1000;
        this.titleX = 1000;

        drawString(matrixStack, Minecraft.getInstance().fontRenderer, info, 159, 95, infoColor);

        drawClock(matrixStack);
        drawPinPad(matrixStack);

        switch(state){
            case MAIN_MENU: drawMainMenu(matrixStack); break;
            case WITHDRAW_MENU: drawWithdrawMenu(matrixStack); break;
            case DEPOSIT_MENU: drawDepositMenu(matrixStack); break;
        }

    }

    private void drawClock(MatrixStack matrixStack){
        if(player == null)
            return;

        String time = formatTime(player.world.getDayTime());
        drawString(matrixStack, Minecraft.getInstance().fontRenderer, time, 293, 18, infoColorDefault);
    }

    private void drawPinPad(MatrixStack matrixStack){
        drawString(matrixStack, Minecraft.getInstance().fontRenderer, pinPlaceholder, 340, 27, 12698049);

        this.addButton(new Button(332, 42, 18, 18, new StringTextComponent("7"), (onclick) -> {
            if(state == AtmState.PIN_INPUT)
                addToPin("7");
        }));

        this.addButton(new Button(352, 42, 18, 18, new StringTextComponent("8"), (onclick) -> {
            if(state == AtmState.PIN_INPUT)
                addToPin("8");
        }));

        this.addButton(new Button(372, 42, 18, 18, new StringTextComponent("9"), (onclick) -> {
            if(state == AtmState.PIN_INPUT)
                addToPin("9");
        }));

        this.addButton(new Button(332, 62, 18, 18, new StringTextComponent("4"), (onclick) -> {
            if(state == AtmState.PIN_INPUT)
                addToPin("4");
        }));

        this.addButton(new Button(352, 62, 18, 18, new StringTextComponent("5"), (onclick) -> {
            if(state == AtmState.PIN_INPUT)
                addToPin("5");
        }));

        this.addButton(new Button(372, 62, 18, 18, new StringTextComponent("6"), (onclick) -> {
            if(state == AtmState.PIN_INPUT)
                addToPin("6");
        }));

        this.addButton(new Button(332, 82, 18, 18, new StringTextComponent("1"), (onclick) -> {
            if(state == AtmState.PIN_INPUT)
                addToPin("1");
        }));

        this.addButton(new Button(352, 82, 18, 18, new StringTextComponent("2"), (onclick) -> {
            if(state == AtmState.PIN_INPUT)
                addToPin("2");
        }));

        this.addButton(new Button(372, 82, 18, 18, new StringTextComponent("3"), (onclick) -> {
            if(state == AtmState.PIN_INPUT)
                addToPin("3");
        }));

        this.addButton(new Button(332, 102, 18, 18, new StringTextComponent("C"), (onclick) -> {
            if(state == AtmState.PIN_INPUT)
                clearPin();
        }));

        this.addButton(new Button(352, 102, 18, 18, new StringTextComponent("0"), (onclick) -> {
            if(state == AtmState.PIN_INPUT)
                addToPin("0");
        }));

        this.addButton(new Button(372, 102, 18, 18, new StringTextComponent("B"), (onclick) -> {
            if(state == AtmState.PIN_INPUT)
                removeFromPin();
        }));

        this.addButton(new Button(332, 122, 58, 18, new StringTextComponent("Enter"), (onclick) -> {
            if(state == AtmState.PIN_INPUT)
                submitPin();
        }));
    }

    private void drawMainMenu(MatrixStack matrixStack){
        drawString(matrixStack, Minecraft.getInstance().fontRenderer, "Kontoinhaber:", 159, 31, infoColorDefault);
        drawString(matrixStack, Minecraft.getInstance().fontRenderer, OWNER, 230, 31, infoColorDefault);

        drawString(matrixStack, Minecraft.getInstance().fontRenderer, "Kontonummer:", 159, 41, infoColorDefault);
        drawString(matrixStack, Minecraft.getInstance().fontRenderer, ACCOUNT_NUMBER, 230, 41, infoColorDefault);

        drawString(matrixStack, Minecraft.getInstance().fontRenderer, "Kontostand:", 159, 61, infoColorDefault);

        if(BALANCE <= 0 )
            drawString(matrixStack, Minecraft.getInstance().fontRenderer, BALANCE + " EUR", 230, 61, infoColorError);
        else
            drawString(matrixStack, Minecraft.getInstance().fontRenderer, BALANCE + " EUR", 230, 61, infoColorSuccess);

        info = "Guten Tag, " + OWNER;

        this.addButton(new Button(70, 35, 80, 20, new StringTextComponent("Auszahlung"), (onclick) -> state = AtmState.WITHDRAW_MENU));

        this.addButton(new Button(70, 57, 80, 20, new StringTextComponent("Einzahlung"), (onclick) -> state = AtmState.DEPOSIT_MENU));
    }

    void drawWithdrawMenu(MatrixStack matrixStack){
        drawString(matrixStack, Minecraft.getInstance().fontRenderer, "Kontostand:", 159, 31, infoColorDefault);

        if(BALANCE <= 0 )
            drawString(matrixStack, Minecraft.getInstance().fontRenderer, BALANCE + " EUR", 230, 31, infoColorError);
        else
            drawString(matrixStack, Minecraft.getInstance().fontRenderer, BALANCE + " EUR", 230, 31, infoColorSuccess);


        this.addButton(new Button(159, 45, 25, 20, new StringTextComponent("5"), (onclick) -> {

        }));

        this.addButton(new Button(186, 45, 25, 20, new StringTextComponent("10"), (onclick) -> {

        }));

        this.addButton(new Button(213, 45, 25, 20, new StringTextComponent("20"), (onclick) -> {

        }));

        this.addButton(new Button(240, 45, 25, 20, new StringTextComponent("50"), (onclick) -> {

        }));

        this.addButton(new Button(159, 67, 25, 20, new StringTextComponent("100"), (onclick) -> {

        }));

        this.addButton(new Button(186, 67, 25, 20, new StringTextComponent("200"), (onclick) -> {

        }));

        this.addButton(new Button(213, 67, 25, 20, new StringTextComponent("500"), (onclick) -> {

        }));

    }

    void drawDepositMenu(MatrixStack matrixStack){
        countDepositNotes();
        drawString(matrixStack, Minecraft.getInstance().fontRenderer, "Kontostand:", 159, 31, infoColorDefault);

        if(BALANCE <= 0 )
            drawString(matrixStack, Minecraft.getInstance().fontRenderer, BALANCE + " EUR", 230, 31, infoColorError);
        else
            drawString(matrixStack, Minecraft.getInstance().fontRenderer, BALANCE + " EUR", 230, 31, infoColorSuccess);

        drawString(matrixStack, Minecraft.getInstance().fontRenderer, "Einzahlsumme:", 159, 41, infoColorDefault);
        drawString(matrixStack, Minecraft.getInstance().fontRenderer, depositValue  + " EUR", 230, 41, infoColorDefault);

        this.addButton(new Button(240, 72, 80, 20, new StringTextComponent("Einzahlen"), (onclick) -> {

        }));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);

        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(GUI);

        int i = this.guiLeft;
        int j = 0;
        this.blit(matrixStack, i, j, 0, 0, 243, 221);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

        if(state == AtmState.PIN_INPUT){
            switch(keyCode){
                case 49: addToPin("1"); break;
                case 50: addToPin("2"); break;
                case 51: addToPin("3"); break;
                case 52: addToPin("4"); break;
                case 53: addToPin("5"); break;
                case 54: addToPin("6"); break;
                case 55: addToPin("7"); break;
                case 56: addToPin("8"); break;
                case 57: addToPin("9"); break;
                case 48: addToPin("0"); break;
                case 259: removeFromPin(); break;
                case 257: submitPin(); break;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void addToPin(String character){
        if(pin.length() < pinLength && pinPlaceholder.length() < pinLength){
            pin += character;
            pinPlaceholder += "*";
        }
    }

    private void removeFromPin(){
        if(pin.length() > 0 && pinPlaceholder.length() > 0){
            StringBuilder sb = new StringBuilder(pin);
            sb.deleteCharAt(pin.length() - 1);
            pin = sb.toString();

            StringBuilder sb1 = new StringBuilder(pinPlaceholder);
            sb1.deleteCharAt(pinPlaceholder.length() - 1);
            pinPlaceholder = sb1.toString();
        }
    }

    private void clearPin(){
        pin = "";
        pinPlaceholder = "";
    }

    private void submitPin(){
        if(pin.length() == pinLength && NumberUtils.isParsable(pin)){

            if(!ACCESS_DENIED){
                infoColor = infoColorError;
                info = "Ihr Konto wurde gesperrt";
                return;
            }

            if(pin.equals(PIN)){
                infoColor = infoColorDefault;
                info = "Guten Tag, Dominik Jung";
                state = AtmState.MAIN_MENU;
                clearPin();
            }
            else{
                infoColor = infoColorError;
                info = "PIN-Eingabe fehlerhaft";
            }
        }
    }

    private boolean areSlotOccupied(){
        boolean slotsOccupied = false;

        for(int i = 0; i < 8; i++){
            if(!te.getItemHandler().getStackInSlot(i).isEmpty()){
                slotsOccupied = true;
            }
        }

        return slotsOccupied;
    }

    private void countDepositNotes(){
        depositValue = 0;
        for(int i = 0; i < 8; i++){
            ItemStack stack = te.getItemHandler().getStackInSlot(i);

            if(stack.getItem() instanceof FiveEuro){
                depositValue += 5 * stack.getCount();
            }
            else if(stack.getItem() instanceof TenEuro){
                depositValue += 10 * stack.getCount();
            }
            else if(stack.getItem() instanceof TwentyEuro){
                depositValue += 20 * stack.getCount();
            }
            else if(stack.getItem() instanceof FiftyEuro){
                depositValue += 50 * stack.getCount();
            }
            else if(stack.getItem() instanceof OnehundredEuro){
                depositValue += 100 * stack.getCount();
            }
            else if(stack.getItem() instanceof TwohundredEuro){
                depositValue += 200 * stack.getCount();
            }
            else if(stack.getItem() instanceof FivehundredEuro){
                depositValue += 500 * stack.getCount();
            }
        }
    }

    public static String formatTime(Long time) {
        int hours24 = (int)(time / 1000L + 6L) % 24;
        int hours = hours24 % 24;
        int minutes = (int)((float) time / 16.666666F % 60.0F);

        return String.format("%02d:%02d", hours < 1 ? 12 : hours, minutes);
    }
}
