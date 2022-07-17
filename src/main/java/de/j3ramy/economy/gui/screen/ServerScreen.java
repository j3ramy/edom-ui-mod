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

import java.util.Objects;

public class ServerScreen extends ContainerScreen<ServerContainer> {
    private final ModScreen setUpScreen;
    private final ModScreen overviewScreen;
    private final int TEXTURE_WIDTH = 256;
    private final int TEXTURE_HEIGHT = 170;

    public int xPos;
    public int yPos;

    private enum ServerScreenState{
        SET_UP,
        OVERVIEW
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
    }


    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);

        this.setUpScreen.clearScreen();
        this.overviewScreen.clearScreen();

        this.xPos = (this.width / 2) - (TEXTURE_WIDTH / 2);
        this.yPos = this.height / 2 - 75;

        this.initSetUpScreen();
        this.initOverviewScreen();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.playerInventoryTitleX = 1000;

        this.screenState = this.server.isSet() ? ServerScreenState.OVERVIEW : ServerScreenState.SET_UP;

        //draw server title heading
        GlStateManager.pushMatrix();
        GlStateManager.scalef(.5f, .5f, .5f);
        String titleText = new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".heading.server").getString();
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, titleText + " | " + ((this.screenState != ServerScreenState.SET_UP) ? this.server.getIp() : "-")
                + " | " + GuiUtils.formatTime(Objects.requireNonNull(this.container.getTileEntity().getWorld()).getDayTime()),
                        (this.xPos + 4) * 2, (this.yPos + 4) * 2, Color.WHITE);
        GlStateManager.popMatrix();

        //draw screen
        switch(this.screenState){
            case SET_UP:
                this.overviewScreen.disable();
                this.setUpScreen.enable();

                this.renderSetUpScreen(matrixStack, mouseX, mouseY, partialTicks);
                this.updateSetUpScreen();

                this.ipField.active = true;
                this.passwordField.active = false;

                this.resetServerButton.visible = false;
                this.resetServerButton.active = false;
                this.saveServerButton.visible = false;
                this.saveServerButton.active = false;
                this.loadServerButton.visible = false;
                this.loadServerButton.active = false;
                this.savePasswordButton.visible = false;
                this.savePasswordButton.active = false;
                break;
            case OVERVIEW:
                this.overviewScreen.enable();
                this.setUpScreen.disable();

                this.renderOverviewScreen(matrixStack, mouseX, mouseY, partialTicks);
                this.updateOverviewScreen(mouseX, mouseY);

                this.ipField.active = false;
                this.passwordField.active = true;

                this.resetServerButton.visible = true;
                this.resetServerButton.active = true;
                this.saveServerButton.visible = true;
                this.saveServerButton.active = true;
                this.loadServerButton.visible = true;
                this.loadServerButton.active = true;
                this.savePasswordButton.visible = true;
                this.savePasswordButton.active = true;
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
    private TextFieldWidget passwordField;
    private Button onButton, offButton;
    private ImageButton resetServerButton, saveServerButton, loadServerButton, savePasswordButton;
    private Tooltip resetServerButtonTooltip, saveServerButtonTooltip, loadServerTooltip, savePasswordTooltip;
    private ConfirmPopUp confirmPopUp;
    private AlertPopUp alertPopUp;


    public void initOverviewScreen(){
        //add password text field
        this.passwordField = new TextFieldWidget(this.font, this.xPos + 70, this.yPos + 62, 60, 12, new StringTextComponent(""));
        this.passwordField.setCanLoseFocus(true);
        this.passwordField.setTextColor(Color.WHITE);
        this.passwordField.setMaxStringLength(20);
        this.children.add(this.passwordField);

        this.addButton(this.savePasswordButton = new ImageButton(this.xPos + 70 + 61 + 2, this.yPos + 59, 20, 19, 0, 0, 19, Texture.SAVE_BUTTON, (click)-> {
            this.server.setPassword(this.passwordField.getText());

            Network.INSTANCE.sendToServer(new CSPacketSendServerData(this.server, false));

            this.overviewScreen.setAlertPopUp(this.alertPopUp = new AlertPopUp(
                    this,
                    new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.title.save_password").getString(),
                    new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.content.save_password").getString(),
                    AlertPopUp.ColorType.DEFAULT));
        }));



        //danger area bottom
        this.addButton(this.resetServerButton = new ImageButton(this.xPos + 15, this.yPos + TEXTURE_HEIGHT - 18 - 10 - 22, 20, 18, 0, 0, 19, Texture.DELETE_BUTTON, (click)->{
            this.overviewScreen.setConfirmPopUp(this.confirmPopUp = new ConfirmPopUp(
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



        //right area
        this.overviewScreen.addVerticalLine(new VerticalLine(this.xPos + 175, this.yPos + 35, 100, Color.WHITE_HEX));

        this.addButton(this.saveServerButton = new ImageButton(this.xPos + 191, this.yPos + 41, 20, 18, 0, 0, 19, Texture.SAVE_BUTTON, (click)->{

            //check if drive is plugged in
            if(this.container.getTileEntity().getData().get(0) == 0){
                this.overviewScreen.setAlertPopUp(this.alertPopUp = new AlertPopUp(
                        this,
                        new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.title.no_drive_found").getString(),
                        new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.content.no_drive_found").getString(),
                        AlertPopUp.ColorType.NOTICE));
                return;
            }

            //check if drive has tag
            if(this.container.getTileEntity().getData().get(1) == 1){
                this.overviewScreen.setConfirmPopUp(this.confirmPopUp = new ConfirmPopUp(
                        this,
                        new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.title.drive_has_data").getString(),
                        new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.content.drive_has_data").getString(),
                        ConfirmPopUp.ColorType.NOTICE,
                        (yesAction)->{
                            Network.INSTANCE.sendToServer(new CSPacketSendServerData(this.server, true));

                            this.confirmPopUp.hide();
                            this.overviewScreen.setAlertPopUp(this.alertPopUp = new AlertPopUp(
                                    this,
                                    new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.title.backup_created").getString(),
                                    new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.content.backup_created").getString(),
                                    AlertPopUp.ColorType.DEFAULT));
                        }));
            }
            else{
                Network.INSTANCE.sendToServer(new CSPacketSendServerData(this.server, true));

                this.overviewScreen.setAlertPopUp(this.alertPopUp = new AlertPopUp(
                        this,
                        new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.title.backup_created").getString(),
                        new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.content.backup_created").getString(),
                        AlertPopUp.ColorType.DEFAULT));
            }


        }));

        this.addButton(this.loadServerButton = new ImageButton(this.xPos + 218, this.yPos + 41, 20, 18, 0, 0, 19, Texture.LOAD_BUTTON, (click)->{

            //check if drive is plugged in
            if(this.container.getTileEntity().getData().get(0) == 0){
                this.overviewScreen.setAlertPopUp(this.alertPopUp = new AlertPopUp(
                        this,
                        new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.title.no_drive_found").getString(),
                        new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.content.no_drive_found").getString(),
                        AlertPopUp.ColorType.NOTICE));
                return;
            }

            //check if drive has backup
            if(this.container.getTileEntity().getData().get(1) == 0){
                this.overviewScreen.setAlertPopUp(this.alertPopUp = new AlertPopUp(
                        this,
                        new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.title.drive_no_data").getString(),
                        new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.content.drive_no_data").getString(),
                        AlertPopUp.ColorType.ERROR));
                return;
            }

            this.overviewScreen.setConfirmPopUp(this.confirmPopUp = new ConfirmPopUp(
                    this,
                    new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.title.backup_overwrite").getString(),
                    new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.content.backup_overwrite").getString(),
                    ConfirmPopUp.ColorType.NOTICE,
                    (yesAction)->{
                        Network.INSTANCE.sendToServer(new CSPacketLoadBackup(this.server.getPos()));

                        this.confirmPopUp.hide();
                        this.overviewScreen.setAlertPopUp(this.alertPopUp = new AlertPopUp(
                                this,
                                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.title.backup_loaded").getString(),
                                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.content.backup_loaded").getString(),
                                AlertPopUp.ColorType.DEFAULT));

                    }));


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


        //tooltips
        this.overviewScreen.addTooltip(this.resetServerButtonTooltip = new Tooltip(new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".tooltip.reset_server").getString()));
        this.overviewScreen.addTooltip(this.loadServerTooltip = new Tooltip(new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".tooltip.load_from_drive").getString()));
        this.overviewScreen.addTooltip(this.saveServerButtonTooltip = new Tooltip(new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".tooltip.save_to_drive").getString()));
        this.overviewScreen.addTooltip(this.savePasswordTooltip = new Tooltip(new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".tooltip.save_password").getString()));

        /*
        ScrollableList list;
        this.overviewScreen.addList(list = new ScrollableList(0, 0, 100 ,100, 17));
        list.addToList("Test1", true, Color.DARK_GRAY_HEX, (c)->{});
        list.addToList("Test2", true, Color.DARK_GRAY_HEX,(c)->{});


        ScrollableTable table;
        this.overviewScreen.addTable(table = new ScrollableTable(0, 100, 100 ,100, 10, true));
        ArrayList<String> c1 = new ArrayList<>();
        c1.add("Name");
        c1.add("Alter");
        c1.add("Tot?");
        table.setAttributeColumns(c1);

        ArrayList<String> c2 = new ArrayList<>();
        c2.add("Berdi");
        c2.add("12");
        c2.add("x");
        table.addRow(c2, true, Color.DARK_GRAY_HEX, (c) ->{});

        ArrayList<String> c3 = new ArrayList<>();
        c3.add("Jaimy");
        c3.add("122");
        c3.add("Nein");
        table.addRow(c3, true, Color.DARK_GRAY_HEX, (c) ->{});

         */


    }

    private void renderOverviewScreen(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
        drawCenteredString(matrixStack, this.font, new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".heading.server_overview").getString(),
                (this.width / 2),
                (this.yPos + 20),
                Color.WHITE);

        this.passwordField.render(matrixStack, mouseX, mouseY, partialTicks);

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

    private boolean isPasswordInitialSet = false;
    private void updateOverviewScreen(int mouseX, int mouseY){

        if(!this.isPasswordInitialSet && this.server.isSet()){
            this.passwordField.setText(this.server.getPassword());
            this.isPasswordInitialSet = true;
        }

        if(!this.server.isOn()){
            this.onButton.active = true;
            this.offButton.active = false;
        }
        else{
            this.onButton.active = false;
            this.offButton.active = true;
        }

        if(this.confirmPopUp != null && !this.confirmPopUp.isHidden() || this.alertPopUp != null && !this.alertPopUp.isHidden()){
            this.onButton.active = false;
            this.offButton.active = false;
            this.resetServerButton.active = false;
            this.saveServerButton.active = false;
            this.loadServerButton.active = false;
            this.savePasswordButton.active = false;

            this.resetServerButtonTooltip.isVisible = false;
            this.saveServerButtonTooltip.isVisible = false;
            this.loadServerTooltip.isVisible = false;
            this.savePasswordTooltip.isVisible = false;
        }
        else{
            this.resetServerButton.active = true;
            this.saveServerButton.active = true;
            this.loadServerButton.active = true;
            this.savePasswordButton.active = true;

            this.resetServerButtonTooltip.isVisible = this.resetServerButton.isMouseOver(mouseX, mouseY);
            this.saveServerButtonTooltip.isVisible = this.saveServerButton.isMouseOver(mouseX, mouseY);
            this.loadServerTooltip.isVisible = this.loadServerButton.isMouseOver(mouseX, mouseY);
            this.savePasswordTooltip.isVisible = this.savePasswordButton.isMouseOver(mouseX, mouseY);
        }
    }

    //endregion

    //region SET UP SCREEN
    private TextFieldWidget ipField = new TextFieldWidget(this.font, 0 ,0 ,0 ,0 , new StringTextComponent(""));
    private DropDown typeDropDown = new DropDown(new String[0], 0, 0, 0, 0, "");
    private Button saveButton = new Button(0, 0, 0, 0, new StringTextComponent(""), (click)->{});

    public void initSetUpScreen(){
        this.setUpScreen.addCenteredHorizontalLine(new CenteredHorizontalLine(this.width, this.yPos + 32, 150, Color.WHITE_HEX));

        //add ip text field
        this.ipField = new TextFieldWidget(this.font, this.width / 2 - 45, this.yPos + 43, 90, 18, new StringTextComponent(""));
        this.ipField.setText(new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".placeholder.ip").getString());
        this.ipField.setCanLoseFocus(true);
        this.ipField.setTextColor(Color.WHITE);
        this.ipField.setMaxStringLength(15);
        this.children.add(this.ipField);

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
        }));
    }

    private void renderSetUpScreen(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){

        drawCenteredString(matrixStack, this.font, new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".heading.server_setup").getString(),
                (this.width / 2),
                (this.yPos + 20),
                Color.WHITE);

        this.ipField.render(matrixStack, mouseX, mouseY, partialTicks);
        this.setUpScreen.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void updateSetUpScreen(){
        this.saveButton.active = !this.ipField.getText().isEmpty() && !this.typeDropDown.getSelectedText().equals(this.typeDropDown.getPlaceholder());
    }
    //endregion

    //region INPUT FIELD METHODS
    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        this.ipField.setText(this.ipField.getText());
        this.passwordField.setText(this.passwordField.getText());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            assert this.minecraft != null;
            assert this.minecraft.player != null;
            this.minecraft.player.closeScreen();
        }

        if(this.ipField.active)
            return this.ipField.keyPressed(keyCode, scanCode, modifiers) || this.ipField.canWrite() || super.keyPressed(keyCode, scanCode, modifiers);
        else if(this.passwordField.active)
            return this.passwordField.keyPressed(keyCode, scanCode, modifiers) || this.passwordField.canWrite() || super.keyPressed(keyCode, scanCode, modifiers);

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void tick() {
        super.tick();
        this.ipField.tick();
        this.passwordField.tick();
    }
    //endregion

}
