package de.j3ramy.economy.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.j3ramy.economy.EconomyMod;
import de.j3ramy.economy.container.ServerContainer;
import de.j3ramy.economy.gui.ModScreen;
import de.j3ramy.economy.gui.elements.Button;
import de.j3ramy.economy.gui.elements.CenteredHorizontalLine;
import de.j3ramy.economy.gui.elements.DropDown;
import de.j3ramy.economy.network.CSPacketSendServerData;
import de.j3ramy.economy.network.Network;
import de.j3ramy.economy.utils.ingame.server.Entry;
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

import java.util.ArrayList;

public class ServerScreen extends ContainerScreen<ServerContainer> {

    private final ResourceLocation GUI = new ResourceLocation(EconomyMod.MOD_ID, "textures/gui/server_gui.png");
    private final ModScreen setUpScreen;
    private final ModScreen overviewScreen;

    public ModScreen getSetUpScreen() {
        return this.setUpScreen;
    }
    public ModScreen getOverviewScreen() {
        return this.overviewScreen;
    }

    private final int TEXTURE_WIDTH = 256;
    private final int TEXTURE_HEIGHT = 148;

    public int xOffset;
    public int yOffset;

    private enum ServerScreenState{
        SET_UP,
        OVERVIEW,
    }

    private ServerScreenState screenState = ServerScreenState.SET_UP;
    private Server server = new Server(new CompoundNBT());
    public void setServer(Server server) {
        this.server = server;
    }


    private TextFieldWidget ipField = new TextFieldWidget(this.font, 0 ,0 ,0 ,0 , new StringTextComponent(""));
    private DropDown typeDropDown = new DropDown(new String[0], 0, 0, 0, 0, "");
    private Button saveButton = new Button(0, 0, 0, 0, 0, new StringTextComponent(""), (click)->{});


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
                System.out.println("Server: " + this.server.getIp());
                System.out.println("DB: " + this.server.getDatabase().getName());
                System.out.println("Table: " + this.server.getDatabase().getTable("Table1").getName() + " | Entry: " + this.server.getDatabase().getTable("Table1").getEntry(0).getColumnsContent());
                System.out.println("Table: " + this.server.getDatabase().getTable("Table1").getName() + " | Entry: " + this.server.getDatabase().getTable("Table1").getEntry(1).getColumnsContent());
                System.out.println("Table: " + this.server.getDatabase().getTable("Table2").getName() + " | Entry: " + this.server.getDatabase().getTable("Table2").getEntry(0).getColumnsContent());
                System.out.println("----------------------------------------------------------");
                break;

        }

        //render mod screen class
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(GUI);

        this.blit(matrixStack, this.xOffset, this.yOffset, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }


    //region SET UP SCREEN
    public void initSetUpScreen(){
        this.setUpScreen.centeredHorizontalLines.add(new CenteredHorizontalLine(this.width, this.yOffset + 32, 150, Color.WHITE_HEX));

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
        this.setUpScreen.dropDowns.add(this.typeDropDown = new DropDown(options, this.width / 2 - 45, this.yOffset + 70, 90, 18, "Preset"));

        /*
        this.screen.scrollableList.add(this.scrollableList = new ScrollableList( 10, 100, 100, 60, 20));
        this.scrollableList.addToList("Test1", true, (onClick) -> {System.out.println("Test1");});
        this.scrollableList.addToList("Test2", false, (onClick) -> {System.out.println("Test2");});
        this.scrollableList.addToList("Test3", true, (onClick) -> {System.out.println("Test3");});
        this.scrollableList.addToList("Test4", true, (onClick) -> {System.out.println("Test4");});
        this.scrollableList.addToList("Test5", true, (onClick) -> {System.out.println("Test5");});
        this.scrollableList.addToList("Test6", true, (onClick) -> {System.out.println("Test6");});
        this.scrollableList.addToList("Test7", true, (onClick) -> {System.out.println("Test7");});
        this.scrollableList.addToList("Test8", true, (onClick) -> {System.out.println("Test8");});

         */

        this.setUpScreen.buttons.add(this.saveButton = new Button(0, this.width / 2 - 30, this.yOffset + 100, 60, 14,
                new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".button.save"), (onPress) ->{

            Server server = new Server(
                    Server.ServerType.valueOf(this.typeDropDown.getSelectedText()),
                    this.ipField.getText().replace(' ', '_').toLowerCase(),
                    this.container.tileEntity.getPos());

            server.initDatabase("db_" + this.typeDropDown.getSelectedText().toLowerCase());


            ArrayList<String> attributes = new ArrayList<>();
            attributes.add("Vorname");
            attributes.add("Nachname");
            attributes.add("Alter");
            server.getDatabase().createTable("Table1", attributes);

            ArrayList<String> columnContent = new ArrayList<>();
            columnContent.add("Jaimy");
            columnContent.add("Seidel");
            columnContent.add("22");
            server.getDatabase().getTable("Table1").insert(new Entry(columnContent));

            ArrayList<String> columnContent2 = new ArrayList<>();
            columnContent2.add("Robert");
            columnContent2.add("Eberhard");
            columnContent2.add("90");
            server.getDatabase().getTable("Table1").insert(new Entry(columnContent2));

            ArrayList<String> attributes1 = new ArrayList<>();
            attributes1.add("Kontostand");
            attributes1.add("Kontonummer");
            attributes1.add("Inhaber");
            server.getDatabase().createTable("Table2", attributes1);

            ArrayList<String> columnContent1 = new ArrayList<>();
            columnContent1.add("38,00 €");
            columnContent1.add("123456789");
            columnContent1.add("Jaimy Seidel");
            server.getDatabase().getTable("Table2").insert(new Entry(columnContent1));

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
