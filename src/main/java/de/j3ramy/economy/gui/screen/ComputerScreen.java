package de.j3ramy.economy.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.j3ramy.economy.EconomyMod;
import de.j3ramy.economy.container.ComputerContainer;
import de.j3ramy.economy.gui.widgets.*;
import de.j3ramy.economy.utils.Color;
import de.j3ramy.economy.utils.GuiUtils;
import de.j3ramy.economy.utils.Texture;
import de.j3ramy.economy.utils.ingame.server.Entry;
import de.j3ramy.economy.utils.ingame.server.Server;
import de.j3ramy.economy.utils.ingame.server.Table;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.LoadingGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.swing.plaf.ProgressBarUI;
import java.util.ArrayList;

public class ComputerScreen extends ContainerScreen<ComputerContainer> {
    private final int TEXTURE_WIDTH = 256;
    private final int TEXTURE_HEIGHT = 148;
    private final ModScreen tableOverviewScreen;
    private final ModScreen createTableScreen;
    private final ModScreen createEntryScreen;
    private final ModScreen updateEntryScreen;

    private int xPos;
    private int yPos;

    private ImageButton createTableButton;
    private ImageButton dropTableButton;
    private ImageButton createEntryButton;
    private ImageButton deleteEntryButton;
    private ImageButton updateEntryButton;
    private Button cancelButton;
    private TextFieldWidget searchField;
    private ScrollableList tableList;
    private ScrollableTable table;
    private Tooltip createTableButtonTooltip;
    private Tooltip dropTableButtonTooltip;
    private Tooltip createEntryButtonTooltip;
    private Tooltip deleteEntryButtonTooltip;
    private Tooltip updateEntryButtonTooltip;
    private ConfirmPopUp confirmDropTable;
    private ConfirmPopUp confirmDeleteEntry;

    private Server server = new Server(new CompoundNBT());
    public void setServer(Server server) {
        this.server = server;
    }

    private enum ComputerScreenState{
        TABLE_OVERVIEW_SCREEN,
        CREATE_TABLE_SCREEN,
        CREATE_ENTRY_SCREEN,
        UPDATE_ENTRY_SCREEN
    }

    private ComputerScreenState screenState = ComputerScreenState.TABLE_OVERVIEW_SCREEN;

    public ComputerScreen(ComputerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);

        this.tableOverviewScreen = new ModScreen();
        this.createTableScreen = new ModScreen();
        this.createEntryScreen = new ModScreen();
        this.updateEntryScreen = new ModScreen();
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);

        this.tableOverviewScreen.clearScreen();

        this.xPos = (this.width / 2) - (TEXTURE_WIDTH / 2);
        this.yPos = this.height / 2 - 75;

        this.server = new Server(Server.DBType.CUSTOM, "Berdi's Leben ist eine Freude IP", this.container.getTileEntity().getPos());
        this.server.initDatabase("db_berdiisthuebsch.de");

        ArrayList<String> columns1 = new ArrayList<>();
        columns1.add("Name");
        columns1.add("Alter");
        columns1.add("Geburtsdatum");
        this.server.getDatabase().createTable("Table1", columns1);

        ArrayList<String> columns2 = new ArrayList<>();
        columns2.add("Benutzername");
        columns2.add("Password");
        columns2.add("Hoffnung");
        columns2.add("Wunsch");
        this.server.getDatabase().createTable("Table2", columns2);

        ArrayList<String> content1 = new ArrayList<>();
        content1.add("Berdi");
        content1.add("34");
        content1.add("12.01.2019");
        this.server.getDatabase().getTable("Table1").insert(new Entry(content1));
        content1.set(0, "Jaimy");
        this.server.getDatabase().getTable("Table1").insert(new Entry(content1));

        ArrayList<String> content2 = new ArrayList<>();
        content2.add("Berdä2");
        content2.add("123456");
        content2.add("Nein");
        content2.add("Viel Geld");
        this.server.getDatabase().getTable("Table2").insert(new Entry(content2));

        initTableOverviewScreen();
        initCreateTableScreen();
        initCreateEntryScreen();
        initUpdateEntryScreen();

        for (Table table : this.server.getDatabase().getTables()){
            this.tableList.addToList(table.getName(), true, this.tableList.getFGColor(), (onClick)->{
                this.table.clear();
                this.table.setAttributeColumns((ArrayList<String>) table.getColumnNames());
                this.dropTableButton.active = true;
                for (Entry entry : table.getAllEntries()){
                    this.table.addRow((ArrayList<String>) entry.getColumnsContent(), true, this.table.getFGColor(), (onClick2)->{this.deleteEntryButton.active = true;});
                }
            });
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.playerInventoryTitleX = 1000;

        //heading
        GlStateManager.pushMatrix();
        GlStateManager.scalef(.5f, .5f, .5f);
        String titleText = new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".heading.computer").getString();
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, titleText + " | " + this.server.getIp() + "/" + this.server.getDatabase().getName() + " | " + GuiUtils.formatTime(this.container.getTileEntity().getWorld().getDayTime()), (this.xPos + 4) * 2, (this.yPos + 4) * 2, Color.WHITE);
        GlStateManager.popMatrix();

        //draw screens
        switch(this.screenState){
            case TABLE_OVERVIEW_SCREEN:
                this.renderTableOverviewScreen(matrixStack, mouseX, mouseY, partialTicks);
                this.updateTableOverviewScreen();
                break;
            case CREATE_TABLE_SCREEN:
                this.renderCreateTableScreen(matrixStack, mouseX, mouseY, partialTicks);
                this.updateCreateTableScreen();
                break;
            case CREATE_ENTRY_SCREEN:
                this.renderCreateEntryScreen(matrixStack, mouseX, mouseY, partialTicks);
                this.updateCreateEntryScreen();
            case UPDATE_ENTRY_SCREEN:
                this.renderUpdateEntryScreen(matrixStack, mouseX, mouseY, partialTicks);
                this.updateUpdateEntryScreen();
        }
    }

    //region TABLE_OVERVIEW_SCREEN
    public void initTableOverviewScreen() {
        //<Buttons>
        this.addButton(this.createTableButton = new ImageButton(this.xPos + 5, this.yPos + 13, 20, 18, 0, 0, 19, Texture.PLUS_BUTTON, (button) -> {
            this.screenState = ComputerScreenState.CREATE_TABLE_SCREEN;
            this.hideTableOverviewScreen();
        }));

        this.addButton(this.dropTableButton = new ImageButton(this.xPos + 30, this.yPos + 13, 20, 18, 0, 0, 19, Texture.DELETE_BUTTON, (button) -> {
            this.tableOverviewScreen.addConfirmPopUp(confirmDropTable = new ConfirmPopUp(this, (onYesClick)->{
                confirmDropTable.hide();
            }));
            this.confirmDropTable.setTitle(new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.title.drop_table").getString());
            this.confirmDropTable.setContent(new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.content.drop_table").getString());
            this.confirmDropTable.setColorType(ConfirmPopUp.ColorType.DEFAULT);
            this.confirmDropTable.show();
        }));
        dropTableButton.active = false;

        this.addButton(this.createEntryButton = new ImageButton(this.xPos + 180, this.yPos + 13, 20, 18, 0, 0, 19, Texture.PLUS_BUTTON, (button) ->{
            this.screenState = ComputerScreenState.CREATE_ENTRY_SCREEN;
            this.hideTableOverviewScreen();
        }));

        this.addButton(this.deleteEntryButton = new ImageButton(this.xPos + 205, this.yPos + 13, 20, 18, 0, 0, 19, Texture.DELETE_BUTTON, (button) ->{
            this.tableOverviewScreen.addConfirmPopUp(confirmDeleteEntry = new ConfirmPopUp(this, (onYesClick)->{
                confirmDeleteEntry.hide();
            }));
            this.confirmDeleteEntry.setTitle(new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.title.delete_entry").getString());
            this.confirmDeleteEntry.setContent(new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.content.delete_entry").getString());
            this.confirmDeleteEntry.setColorType(ConfirmPopUp.ColorType.DEFAULT);
            this.confirmDeleteEntry.show();
        }));
        this.deleteEntryButton.active = false;

        this.addButton(this.updateEntryButton = new ImageButton(this.xPos + 230, this.yPos + 13, 20, 18, 0, 0, 19, Texture.PEN_BUTTON, (button) ->{
            this.screenState = ComputerScreenState.UPDATE_ENTRY_SCREEN;
            hideTableOverviewScreen();
        }));
        //</Buttons>

        //<Tooltips>
        this.tableOverviewScreen.addTooltip(createTableButtonTooltip = new Tooltip(GuiUtils.getTranslationText("create_table")));
        this.tableOverviewScreen.addTooltip(dropTableButtonTooltip = new Tooltip(GuiUtils.getTranslationText("drop_table")));
        this.tableOverviewScreen.addTooltip(createEntryButtonTooltip = new Tooltip(GuiUtils.getTranslationText("create_entry")));
        this.tableOverviewScreen.addTooltip(deleteEntryButtonTooltip = new Tooltip(GuiUtils.getTranslationText("delete_entry")));
        this.tableOverviewScreen.addTooltip(updateEntryButtonTooltip = new Tooltip(GuiUtils.getTranslationText("update_entry")));
        //</Tooltips>

        //Search field
        this.searchField = new TextFieldWidget(this.font, (this.xPos + 85), (this.yPos + 16), 80, 12, new StringTextComponent(""));
        this.searchField.setText(new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".placeholder.search").getString());
        this.searchField.setCanLoseFocus(true);
        this.searchField.setTextColor(Color.WHITE);
        this.children.add(this.searchField);

        //List of tables
        this.tableOverviewScreen.addList(this.tableList = new ScrollableList(this.xPos + 5, this.yPos + 36, 75, 105, 13));

        //List of Entries in Table
        this.tableOverviewScreen.addTable(table = new ScrollableTable(this.xPos + 85, this.yPos + 36, 165, 105, 10, true));
    }

    private void renderTableOverviewScreen(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.searchField.render(matrixStack, mouseX, mouseY, partialTicks);
        this.tableOverviewScreen.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void updateTableOverviewScreen(){
        this.createTableButtonTooltip.isVisible = createTableButton.isHovered();
        this.dropTableButtonTooltip.isVisible = dropTableButton.isHovered();
        this.createEntryButtonTooltip.isVisible = createEntryButton.isHovered();
        this.deleteEntryButtonTooltip.isVisible = deleteEntryButton.isHovered();
        this.updateEntryButtonTooltip.isVisible = updateEntryButton.isHovered();
    }

    private void showTableOverviewScreen(){
        this.createTableButton.visible = true;
        this.dropTableButton.visible = true;
        this.createEntryButton.visible = true;
        this.deleteEntryButton.visible = true;
        this.updateEntryButton.visible = true;

        this.createTableButton.active = true;
        this.dropTableButton.active = true;
        this.createEntryButton.active = true;
        this.deleteEntryButton.active = true;
        this.updateEntryButton.active = true;
    }

    private void hideTableOverviewScreen(){
        this.createTableButton.visible = false;
        this.dropTableButton.visible = false;
        this.createEntryButton.visible = false;
        this.deleteEntryButton.visible = false;
        this.updateEntryButton.visible = false;

        this.createTableButton.active = false;
        this.dropTableButton.active = false;
        this.createEntryButton.active = false;
        this.deleteEntryButton.active = false;
        this.updateEntryButton.active = false;
    }

    //endregion

    //region CREATE_TABLE_SCREEN
    private void initCreateTableScreen() {
        this.createTableScreen.addButton(cancelButton = new Button(xPos + 200, yPos + 80, 50, 18, new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".button.cancle"), (onClick)->{
            this.screenState = ComputerScreenState.TABLE_OVERVIEW_SCREEN;
            showTableOverviewScreen();
            System.out.println("Clicked");
        }));
    }

    private void renderCreateTableScreen(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.createTableScreen.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void updateCreateTableScreen(){

    }

    private void showCreateTableScreen(){
        this.createTableButton.visible = true;

        this.createTableButton.active = true;
        this.dropTableButton.active = true;
        this.createEntryButton.active = true;
        this.deleteEntryButton.active = true;
        this.updateEntryButton.active = true;
    }

    private void hideCreateTableScreen(){
        this.createTableButton.visible = false;
        this.dropTableButton.visible = false;
        this.createEntryButton.visible = false;
        this.deleteEntryButton.visible = false;
        this.updateEntryButton.visible = false;

        this.createTableButton.active = false;
        this.dropTableButton.active = false;
        this.createEntryButton.active = false;
        this.deleteEntryButton.active = false;
        this.updateEntryButton.active = false;
    }

    //endregion

    //region CREATE_ENTRY_SCREEN
    private void initCreateEntryScreen() {
    }
    private void renderCreateEntryScreen(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.createEntryScreen.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void updateCreateEntryScreen() {
    }

    //endregion

    //region UPDATE_ENTRY_SCREEN

    private void initUpdateEntryScreen() {
    }

    private void renderUpdateEntryScreen(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.updateEntryScreen.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void updateUpdateEntryScreen() {
    }

    //endregion

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(Texture.SCREEN_GUI);

        this.blit(matrixStack, this.xPos, this.yPos, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            assert this.minecraft != null;
            assert this.minecraft.player != null;
            this.minecraft.player.closeScreen();
        }

        return (this.searchField.keyPressed(keyCode, scanCode, modifiers) || this.searchField.canWrite()) ||
                super.keyPressed(keyCode, scanCode, modifiers);
    }
}
