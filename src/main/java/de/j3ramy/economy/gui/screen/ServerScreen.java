package de.j3ramy.economy.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.j3ramy.economy.EconomyMod;
import de.j3ramy.economy.container.ServerContainer;
import de.j3ramy.economy.gui.widgets.*;
import de.j3ramy.economy.network.CSPacketSendServerData;
import de.j3ramy.economy.network.Network;
import de.j3ramy.economy.utils.Color;
import de.j3ramy.economy.utils.GuiUtils;
import de.j3ramy.economy.utils.Texture;
import de.j3ramy.economy.utils.ingame.server.Server;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Objects;

public class ServerScreen extends ContainerScreen<ServerContainer> {
    private final ResourceLocation GUI = new ResourceLocation(EconomyMod.MOD_ID, "textures/gui/screen_inv_gui.png");
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
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, titleText + " | " + ((this.screenState != ServerScreenState.SET_UP) ? this.server.getIp() : "")
                + " | " + GuiUtils.formatTime(this.container.getTileEntity().getWorld().getDayTime()),
                        (this.xPos + 4) * 2, (this.yPos + 4) * 2, Color.WHITE);
        GlStateManager.popMatrix();

        //draw screen
        switch(this.screenState){
            case SET_UP:
                this.renderSetUpScreen(matrixStack, mouseX, mouseY, partialTicks);
                this.updateSetUpScreen();
                this.ipField.render(matrixStack, mouseX, mouseY, partialTicks);
                break;
            case OVERVIEW:
                this.renderOverviewScreen(matrixStack, mouseX, mouseY, partialTicks);
                this.updateOverviewScreen(mouseX, mouseY);
                break;

        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        assert this.minecraft != null;

        this.minecraft.getTextureManager().bindTexture(GUI);
        this.blit(matrixStack, this.xPos, this.yPos, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    //region OVERVIEW SCREEN
    private Button onButton;
    private Button offButton;
    private ImageButton clearDatabaseButton;
    private Tooltip clearDatabaseButtonTooltip;
    private ImageButton resetServerButton;
    private Tooltip resetServerButtonTooltip;
    private ImageButton saveServerButton;
    private Tooltip saveServerButtonTooltip;
    private ImageButton loadServerButton;
    private Tooltip loadServerTooltip;
    private ConfirmPopUp confirmPopUp;

    public void initOverviewScreen(){
        this.overviewScreen.addButton(this.onButton = new Button(this.xPos + TEXTURE_WIDTH - 50 - 10, this.yPos + TEXTURE_HEIGHT - 18 - 15 - 22 - 18, 50, 18,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".button.on"), (click)->{
            this.server.setOn(true);
            Network.INSTANCE.sendToServer(new CSPacketSendServerData(this.server));
        }));

        this.overviewScreen.addButton(this.offButton = new Button(this.xPos + TEXTURE_WIDTH - 50 - 10, this.yPos + TEXTURE_HEIGHT - 18 - 10 - 22, 50, 18,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".button.off"), (click)->{
            this.server.setOn(false);
            Network.INSTANCE.sendToServer(new CSPacketSendServerData(this.server));
        }));

        //right area
        this.overviewScreen.addVerticalLine(new VerticalLine(this.xPos + 175, this.yPos + 35, 100, Color.WHITE_HEX));

        //tooltips
        this.overviewScreen.addTooltip(this.clearDatabaseButtonTooltip = new Tooltip(new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".tooltip.clear_db_button").getString()));
        this.overviewScreen.addTooltip(this.resetServerButtonTooltip = new Tooltip(new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".tooltip.reset_server").getString()));
        this.overviewScreen.addTooltip(this.loadServerTooltip = new Tooltip(new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".tooltip.load_from_drive").getString()));
        this.overviewScreen.addTooltip(this.saveServerButtonTooltip = new Tooltip(new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".tooltip.save_to_drive").getString()));


        this.overviewScreen.addConfirmPopUp(confirmPopUp = new ConfirmPopUp(this, (click) ->{
            confirmPopUp.hide();
            System.out.println("YES");
        }));

        confirmPopUp.setTitle("Do you want to...?");
        confirmPopUp.setContent("It really is dangeorus!!!!");
        confirmPopUp.setColorType(ConfirmPopUp.ColorType.DEFAULT);
        confirmPopUp.show();
    }


    private void renderOverviewScreen(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
        this.buttons.clear();

        drawCenteredString(matrixStack, this.font, new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".heading.server_overview").getString(),
                (this.width / 2),
                (this.yPos + 20),
                Color.WHITE);

        //stats
        this.drawGeneralInfo(matrixStack);
        this.drawStats(matrixStack);

        //danger area bottom
        this.addButton(this.clearDatabaseButton = new ImageButton(this.xPos + 15, this.yPos + TEXTURE_HEIGHT - 18 - 10 - 22, 20, 18, 0, 0, 19, Texture.CLEAR_DB_BUTTON, (click)->{
            System.out.println("CLICK");
        }));

        this.addButton(this.resetServerButton = new ImageButton(this.xPos + 40, this.yPos + TEXTURE_HEIGHT - 18 - 10 - 22, 20, 18, 0, 0, 19, Texture.DELETE_BUTTON, (click)->{
            System.out.println("CLICK");
        }));

        //right area
        AbstractGui.fill(matrixStack, this.xPos + TEXTURE_WIDTH - 50 - 20, this.yPos + TEXTURE_HEIGHT - 18 - 15 - 18 - 22,
                this.xPos + TEXTURE_WIDTH - 50 - 15, this.yPos + TEXTURE_HEIGHT - 10 - 22, this.server.isOn() ? Color.GREEN_HEX : Color.RED_HEX);

        this.addButton(this.saveServerButton = new ImageButton(this.xPos + 191, this.yPos + 57, 20, 18, 0, 0, 19, Texture.SAVE_BUTTON, (click)->{
            System.out.println("CLICK");
        }));

        this.addButton(this.loadServerButton = new ImageButton(this.xPos + 218, this.yPos + 57, 20, 18, 0, 0, 19, Texture.LOAD_BUTTON, (click)->{
            System.out.println("CLICK");
        }));

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
                (int) ((this.yPos + 48) * GuiUtils.getScalingPositionMultiplier(.5f)),
                Color.WHITE);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack,
                this.server.getIp(),
                (int) ((this.xPos + 70) * GuiUtils.getScalingPositionMultiplier(.5f)),
                (int) ((this.yPos + 48) * GuiUtils.getScalingPositionMultiplier(.5f)),
                Color.WHITE);

        Minecraft.getInstance().fontRenderer.drawString(matrixStack,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".label.password").getString(),
                (int) ((this.xPos + 15) * GuiUtils.getScalingPositionMultiplier(.5f)),
                (int) ((this.yPos + 55) * GuiUtils.getScalingPositionMultiplier(.5f)),
                Color.WHITE);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack,
                "PW",
                (int) ((this.xPos + 70) * GuiUtils.getScalingPositionMultiplier(.5f)),
                (int) ((this.yPos + 55) * GuiUtils.getScalingPositionMultiplier(.5f)),
                Color.WHITE);

        Minecraft.getInstance().fontRenderer.drawString(matrixStack,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".label.db_name").getString(),
                (int) ((this.xPos + 15) * GuiUtils.getScalingPositionMultiplier(.5f)),
                (int) ((this.yPos + 62) * GuiUtils.getScalingPositionMultiplier(.5f)),
                Color.WHITE);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack,
                this.server.getDatabase().getName(),
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
                (int) ((this.yPos + 93) * GuiUtils.getScalingPositionMultiplier(.5f)),
                Color.WHITE);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack,
                Integer.toString(this.server.getDatabase().getTableCount()),
                (int) ((this.xPos + 70) * GuiUtils.getScalingPositionMultiplier(.5f)),
                (int) ((this.yPos + 93) * GuiUtils.getScalingPositionMultiplier(.5f)),
                Color.WHITE);

        Minecraft.getInstance().fontRenderer.drawString(matrixStack,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".label.amount_entries").getString(),
                (int) ((this.xPos + 15) * GuiUtils.getScalingPositionMultiplier(.5f)),
                (int) ((this.yPos + 101) * GuiUtils.getScalingPositionMultiplier(.5f)),
                Color.WHITE);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack,
                Integer.toString(this.server.getDatabase().getTotalEntryCount()),
                (int) ((this.xPos + 70) * GuiUtils.getScalingPositionMultiplier(.5f)),
                (int) ((this.yPos + 101) * GuiUtils.getScalingPositionMultiplier(.5f)),
                Color.WHITE);

        Minecraft.getInstance().fontRenderer.drawString(matrixStack,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".label.total_accesses").getString(),
                (int) ((this.xPos + 15) * GuiUtils.getScalingPositionMultiplier(.5f)),
                (int) ((this.yPos + 109) * GuiUtils.getScalingPositionMultiplier(.5f)),
                Color.WHITE);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack,
                Integer.toString(this.server.getAccesses()),
                (int) ((this.xPos + 70) * GuiUtils.getScalingPositionMultiplier(.5f)),
                (int) ((this.yPos + 109) * GuiUtils.getScalingPositionMultiplier(.5f)),
                Color.WHITE);

        GlStateManager.popMatrix();
    }

    private void updateOverviewScreen(int mouseX, int mouseY){
        if(this.server.isOn()){
            this.onButton.active = true;
            this.offButton.active = false;
        }
        else{
            this.onButton.active = false;
            this.offButton.active = true;
        }

        if(!this.confirmPopUp.isHidden()){
            this.onButton.active = false;
            this.offButton.active = false;
            this.clearDatabaseButton.active = false;
            this.resetServerButton.active = false;
            this.saveServerButton.active = false;
            this.loadServerButton.active = false;
        }
        else{
            this.onButton.active = true;
            this.offButton.active = true;
            this.clearDatabaseButton.active = true;
            this.resetServerButton.active = true;
            this.saveServerButton.active = true;
            this.loadServerButton.active = true;

            this.clearDatabaseButtonTooltip.isVisible = this.clearDatabaseButton.isMouseOver(mouseX, mouseY);
            this.resetServerButtonTooltip.isVisible = this.resetServerButton.isMouseOver(mouseX, mouseY);
            this.saveServerButtonTooltip.isVisible = this.saveServerButton.isMouseOver(mouseX, mouseY);
            this.loadServerTooltip.isVisible = this.loadServerButton.isMouseOver(mouseX, mouseY);
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

            server.initDatabase("db_" + this.typeDropDown.getSelectedText().toLowerCase());

            this.setServer(server);
            Network.INSTANCE.sendToServer(new CSPacketSendServerData(this.server));

            this.screenState = ServerScreenState.OVERVIEW;
        }));
    }

    private void renderSetUpScreen(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
        this.setUpScreen.render(matrixStack, mouseX, mouseY, partialTicks);
        drawCenteredString(matrixStack, this.font, new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".heading.server_setup").getString(),
                (this.width / 2),
                (this.yPos + 20),
                Color.WHITE);
    }

    private void updateSetUpScreen(){
        this.saveButton.active = this.ipField.getText().isEmpty() || this.typeDropDown.getSelectedText().equals(this.typeDropDown.getPlaceholder());
    }
    //endregion

    //region INPUT FIELD METHODS
    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        this.ipField.setText(this.ipField.getText());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            assert this.minecraft != null;
            assert this.minecraft.player != null;
            this.minecraft.player.closeScreen();
        }

        return (this.ipField.keyPressed(keyCode, scanCode, modifiers) || this.ipField.canWrite()) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void tick() {
        super.tick();
        this.ipField.tick();
    }
    //endregion

}
