package de.j3ramy.economy.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.j3ramy.economy.EconomyMod;
import de.j3ramy.economy.container.ServerContainer;
import de.j3ramy.economy.gui.ModScreen;
import de.j3ramy.economy.gui.elements.Button;
import de.j3ramy.economy.gui.elements.CenteredHorizontalLine;
import de.j3ramy.economy.gui.elements.DropDown;
import de.j3ramy.economy.gui.elements.ScrollableList;
import de.j3ramy.economy.network.CSPacketSendCreditCardData;
import de.j3ramy.economy.network.CSPacketSendServerData;
import de.j3ramy.economy.network.Network;
import de.j3ramy.economy.utils.Color;
import de.j3ramy.economy.utils.Server;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ServerScreen extends ContainerScreen<ServerContainer> {

    private final ResourceLocation GUI = new ResourceLocation(EconomyMod.MOD_ID, "textures/gui/server_gui.png");
    private final ModScreen screen;

    public ModScreen getScreen() {
        return this.screen;
    }

    private final int TEXTURE_WIDTH = 256;
    private final int TEXTURE_HEIGHT = 148;

    public int xOffset;
    public int yOffset;

    private enum ServerScreenState{
        SET_UP,
        OVERVIEW,
        CREATE_TABLE,
        VIEW_TABLE,
    }

    private ServerScreenState screenState;
    private Server server;
    public void setServer(Server server) {
        this.server = server;
    }

    //private PopUpWindow popUpWindow;
    private TextFieldWidget ipField;
    private DropDown typeDropDown;
    private Button saveButton;
    private ScrollableList scrollableList;


    public ServerScreen(ServerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);

        this.screen = new ModScreen(titleIn);
    }


    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);

        this.xOffset = (this.width / 2) - (TEXTURE_WIDTH / 2);
        this.yOffset = this.height / 2 - 75;

        if(this.server == null)
            this.screenState = ServerScreenState.SET_UP;
        else
            this.screenState = ServerScreenState.OVERVIEW;



        //add popup window once
        //this.screen.popUpWindows.add(this.popUpWindow = new PopUpWindow(this));

        this.initSetUpScreen();
    }

    public void initSetUpScreen(){
        this.screen.centeredHorizontalLines.add(new CenteredHorizontalLine(this.width, this.yOffset + 32, 150, Color.WHITE_HEX));

        //add ip text field
        this.ipField = new TextFieldWidget(this.font, this.width / 2 - 45, this.yOffset + 43, 90, 18, new StringTextComponent(""));
        this.ipField.setText(new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".placeholder.ip").getString());
        this.ipField.setCanLoseFocus(true);
        this.ipField.setTextColor(Color.WHITE);
        this.ipField.setMaxStringLength(15);
        this.children.add(this.ipField);

        String[] options = new String[Server.ServerType.values().length];
        for(int i = 0; i < Server.ServerType.values().length; i++){
            options[i] = Server.ServerType.values()[i].toString();
        }
        this.screen.dropDowns.add(this.typeDropDown = new DropDown(options, this.width / 2 - 45, this.yOffset + 70, 90, 18, "Preset"));

        /*
        this.screen.scrollableList.add(this.scrollableList = new ScrollableList( 10, 100, 100, 60, 20));
        this.scrollableList.addToList("Test1sssssssssssssssssssssssss", true, (onClick) -> {System.out.println("Test1");});
        this.scrollableList.addToList("Test2", false, (onClick) -> {System.out.println("Test2");});
        this.scrollableList.addToList("Test3", true, (onClick) -> {System.out.println("Test3");});
        this.scrollableList.addToList("Test4", true, (onClick) -> {System.out.println("Test4");});
        this.scrollableList.addToList("Test5", true, (onClick) -> {System.out.println("Test5");});
        this.scrollableList.addToList("Test6", true, (onClick) -> {System.out.println("Test6");});
        this.scrollableList.addToList("Test7", true, (onClick) -> {System.out.println("Test7");});
        this.scrollableList.addToList("Test8", true, (onClick) -> {System.out.println("Test8");});

         */

        this.screen.buttons.add(this.saveButton = new Button(0, this.width / 2 - 30, this.yOffset + 100, 60, 14,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".button.save"), (onPress) ->{

            Server server = new Server(
                    Server.ServerType.valueOf(this.typeDropDown.getSelectedText()),
                    this.ipField.getText().replace(' ', '_').toLowerCase(),
                    this.container.tileEntity.getPos(),
                    "db_" + this.typeDropDown.getSelectedText().toLowerCase());

            this.setServer(server);
            Network.INSTANCE.sendToServer(new CSPacketSendServerData(this.server, this.container.tileEntity.getPos()));

            this.screenState = ServerScreenState.OVERVIEW;
        }));

    }

    private void unloadSetUpScreen(){
        this.screen.clearScreen();
        this.ipField.visible = false;

    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.playerInventoryTitleX = 1000;

        //Draw server heading
        GlStateManager.pushMatrix();
        GlStateManager.scalef(.5f, .5f, .5f);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".heading.server").getString(),
                (this.xOffset + 4) * 2,
                (this.yOffset + 4) * 2,
                Color.WHITE);
        GlStateManager.popMatrix();

        switch(this.screenState){
            case SET_UP:
                this.renderSetUpScreen(matrixStack);
                this.updateSetUpScreen();
                break;
            case OVERVIEW:
                this.unloadSetUpScreen();


        }

        //render text field widgets
        this.renderTextFieldWidgets(matrixStack, mouseX, mouseY, partialTicks);
        //render mod screen class
        this.screen.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(GUI);

        this.blit(matrixStack, this.xOffset, this.yOffset, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    private void renderSetUpScreen(MatrixStack matrixStack){
        drawCenteredString(matrixStack, this.font, new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".heading.server_setup").getString(),
                (this.width / 2),
                (this.yOffset + 20),
                Color.WHITE);
    }

    private void updateSetUpScreen(){
        this.saveButton.isDisabled(this.ipField.getText().isEmpty() || this.typeDropDown.getSelectedText().equals(this.typeDropDown.getPlaceholder()));
    }


    //region INPUT FIELD METHODS
    private void renderTextFieldWidgets(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.ipField.render(matrixStack, mouseX, mouseY, partialTicks);
    }

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

        return (this.ipField.keyPressed(keyCode, scanCode, modifiers) || this.ipField.canWrite()) ||
                super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void tick() {
        super.tick();
        this.ipField.tick();
    }
    //endregion

}
