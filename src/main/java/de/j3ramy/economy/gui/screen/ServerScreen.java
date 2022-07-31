package de.j3ramy.economy.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.j3ramy.economy.EconomyMod;
import de.j3ramy.economy.container.ServerContainer;
import de.j3ramy.economy.gui.widgets.*;
import de.j3ramy.economy.network.CSPacketLoadBackup;
import de.j3ramy.economy.network.CSPacketSendServerData;
import de.j3ramy.economy.network.Network;
import de.j3ramy.economy.utils.Color;
import de.j3ramy.economy.utils.GuiUtils;
import de.j3ramy.economy.utils.Texture;
import de.j3ramy.economy.utils.server.Entry;
import de.j3ramy.economy.utils.server.Server;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.Objects;

public class ServerScreen extends ContainerScreen<ServerContainer> {
    private final ModScreen setUpScreen, overviewScreen, settingsScreen, adminScreen;
    private final int TEXTURE_WIDTH = 256;
    private final int TEXTURE_HEIGHT = 170;

    public int xPos;
    public int yPos;

    private enum ServerScreenState{
        SET_UP,
        OVERVIEW,
        MORE_SETTINGS,
        ADMIN
    }
    private ServerScreenState screenState = ServerScreenState.SET_UP;

    private Server server = new Server(new CompoundNBT());
    public void setServer(Server server) {
        this.server = server;
    }


    public ServerScreen(ServerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);

        this.setUpScreen = new ModScreen();
        this.overviewScreen = new ModScreen();
        this.settingsScreen = new ModScreen();
        this.adminScreen = new ModScreen();
    }


    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);

        this.setUpScreen.clearScreen();
        this.overviewScreen.clearScreen();
        this.settingsScreen.clearScreen();

        this.xPos = (this.width / 2) - (TEXTURE_WIDTH / 2);
        this.yPos = this.height / 2 - 75;

        this.initSetUpScreen();
        this.initOverviewScreen();
        this.initSettingsScreen();
        this.initAdminScreen();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.playerInventoryTitleX = 1000;
        this.renderBackground(matrixStack);

        super.render(matrixStack, mouseX, mouseY, partialTicks);

        if(this.screenState != ServerScreenState.MORE_SETTINGS && this.screenState != ServerScreenState.ADMIN)
            this.screenState = this.server.isSet() ? ServerScreenState.OVERVIEW : ServerScreenState.SET_UP;

        //draw server title heading
        GlStateManager.pushMatrix();
        GlStateManager.scalef(.5f, .5f, .5f);
        String titleText = new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".heading.server").getString();
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, titleText + " | " + ((screenState != ServerScreenState.SET_UP) ? this.server.getIp() : "-")
                + " | " + GuiUtils.formatTime(Objects.requireNonNull(this.container.getTileEntity().getWorld()).getDayTime()),
                        (this.xPos + 4) * 2, (this.yPos + 4) * 2, Color.WHITE);
        GlStateManager.popMatrix();

        //draw screen
        switch(screenState){
            case SET_UP:
                this.overviewScreen.disable();
                this.settingsScreen.disable();
                this.setUpScreen.enable();
                this.adminScreen.disable();

                this.renderSetUpScreen(matrixStack, mouseX, mouseY, partialTicks);
                this.updateSetUpScreen();
                break;
            case OVERVIEW:
                this.overviewScreen.enable();
                this.settingsScreen.disable();
                this.setUpScreen.disable();
                this.adminScreen.disable();

                this.renderOverviewScreen(matrixStack, mouseX, mouseY, partialTicks);
                this.updateOverviewScreen();
                break;
            case MORE_SETTINGS:
                this.overviewScreen.disable();
                this.settingsScreen.enable();
                this.setUpScreen.disable();
                this.adminScreen.disable();

                this.renderSettingsScreen(matrixStack, mouseX, mouseY, partialTicks);
                this.updateSettingsScreen();
                break;
            case ADMIN:
                this.overviewScreen.disable();
                this.settingsScreen.disable();
                this.setUpScreen.disable();
                this.adminScreen.enable();

                this.renderAdminScreen(matrixStack, mouseX, mouseY, partialTicks);
                this.updateAdminScreen();
                break;
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        assert this.minecraft != null;

        this.minecraft.getTextureManager().bindTexture(Texture.SCREEN_GUI_INV);
        this.blit(matrixStack, this.xPos, this.yPos, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    //region OVERVIEW SCREEN
    private Button onButton, offButton;

    public void initOverviewScreen(){
        this.overviewScreen.addButton(new Button(this.xPos + TEXTURE_WIDTH - 60 - 10, this.yPos + 36, 60, 16,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".button.admin"), (action) ->{
            this.screenState = ServerScreenState.ADMIN;
        }));

        this.overviewScreen.addButton(new Button(this.xPos + TEXTURE_WIDTH - 60 - 10, this.yPos + 57, 60, 16,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".button.more"), (action) ->{
            this.screenState = ServerScreenState.MORE_SETTINGS;
        }));

        this.overviewScreen.addButton(this.onButton = new Button(this.xPos + TEXTURE_WIDTH - 50 - 10, this.yPos + TEXTURE_HEIGHT - 18 - 15 - 22 - 18, 50, 18,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".button.on"), (click)->{
            this.server.setOn(true);

            Network.INSTANCE.sendToServer(new CSPacketSendServerData(this.server, false));
        }));

        this.overviewScreen.addButton(this.offButton = new Button(this.xPos + TEXTURE_WIDTH - 50 - 10, this.yPos + TEXTURE_HEIGHT - 18 - 10 - 22, 50, 18,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".button.off"), (click)->{
            this.server.setOn(false);

            Network.INSTANCE.sendToServer(new CSPacketSendServerData(this.server, false));
        }));

        //right area
        this.overviewScreen.addVerticalLine(new VerticalLine(this.xPos + 175, this.yPos + 35, 100, Color.WHITE_HEX));
    }

    private void renderOverviewScreen(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
        drawCenteredString(matrixStack, this.font, new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".heading.server_overview").getString(),
                (this.width / 2),
                (this.yPos + 20),
                Color.WHITE);

        //stats
        this.drawGeneralInfo(matrixStack);
        this.drawStats(matrixStack);

        //on/off indicator
        AbstractGui.fill(matrixStack, this.xPos + TEXTURE_WIDTH - 50 - 20, this.yPos + TEXTURE_HEIGHT - 18 - 15 - 18 - 22,
                this.xPos + TEXTURE_WIDTH - 50 - 15, this.yPos + TEXTURE_HEIGHT - 10 - 22, this.server.isOn() ? Color.GREEN_HEX : Color.RED_HEX);

        this.overviewScreen.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void drawGeneralInfo(MatrixStack matrixStack){
        GlStateManager.pushMatrix();
        GlStateManager.scalef(.75f, .75f, .75f);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".heading.server_overview.general").getString(),
                (int) ((this.xPos + 15) * GuiUtils.getScalingPositionMultiplier(.75f)),
                (int) ((this.yPos + 35) * GuiUtils.getScalingPositionMultiplier(.75f)),
                Color.WHITE);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.scalef(.5f, .5f, .5f);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".label.server_ip").getString(),
                (int) ((this.xPos + 15) * GuiUtils.getScalingPositionMultiplier(.5f)),
                (int) ((this.yPos + 45) * GuiUtils.getScalingPositionMultiplier(.5f)),
                Color.WHITE);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack,
                this.server.getIp(),
                (int) ((this.xPos + 70) * GuiUtils.getScalingPositionMultiplier(.5f)),
                (int) ((this.yPos + 45) * GuiUtils.getScalingPositionMultiplier(.5f)),
                Color.WHITE);

        Minecraft.getInstance().fontRenderer.drawString(matrixStack,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".label.db_name").getString(),
                (int) ((this.xPos + 15) * GuiUtils.getScalingPositionMultiplier(.5f)),
                (int) ((this.yPos + 53) * GuiUtils.getScalingPositionMultiplier(.5f)),
                Color.WHITE);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack,
                this.server.getDatabase().getName(),
                (int) ((this.xPos + 70) * GuiUtils.getScalingPositionMultiplier(.5f)),
                (int) ((this.yPos + 53) * GuiUtils.getScalingPositionMultiplier(.5f)),
                Color.WHITE);

        Minecraft.getInstance().fontRenderer.drawString(matrixStack,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".label.password").getString(),
                (int) ((this.xPos + 15) * GuiUtils.getScalingPositionMultiplier(.5f)),
                (int) ((this.yPos + 62) * GuiUtils.getScalingPositionMultiplier(.5f)),
                Color.WHITE);

        StringBuilder password = new StringBuilder();
        password.append(new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".label.no_password").getString());

        if(!this.server.getPassword().isEmpty()){
            password.delete(0, password.length());
            for(int i = 0; i < this.server.getPassword().length(); i++)
                password.append("*");
        }

        Minecraft.getInstance().fontRenderer.drawString(matrixStack,
                password.toString(),
                (int) ((this.xPos + 70) * GuiUtils.getScalingPositionMultiplier(.5f)),
                (int) ((this.yPos + 62) * GuiUtils.getScalingPositionMultiplier(.5f)),
                Color.WHITE);
        GlStateManager.popMatrix();
    }

    private void drawStats(MatrixStack matrixStack){
        GlStateManager.pushMatrix();
        GlStateManager.scalef(.75f, .75f, .75f);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".heading.server_overview.stats").getString(),
                (int) ((this.xPos + 15) * GuiUtils.getScalingPositionMultiplier(.75f)),
                (int) ((this.yPos + 80) * GuiUtils.getScalingPositionMultiplier(.75f)),
                Color.WHITE);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.scalef(.5f, .5f, .5f);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".label.amount_tables").getString(),
                (int) ((this.xPos + 15) * GuiUtils.getScalingPositionMultiplier(.5f)),
                (int) ((this.yPos + 91) * GuiUtils.getScalingPositionMultiplier(.5f)),
                Color.WHITE);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack,
                Integer.toString(this.server.getDatabase().getTableCount()),
                (int) ((this.xPos + 70) * GuiUtils.getScalingPositionMultiplier(.5f)),
                (int) ((this.yPos + 91) * GuiUtils.getScalingPositionMultiplier(.5f)),
                Color.WHITE);

        Minecraft.getInstance().fontRenderer.drawString(matrixStack,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".label.amount_entries").getString(),
                (int) ((this.xPos + 15) * GuiUtils.getScalingPositionMultiplier(.5f)),
                (int) ((this.yPos + 99) * GuiUtils.getScalingPositionMultiplier(.5f)),
                Color.WHITE);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack,
                Integer.toString(this.server.getDatabase().getTotalEntryCount()),
                (int) ((this.xPos + 70) * GuiUtils.getScalingPositionMultiplier(.5f)),
                (int) ((this.yPos + 99) * GuiUtils.getScalingPositionMultiplier(.5f)),
                Color.WHITE);

        Minecraft.getInstance().fontRenderer.drawString(matrixStack,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".label.total_accesses").getString(),
                (int) ((this.xPos + 15) * GuiUtils.getScalingPositionMultiplier(.5f)),
                (int) ((this.yPos + 107) * GuiUtils.getScalingPositionMultiplier(.5f)),
                Color.WHITE);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack,
                Integer.toString(this.server.getAccesses()),
                (int) ((this.xPos + 70) * GuiUtils.getScalingPositionMultiplier(.5f)),
                (int) ((this.yPos + 107) * GuiUtils.getScalingPositionMultiplier(.5f)),
                Color.WHITE);
        GlStateManager.popMatrix();
    }

    private void updateOverviewScreen(){
        if(!this.server.isOn()){
            this.onButton.active = true;
            this.offButton.active = false;
        }
        else{
            this.onButton.active = false;
            this.offButton.active = true;
        }

    }

    //endregion

    //region SET UP SCREEN
    private TextFieldWidget ipField;
    private DropDown typeDropDown;
    private Button saveButton;

    public void initSetUpScreen(){
        this.setUpScreen.addCenteredHorizontalLine(new CenteredHorizontalLine(this.width, this.yPos + 32, 150, Color.WHITE_HEX));

        //add ip text field
        this.setUpScreen.addTextField(this.ipField = new TextFieldWidget(this.font, this.width / 2 - 45, this.yPos + 43, 90, 18, new StringTextComponent("")));
        this.ipField.setText(new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".placeholder.ip").getString());
        this.ipField.setCanLoseFocus(true);
        this.ipField.setTextColor(Color.WHITE);
        this.ipField.setMaxStringLength(15);

        String[] options = new String[Server.DBType.values().length];
        for(int i = 0; i < Server.DBType.values().length; i++){
            options[i] = Server.DBType.values()[i].toString();
        }
        this.setUpScreen.addDropDown(this.typeDropDown = new DropDown(options, this.width / 2 - 45, this.yPos + 70, 90, 18, "Preset"));

        this.setUpScreen.addButton(this.saveButton = new Button(this.width / 2 - 30, this.yPos + 100, 60, 14,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".button.save"), (onPress) ->{

            Server server = new Server(
                    Server.DBType.valueOf(this.typeDropDown.getSelectedText()),
                    this.ipField.getText().replace(' ', '_').toLowerCase(),
                    this.container.getTileEntity().getPos());

            this.setServer(server);

            Network.INSTANCE.sendToServer(new CSPacketSendServerData(this.server, false));
            this.screenState = ServerScreenState.OVERVIEW;
        }));
    }

    private void renderSetUpScreen(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){

        drawCenteredString(matrixStack, this.font, new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".heading.server_setup").getString(),
                (this.width / 2),
                (this.yPos + 20),
                Color.WHITE);

        this.setUpScreen.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void updateSetUpScreen(){
        if(this.saveButton != null)
            this.saveButton.active = !this.ipField.getText().isEmpty() && !this.typeDropDown.getSelectedText().equals(this.typeDropDown.getPlaceholder());
    }
    //endregion

    //region SETTINGS
    private TextFieldWidget changeIpField, passwordField;
    private ConfirmPopUp confirmPopUp;

    private void initSettingsScreen(){
        this.settingsScreen.addButton(new Button(this.xPos + 10, this.yPos + this.TEXTURE_HEIGHT - 22 - 10 - 16, 60, 16,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".button.save"), (click) -> {

            if(this.changeIpField.getText().isEmpty()){
                this.settingsScreen.addAlertPopUp(new AlertPopUp(
                        this,
                        new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.title.no_ip").getString(),
                        new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.content.no_ip").getString(),
                        AlertPopUp.ColorType.ERROR
                        ));

                return;
            }

            this.server.setIp(this.changeIpField.getText());
            this.server.setPassword(this.passwordField.getText());

            Network.INSTANCE.sendToServer(new CSPacketSendServerData(this.server, false));
            this.screenState = ServerScreenState.OVERVIEW;
        }));

        this.settingsScreen.addButton(new Button(this.xPos + TEXTURE_WIDTH - 10 - 60, this.yPos + this.TEXTURE_HEIGHT - 22 - 10 - 16, 60, 16,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".button.back"), (click) -> {
            this.screenState = ServerScreenState.OVERVIEW;
        }));

        //add ip text field
        this.settingsScreen.addTextField(this.changeIpField = new TextFieldWidget(this.font, this.xPos + 71, this.yPos + 45, 85, 14, new StringTextComponent("")));
        this.changeIpField.setCanLoseFocus(true);
        this.changeIpField.setTextColor(Color.WHITE);
        this.changeIpField.setMaxStringLength(20);

        //add password text field
        this.settingsScreen.addTextField(this.passwordField = new TextFieldWidget(this.font, this.xPos + 71, this.yPos + 65, 85, 14, new StringTextComponent("")));
        this.passwordField.setCanLoseFocus(true);
        this.passwordField.setTextColor(Color.WHITE);
        this.passwordField.setMaxStringLength(20);

        //danger area bottom
        ImageButton resetServerButton;
        this.settingsScreen.addImageButton(resetServerButton = new ImageButton(this.xPos + (TEXTURE_WIDTH / 2) + 15, this.yPos + 95, 20, 18, 0, 0, 19, Texture.DELETE_BUTTON, (click)-> {
            this.settingsScreen.addConfirmPopUp(this.confirmPopUp = new ConfirmPopUp(
                    this,
                    new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.title.server_reset").getString(),
                    new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.content.server_reset").getString(),
                    ConfirmPopUp.ColorType.ERROR,
                    (yesAction)->{
                        this.server = new Server(new CompoundNBT());
                        this.server.setPos(this.container.getTileEntity().getPos());
                        Network.INSTANCE.sendToServer(new CSPacketSendServerData(this.server, false));

                        this.confirmPopUp.hide();
                        this.passwordField.setText("");
                    }));
        }));

        ImageButton saveServerButton;
        this.settingsScreen.addImageButton(saveServerButton = new ImageButton(this.xPos + 15, this.yPos + 95, 20, 18, 0, 0, 19, Texture.SAVE_BUTTON, (click)->{

            //check if drive is plugged in
            if(this.container.getTileEntity().getIntData().get(0) == 0){
                this.settingsScreen.addAlertPopUp(new AlertPopUp(
                        this,
                        new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.title.no_drive_found").getString(),
                        new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.content.no_drive_found").getString(),
                        AlertPopUp.ColorType.NOTICE));
                return;
            }

            //check if drive has tag
            if(this.container.getTileEntity().getIntData().get(1) == 1){
                this.settingsScreen.addConfirmPopUp(this.confirmPopUp = new ConfirmPopUp(
                        this,
                        new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.title.drive_has_data").getString(),
                        new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.content.drive_has_data").getString(),
                        ConfirmPopUp.ColorType.NOTICE,
                        (yesAction)->{
                            Network.INSTANCE.sendToServer(new CSPacketSendServerData(this.server, true));

                            this.confirmPopUp.hide();
                            this.overviewScreen.addAlertPopUp(new AlertPopUp(
                                    this,
                                    new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.title.backup_created").getString(),
                                    new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.content.backup_created").getString(),
                                    AlertPopUp.ColorType.DEFAULT));
                        }));
            }
            else{
                Network.INSTANCE.sendToServer(new CSPacketSendServerData(this.server, true));

                this.settingsScreen.addAlertPopUp(new AlertPopUp(
                        this,
                        new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.title.backup_created").getString(),
                        new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.content.backup_created").getString(),
                        AlertPopUp.ColorType.DEFAULT));
            }


        }));

        ImageButton loadServerButton;
        this.settingsScreen.addImageButton(loadServerButton = new ImageButton(this.xPos + 40, this.yPos + 95, 20, 18, 0, 0, 19, Texture.LOAD_BUTTON, (click)->{

            //check if drive is plugged in
            if(this.container.getTileEntity().getIntData().get(0) == 0){
                this.settingsScreen.addAlertPopUp(new AlertPopUp(
                        this,
                        new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.title.no_drive_found").getString(),
                        new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.content.no_drive_found").getString(),
                        AlertPopUp.ColorType.NOTICE));
                return;
            }

            //check if drive has backup
            if(this.container.getTileEntity().getIntData().get(1) == 0){
                this.settingsScreen.addAlertPopUp(new AlertPopUp(
                        this,
                        new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.title.drive_no_data").getString(),
                        new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.content.drive_no_data").getString(),
                        AlertPopUp.ColorType.ERROR));
                return;
            }

            this.settingsScreen.addConfirmPopUp(this.confirmPopUp = new ConfirmPopUp(
                    this,
                    new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.title.backup_overwrite").getString(),
                    new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.content.backup_overwrite").getString(),
                    ConfirmPopUp.ColorType.NOTICE,
                    (yesAction)->{
                        Network.INSTANCE.sendToServer(new CSPacketLoadBackup(this.server.getPos()));

                        this.confirmPopUp.hide();
                        this.settingsScreen.addAlertPopUp(new AlertPopUp(
                                this,
                                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.title.backup_loaded").getString(),
                                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.content.backup_loaded").getString(),
                                AlertPopUp.ColorType.DEFAULT));

                    }));
        }));

        //tooltips
        this.settingsScreen.addTooltip(new Tooltip(new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".tooltip.reset_server").getString(), resetServerButton));
        this.settingsScreen.addTooltip(new Tooltip(new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".tooltip.load_from_drive").getString(), loadServerButton));
        this.settingsScreen.addTooltip(new Tooltip(new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".tooltip.save_to_drive").getString(), saveServerButton));
    }

    private void renderSettingsScreen(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
        drawCenteredString(matrixStack, this.font, new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".heading.more_settings").getString(),
                (this.width / 2),
                (this.yPos + 20),
                Color.WHITE);

        GlStateManager.pushMatrix();
        GlStateManager.scalef(.75f, .75f, .75f);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".heading.more_settings.access_settings").getString(),
                (int) ((this.xPos + 15) * GuiUtils.getScalingPositionMultiplier(.75f)),
                (int) ((this.yPos + 35) * GuiUtils.getScalingPositionMultiplier(.75f)),
                Color.WHITE);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.scalef(.5f, .5f, .5f);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".label.server_ip").getString(),
                (int) ((this.xPos + 15) * GuiUtils.getScalingPositionMultiplier(.5f)),
                (int) ((this.yPos + 45) * GuiUtils.getScalingPositionMultiplier(.5f)),
                Color.WHITE);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".label.password").getString(),
                (int) ((this.xPos + 15) * GuiUtils.getScalingPositionMultiplier(.5f)),
                (int) ((this.yPos + 65) * GuiUtils.getScalingPositionMultiplier(.5f)),
                Color.WHITE);
        GlStateManager.popMatrix();


        GlStateManager.pushMatrix();
        GlStateManager.scalef(.75f, .75f, .75f);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".heading.more_settings.backup").getString(),
                (int) ((this.xPos + 15) * GuiUtils.getScalingPositionMultiplier(.75f)),
                (int) ((this.yPos + 85) * GuiUtils.getScalingPositionMultiplier(.75f)),
                Color.WHITE);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.scalef(.75f, .75f, .75f);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".heading.more_settings.danger_zone").getString(),
                (int) ((this.xPos + (TEXTURE_WIDTH / 2) + 15) * GuiUtils.getScalingPositionMultiplier(.75f)),
                (int) ((this.yPos + 85) * GuiUtils.getScalingPositionMultiplier(.75f)),
                Color.RED);
        GlStateManager.popMatrix();

        this.settingsScreen.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private boolean isIpInitialSet = false;
    private boolean isPasswordInitialSet = false;
    private void updateSettingsScreen(){
        if(!this.isIpInitialSet && this.server.isSet()){
            this.changeIpField.setText(this.server.getIp());
            this.isIpInitialSet = true;
        }

        if(!this.isPasswordInitialSet && this.server.isSet()){
            this.passwordField.setText(this.server.getPassword());
            this.isPasswordInitialSet = true;
        }
    }

    //endregion

    //region ADMIN
    private TextFieldWidget adminUsernameField, adminPasswordField;
    private void initAdminScreen(){
        this.adminScreen.addButton(new Button(this.xPos + 10, this.yPos + this.TEXTURE_HEIGHT - 22 - 10 - 16, 60, 16,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".button.save"), (click) -> {
            this.server.setAdminUsername(this.adminUsernameField.getText());
            this.server.setAdminPassword(this.adminPasswordField.getText());

            Entry entry = this.server.getDatabase().getTable("user").getEntry(0);
            ArrayList<String> content = entry.getColumnsContent();
            content.set(0, this.server.getAdminUsername());
            content.set(1, this.server.getAdminPassword());
            this.server.getDatabase().getTable("user").update(0, content);

            Network.INSTANCE.sendToServer(new CSPacketSendServerData(this.server, false));
            this.screenState = ServerScreenState.OVERVIEW;
        }));

        this.adminScreen.addButton(new Button(this.xPos + TEXTURE_WIDTH - 10 - 60, this.yPos + this.TEXTURE_HEIGHT - 22 - 10 - 16, 60, 16,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".button.back"), (click) -> {
            this.screenState = ServerScreenState.OVERVIEW;
        }));

        //add ip text field
        this.adminScreen.addTextField(this.adminUsernameField = new TextFieldWidget(this.font, this.xPos + 71, this.yPos + 45, 85, 14, new StringTextComponent("")));
        this.adminUsernameField.setCanLoseFocus(true);
        this.adminUsernameField.setTextColor(Color.WHITE);
        this.adminUsernameField.setMaxStringLength(20);

        //add password text field
        this.adminScreen.addTextField(this.adminPasswordField = new TextFieldWidget(this.font, this.xPos + 71, this.yPos + 65, 85, 14, new StringTextComponent("")));
        this.adminPasswordField.setCanLoseFocus(true);
        this.adminPasswordField.setTextColor(Color.WHITE);
        this.adminPasswordField.setMaxStringLength(20);
    }

    private void renderAdminScreen(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
        drawCenteredString(matrixStack, this.font, new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".heading.admin_settings").getString(),
                (this.width / 2),
                (this.yPos + 20),
                Color.WHITE);

        GlStateManager.pushMatrix();
        GlStateManager.scalef(.75f, .75f, .75f);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".heading.more_settings.access_settings").getString(),
                (int) ((this.xPos + 15) * GuiUtils.getScalingPositionMultiplier(.75f)),
                (int) ((this.yPos + 35) * GuiUtils.getScalingPositionMultiplier(.75f)),
                Color.WHITE);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.scalef(.5f, .5f, .5f);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".label.admin_username").getString(),
                (int) ((this.xPos + 15) * GuiUtils.getScalingPositionMultiplier(.5f)),
                (int) ((this.yPos + 45) * GuiUtils.getScalingPositionMultiplier(.5f)),
                Color.WHITE);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".label.admin_password").getString(),
                (int) ((this.xPos + 15) * GuiUtils.getScalingPositionMultiplier(.5f)),
                (int) ((this.yPos + 65) * GuiUtils.getScalingPositionMultiplier(.5f)),
                Color.WHITE);
        GlStateManager.popMatrix();

        this.adminScreen.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private boolean isAdminNameInitialSet = false;
    private boolean isAdminPasswordInitialSet = false;
    private void updateAdminScreen(){
        if(!this.isAdminNameInitialSet && this.server.isSet()){
            this.adminUsernameField.setText(this.server.getAdminUsername());
            this.isAdminNameInitialSet = true;
        }

        if(!this.isAdminPasswordInitialSet && this.server.isSet()){
            this.adminPasswordField.setText(this.server.getAdminPassword());
            this.isAdminPasswordInitialSet = true;
        }
    }
    //endregion

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
