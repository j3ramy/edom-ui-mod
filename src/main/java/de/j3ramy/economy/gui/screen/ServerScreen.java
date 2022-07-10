package de.j3ramy.economy.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.j3ramy.economy.EconomyMod;
import de.j3ramy.economy.container.ServerContainer;
import de.j3ramy.economy.gui.elements.Button;
import de.j3ramy.economy.gui.elements.CenteredHorizontalLine;
import de.j3ramy.economy.gui.elements.DropDown;
import de.j3ramy.economy.network.CSPacketSendServerData;
import de.j3ramy.economy.network.Network;
import de.j3ramy.economy.utils.ingame.server.Server;
import de.j3ramy.economy.utils.screen.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ServerScreen extends ContainerScreen<ServerContainer> {

    private final ResourceLocation GUI = new ResourceLocation(EconomyMod.MOD_ID, "textures/gui/server_gui.png");
    private final ModScreen setUpScreen;
    private final ModScreen overviewScreen;
    private final int TEXTURE_WIDTH = 256;
    private final int TEXTURE_HEIGHT = 148;

    public int xOffset;
    public int yOffset;

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

        this.setUpScreen = new ModScreen(titleIn);
        this.overviewScreen = new ModScreen(titleIn);
    }


    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);

        this.xOffset = (this.width / 2) - (TEXTURE_WIDTH / 2);
        this.yOffset = this.height / 2 - 75;

        this.initSetUpScreen();
        this.initOverviewScreen();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.playerInventoryTitleX = 1000;

        this.screenState = this.server.isSet() ? ServerScreenState.OVERVIEW : ServerScreenState.SET_UP;

        //Draw server title heading
        GlStateManager.pushMatrix();
        GlStateManager.scalef(.5f, .5f, .5f);
        String titleText = new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".heading.server").getString();
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, titleText + ((this.screenState != ServerScreenState.SET_UP) ? " | " + this.server.getIp() : ""),
                (this.xOffset + 4) * 2, (this.yOffset + 4) * 2, Color.WHITE);
        GlStateManager.popMatrix();

        switch(this.screenState){
            case SET_UP:
                this.renderSetUpScreen(matrixStack, mouseX, mouseY, partialTicks);
                this.updateSetUpScreen();
                this.ipField.render(matrixStack, mouseX, mouseY, partialTicks);
                break;
            case OVERVIEW:
                this.renderOverviewScreen(matrixStack, mouseX, mouseY, partialTicks);
                this.updateOverviewScreen();
                break;

        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(GUI);

        this.blit(matrixStack, this.xOffset, this.yOffset, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    //region SET UP SCREEN
    public void initOverviewScreen(){

    }

    private void renderOverviewScreen(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
        this.overviewScreen.render(matrixStack, mouseX, mouseY, partialTicks);

        /*
        this.setUpScreen.render(matrixStack, mouseX, mouseY, partialTicks);
        drawCenteredString(matrixStack, this.font, new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".heading.server_setup").getString(),
                (this.width / 2),
                (this.yOffset + 20),
                Color.WHITE);

         */

    }

    private void updateOverviewScreen(){

    }
    //endregion

    //region SET UP SCREEN
    private TextFieldWidget ipField = new TextFieldWidget(this.font, 0 ,0 ,0 ,0 , new StringTextComponent(""));
    private DropDown typeDropDown = new DropDown(new String[0], 0, 0, 0, 0, "");
    private Button saveButton = new Button(0, 0, 0, 0, 0, new StringTextComponent(""), (click)->{});

    public void initSetUpScreen(){
        this.setUpScreen.centeredHorizontalLines.add(new CenteredHorizontalLine(this.width, this.yOffset + 32, 150, Color.WHITE_HEX));

        //add ip text field
        this.ipField = new TextFieldWidget(this.font, this.width / 2 - 45, this.yOffset + 43, 90, 18, new StringTextComponent(""));
        this.ipField.setText(new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".placeholder.ip").getString());
        this.ipField.setCanLoseFocus(true);
        this.ipField.setTextColor(Color.WHITE);
        this.ipField.setMaxStringLength(15);
        this.children.add(this.ipField);

        String[] options = new String[Server.DBType.values().length];
        for(int i = 0; i < Server.DBType.values().length; i++){
            options[i] = Server.DBType.values()[i].toString();
        }
        this.setUpScreen.dropDowns.add(this.typeDropDown = new DropDown(options, this.width / 2 - 45, this.yOffset + 70, 90, 18, "Preset"));

        this.setUpScreen.buttons.add(this.saveButton = new Button(0, this.width / 2 - 30, this.yOffset + 100, 60, 14,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".button.save"), (onPress) ->{

            Server server = new Server(
                    Server.DBType.valueOf(this.typeDropDown.getSelectedText()),
                    this.ipField.getText().replace(' ', '_').toLowerCase(),
                    this.container.tileEntity.getPos());

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
                (this.yOffset + 20),
                Color.WHITE);
    }

    private void updateSetUpScreen(){
        this.saveButton.isDisabled(this.ipField.getText().isEmpty() || this.typeDropDown.getSelectedText().equals(this.typeDropDown.getPlaceholder()));
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
