package de.j3ramy.economy.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.j3ramy.economy.EconomyMod;
import de.j3ramy.economy.container.ComputerContainer;
import de.j3ramy.economy.gui.widgets.*;
import de.j3ramy.economy.utils.GuiUtils;
import de.j3ramy.economy.utils.Texture;
import de.j3ramy.economy.utils.ingame.server.Server;
import de.j3ramy.economy.utils.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.tools.Tool;

public class ComputerScreen extends ContainerScreen<ComputerContainer> {

    private final ResourceLocation GUI = new ResourceLocation(EconomyMod.MOD_ID, "textures/gui/screen_gui.png");
    private final int TEXTURE_WIDTH = 256;
    private final int TEXTURE_HEIGHT = 148;
    private final ModScreen tableOverviewScreen;
    private final ModScreen screen2;

    private int xPos;
    private int yPos;

    private ImageButton createTableButton;
    private ImageButton dropTableButton;
    private ImageButton createEntryButton;
    private ImageButton deleteEntryButton;
    private ImageButton updateEntryButton;
    private TextFieldWidget searchField;
    private ScrollableList tableList;
    private ScrollableTable table;
    private Tooltip createTableButtonTooltip;
    private Tooltip dropTableButtonTooltip;
    private Tooltip createEntryButtonTooltip;
    private Tooltip deleteEntryButtonTooltip;
    private Tooltip updateEntryButtonTooltip;
    private Tooltip viewNameTooltip;

    private Server server = new Server(new CompoundNBT());
    public void setServer(Server server) {
        this.server = server;
    }

    private enum ComputerScreenState{
        TABLE_OVERVIEW_SCREEN,
        SCREEN2
    }

    private ComputerScreenState screenState = ComputerScreenState.TABLE_OVERVIEW_SCREEN;

    public ComputerScreen(ComputerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);

        this.tableOverviewScreen = new ModScreen();
        this.screen2 = new ModScreen();
    }


    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);

        this.xPos = (this.width / 2) - (TEXTURE_WIDTH / 2);
        this.yPos = this.height / 2 - 75;

/*        this.screen.buttons.add(this.button = new Button(this.xPos + 10, this.yPos, 100, 20, new TranslationTextComponent("screen.economy.button.save"), (onclick)->{
            System.out.println("Selected Option: " + this.dropDown.getSelectedText());
        }));

        String[] options = new String[]{"Option A", "Option B", "Option C"};
        this.screen.dropDowns.add(this.dropDown = new DropDown(options, this.xPos, this.yPos + 50, 100, 20, "Choose Option"));*/

        initButtons();
        initTooltips();

        //Search field
        this.searchField = new TextFieldWidget(this.font, (this.xPos + 85), (this.yPos + 16), 80, 12, new StringTextComponent(""));
        this.searchField.setText(new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".placeholder.search").getString());
        this.searchField.setCanLoseFocus(true);
        this.searchField.setTextColor(Color.WHITE);
        this.children.add(this.searchField);

        //List of tables
        this.tableOverviewScreen.addList(this.tableList = new ScrollableList(this.xPos + 5, this.yPos + 36, 75, 105, 20));

        //List of Entries in Table
        String[] columnNames = new String[1];
        this.tableOverviewScreen.addTable(table = new ScrollableTable(this.xPos + 85, this.yPos + 36, 165, 105, 20, columnNames, true));
    }

    private void initButtons(){
        this.addButton(this.createTableButton = new ImageButton(this.xPos + 5, this.yPos + 13, 20, 18, 0, 0, 19, Texture.PLUS_BUTTON, (button) -> {
            this.createTable();
        }));

        this.addButton(this.dropTableButton = new ImageButton(this.xPos + 30, this.yPos + 13, 20, 18, 0, 0, 19, Texture.DELETE_BUTTON, (button) -> {
            this.dropTable();
        }));

        this.addButton(this.createEntryButton = new ImageButton(this.xPos + 180, this.yPos + 13, 20, 18, 0, 0, 19, Texture.PLUS_BUTTON, (button) ->{
            this.createEntry();
        }));

        this.addButton(this.deleteEntryButton = new ImageButton(this.xPos + 205, this.yPos + 13, 20, 18, 0, 0, 19, Texture.DELETE_BUTTON, (button) ->{
            this.deleteEntry();
        }));

        this.addButton(this.updateEntryButton = new ImageButton(this.xPos + 230, this.yPos + 13, 20, 18, 0, 0, 19, Texture.PEN_BUTTON, (button) ->{
            this.updateEntry();
        }));
    }

    private void initTooltips(){
        this.tableOverviewScreen.addTooltip(createTableButtonTooltip = new Tooltip(this.getTranslationText("create_table")));
        this.tableOverviewScreen.addTooltip(dropTableButtonTooltip = new Tooltip(this.getTranslationText("drop_table")));
        this.tableOverviewScreen.addTooltip(createEntryButtonTooltip = new Tooltip(this.getTranslationText("create_entry")));
        this.tableOverviewScreen.addTooltip(deleteEntryButtonTooltip = new Tooltip(this.getTranslationText("delete_entry")));
        this.tableOverviewScreen.addTooltip(updateEntryButtonTooltip = new Tooltip(this.getTranslationText("update_entry")));
    }

    private String getTranslationText(String translationKey){
        return new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".tooltip." + translationKey).getString();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        this.playerInventoryTitleX = 1000;

//        //ohne Schatten
//        Minecraft.getInstance().fontRenderer.drawString(matrixStack, "Test Text", (this.xPos + 10), (this.yPos + 100), Color.RED);
//        //mit Schatten
//        drawString(matrixStack, this.font, "Test Text", (this.xPos + 10), (this.yPos + 120), Color.RED);
//
//        //skalierten Text
//        GlStateManager.pushMatrix();
//        GlStateManager.scalef(.5f, .5f, .5f);
//        Minecraft.getInstance().fontRenderer.drawString(matrixStack, "Test Text", (this.xPos + 30) * 2, (this.yPos + 100) * 2, Color.GREEN);
//        GlStateManager.popMatrix();
//
//        this.update();

        //draw title heading
        GlStateManager.pushMatrix();
        GlStateManager.scalef(.5f, .5f, .5f);
        String titleText = new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".heading.computer").getString();
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, titleText + " | " + "IPAdress" + "/" + "DBName" + " | " + GuiUtils.formatTime(this.container.getTileEntity().getWorld().getDayTime()), (this.xPos + 4) * 2, (this.yPos + 4) * 2, Color.WHITE);
        GlStateManager.popMatrix();

        this.searchField.render(matrixStack, mouseX, mouseY, partialTicks);
        createTableButtonTooltip.isVisible = createTableButton.isHovered();
        dropTableButtonTooltip.isVisible = dropTableButton.isHovered();
        createEntryButtonTooltip.isVisible = createEntryButton.isHovered();
        deleteEntryButtonTooltip.isVisible = deleteEntryButton.isHovered();
        updateEntryButtonTooltip.isVisible = updateEntryButton.isHovered();
        this.tableOverviewScreen.render(matrixStack, mouseX, mouseY, partialTicks);
    }

/*    private void update(){
        if(this.button == null || this.dropDown == null)
            return;

        //if-else
        if(this.dropDown.isOptionSelected()){
            this.button.isDisabled(false);
        }
        else{
            this.button.isDisabled(true);
        }

        //simplified "if-else"
        this.button.isDisabled(!this.dropDown.isOptionSelected());

        //ternary operator
        int i = (this.dropDown.isOptionSelected()) ? 1 : 0;
    }*/

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(GUI);

        this.blit(matrixStack, this.xPos, this.yPos, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    private void createTable() {
    }

    private void dropTable() {
    }

    private void createEntry() {
    }

    private void deleteEntry() {
    }

    private void updateEntry() {
    }
}
