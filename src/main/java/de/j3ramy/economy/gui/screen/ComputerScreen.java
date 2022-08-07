package de.j3ramy.economy.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.j3ramy.economy.EconomyMod;
import de.j3ramy.economy.container.ComputerContainer;
import de.j3ramy.economy.gui.widgets.*;
import de.j3ramy.economy.gui.windows.Desktop;
import de.j3ramy.economy.utils.Color;
import de.j3ramy.economy.utils.GuiUtils;
import de.j3ramy.economy.utils.Texture;
import de.j3ramy.economy.utils.server.Entry;
import de.j3ramy.economy.utils.server.Server;
import de.j3ramy.economy.utils.server.Table;
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

public class ComputerScreen extends ContainerScreen<ComputerContainer> {
    private final int TEXTURE_WIDTH = 300;
    private final int TEXTURE_HEIGHT = 169;
    private final ModScreen tableOverviewScreen;
    private final ModScreen createTableScreen;
    private final ModScreen createEntryScreen;
    private final ModScreen updateEntryScreen;

    private int xLeft;
    private int xRight;
    private int yTop;
    private int yBottom;

    private ImageButton createTableButton;
    private ImageButton dropTableButton;
    private ImageButton createEntryButton;
    private ImageButton deleteEntryButton;
    private ImageButton updateEntryButton;
    //X und Y werden jeweils in der init Methode festgelegt
    private Button saveButton;
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
    private Taskbar taskbar;

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
        this.createTableScreen.clearScreen();
        this.createEntryScreen.clearScreen();
        this.updateEntryScreen.clearScreen();

        this.xLeft = (this.width / 2) - (TEXTURE_WIDTH / 2);
        this.xRight = (this.width / 2) + (TEXTURE_WIDTH / 2);
        this.yTop = (this.height / 2) - (TEXTURE_HEIGHT / 2);
        this.yBottom = (this.height / 2) + (TEXTURE_HEIGHT / 2);

        this.server = new Server(Server.DBType.CUSTOM, "Berdi's Leben ist eine Freude IP", this.container.getTileEntity().getPos());

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

        this.taskbar = new Taskbar(this.xLeft, this.yBottom - 14, TEXTURE_WIDTH, 15, true, true);
        this.taskbar.addToOsLogoList(new TranslationTextComponent("screen.economy.button.settings").getString(), ()->{
            this.taskbar.hideOsLogoList();
        });
        this.taskbar.addToOsLogoList(new TranslationTextComponent("screen.economy.button.logout").getString(), ()->{
            this.taskbar.hideOsLogoList();
        });

        //Cancel and Save Buttons for CreateTable, CreateEntry, UpdateEntry
        this.saveButton = new Button(xRight - 50 - 5, yBottom - 18 - 2 - 15, 50, 18, new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".button.save").getString(), ()->{
            switch(this.screenState){
                case CREATE_TABLE_SCREEN:
                    System.out.println("CREATE_TABLE_SCREEN");
                    this.createTableScreen.addWidget(new ProgressPopUp(0, 0, 175, 100, new TranslationTextComponent("screen.economy.popup.title.creating_table").getString(), 2, true, ()->{
                        this.screenState = ComputerScreenState.TABLE_OVERVIEW_SCREEN;
                    }));
                    break;
                case CREATE_ENTRY_SCREEN:
                    System.out.println("CREATE_ENTRY_SCREEN");
                    this.createEntryScreen.addWidget(new ProgressPopUp(0, 0, 175, 100, new TranslationTextComponent("screen.economy.popup.title.creating_entry").getString(), 2, true, ()->{
                        this.screenState = ComputerScreenState.TABLE_OVERVIEW_SCREEN;
                    }));
                    break;
                case UPDATE_ENTRY_SCREEN:
                    System.out.println("UPDATE_ENTRY_SCREEN");
                    this.updateEntryScreen.addWidget(new ProgressPopUp(0, 0, 175, 100, new TranslationTextComponent("screen.economy.popup.title.updating_entry").getString(), 2, true, ()->{
                        this.screenState = ComputerScreenState.TABLE_OVERVIEW_SCREEN;
                    }));
                    break;
            }
        });

        this.cancelButton = new Button(xRight - 50 - 5 - 50 - 5, yBottom - 18 - 2 - 15, 50, 18, new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".button.cancel").getString(), ()->{
            this.screenState = ComputerScreenState.TABLE_OVERVIEW_SCREEN;
        });

        initTableOverviewScreen();
        initCreateTableScreen();
        initCreateEntryScreen();
        initUpdateEntryScreen();

        for (Table table : this.server.getDatabase().getTables()){
            this.tableList.addElement(table.getName(), true, Color.DARK_GRAY_HEX, Color.LIGHT_GRAY_HEX, ()->{
                this.table.clear();
                this.deleteEntryButton.active = false;
                this.updateEntryButton.active = false;
                this.table.setAttributeRow((ArrayList<String>) table.getColumnNames(), Color.ORANGE_HEX, Color.ORANGE_HEX);
                for (Entry entry : table.getAllEntries()){
                    this.table.addRow(entry.getColumnsContent(), true, Color.DARK_GRAY_HEX, Color.LIGHT_GRAY_HEX, ()->{
                        this.deleteEntryButton.active = true;
                        this.updateEntryButton.active = true;
                    });
                }
                this.dropTableButton.active = true;
            });
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        this.playerInventoryTitleX = 1000;

        new Desktop(TEXTURE_WIDTH, TEXTURE_HEIGHT).render(matrixStack);
        AbstractGui.fill(matrixStack, xLeft, yTop, xRight, yTop + 8, Color.DARK_GRAY_HEX);

        super.render(matrixStack, mouseX, mouseY, partialTicks);

        //For every Screen
        //Heading
        GlStateManager.pushMatrix();
        GlStateManager.scalef(.5f, .5f, .5f);
        String titleText = new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".heading.computer").getString();
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, titleText + " | " + this.server.getIp() + "/" + this.server.getDatabase().getName() + " | " + GuiUtils.formatTime(this.container.getTileEntity().getWorld().getDayTime()), (this.xLeft + 2) * 2, (this.yTop + 2) * 2, Color.WHITE);
        GlStateManager.popMatrix();

        //Individual Screens
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
                break;
            case UPDATE_ENTRY_SCREEN:
                this.renderUpdateEntryScreen(matrixStack, mouseX, mouseY, partialTicks);
                this.updateUpdateEntryScreen();
                break;
        }
    }

    //region TABLE_OVERVIEW_SCREEN
    public void initTableOverviewScreen() {
        //<Buttons>
        this.tableOverviewScreen.addMcWidget(this.createTableButton = new ImageButton(this.xLeft + 2, this.yTop + 11, 20, 18, 0, 0, 19, Texture.PLUS_BUTTON, (button) -> {
            this.screenState = ComputerScreenState.CREATE_TABLE_SCREEN;
        }));

        this.tableOverviewScreen.addMcWidget(this.dropTableButton = new ImageButton(this.xLeft + 27, this.yTop + 11, 20, 18, 0, 0, 19, Texture.DELETE_BUTTON, (button) -> {
            this.tableOverviewScreen.addWidget(this.confirmDropTable = new ConfirmPopUp(
                    0, 0, 175, 100,
                    new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.title.drop_table").getString(),
                    new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.content.drop_table").getString(),
                    ConfirmPopUp.ColorType.DEFAULT,
                    ()-> {
                        this.tableOverviewScreen.addWidget(new ProgressPopUp(0, 0, 175, 100, new TranslationTextComponent("screen.economy.popup.title.dropping_table").getString(), 2, true, ()->{
                            this.confirmDropTable.setHidden(true);
                        }));
                    }));
        }));
        dropTableButton.active = false;

        this.tableOverviewScreen.addMcWidget(this.createEntryButton = new ImageButton(this.xRight - 2 - 25 - 25 -20, this.yTop + 11, 20, 18, 0, 0, 19, Texture.PLUS_BUTTON, (button) ->{
            this.screenState = ComputerScreenState.CREATE_ENTRY_SCREEN;
            this.hideTableOverviewScreen();
        }));

        this.tableOverviewScreen.addMcWidget(this.deleteEntryButton = new ImageButton(this.xRight - 2 - 25 - 20, this.yTop + 11, 20, 18, 0, 0, 19, Texture.DELETE_BUTTON, (button) ->{
            this.tableOverviewScreen.addWidget(this.confirmDeleteEntry = new ConfirmPopUp(
                    0, 0, 175, 100,
                    new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.title.delete_entry").getString(),
                    new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".popup.content.delete_entry").getString(),
                    ConfirmPopUp.ColorType.DEFAULT,
                    ()->{
                        this.tableOverviewScreen.addWidget(new ProgressPopUp(0, 0, 175, 100, new TranslationTextComponent("screen.economy.popup.title.deleting_entry").getString(), 2, true, ()->{
                            this.confirmDeleteEntry.setHidden(true);
                        }));
                    }));
        }));
        this.deleteEntryButton.active = false;

        this.tableOverviewScreen.addMcWidget(this.updateEntryButton = new ImageButton(this.xRight - 2 - 20, this.yTop + 11, 20, 18, 0, 0, 19, Texture.PEN_BUTTON, (button) ->{
            this.screenState = ComputerScreenState.UPDATE_ENTRY_SCREEN;
            this.hideTableOverviewScreen();
        }));
        this.updateEntryButton.active = false;
        //</Buttons>

        //Search field
        tableOverviewScreen.addMcWidget(this.searchField = new TextFieldWidget(this.font, (this.xLeft + 82), (this.yTop + 14), 80, 12, new StringTextComponent("")));
        this.searchField.setText(new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".placeholder.search").getString());
        this.searchField.setCanLoseFocus(true);
        this.searchField.setTextColor(Color.WHITE);

        //List of Entries in Table
        this.tableOverviewScreen.addWidget(table = new ScrollableTable(this.xLeft + 82, this.yTop + 34, this.xRight - 2 - (this.xLeft + 82), 115, 10, true));

        //List of tables
        this.tableOverviewScreen.addWidget(this.tableList = new ScrollableList(this.xLeft + 2, this.yTop + 34, 75, 115, 10));

        //Taskbar
        this.tableOverviewScreen.addWidget(this.taskbar);

        //<s>
        this.tableOverviewScreen.addWidget(createTableButtonTooltip = new Tooltip(GuiUtils.getTranslationText("create_table"), this.createTableButton));
        this.tableOverviewScreen.addWidget(dropTableButtonTooltip = new Tooltip(GuiUtils.getTranslationText("drop_table"), this.dropTableButton));
        this.tableOverviewScreen.addWidget(createEntryButtonTooltip = new Tooltip(GuiUtils.getTranslationText("create_entry"), this.createEntryButton));
        this.tableOverviewScreen.addWidget(deleteEntryButtonTooltip = new Tooltip(GuiUtils.getTranslationText("delete_entry"), this.deleteEntryButton));
        this.tableOverviewScreen.addWidget(updateEntryButtonTooltip = new Tooltip(GuiUtils.getTranslationText("update_entry"), this.updateEntryButton));
        //</Tooltips>

    }

    private void renderTableOverviewScreen(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.tableOverviewScreen.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void updateTableOverviewScreen(){
        this.showTableOverviewScreen();
        this.hideCreateTableScreen();
        this.hideCreateEntryScreen();
        this.hideUpdateEntryScreen();
    }

    private void showTableOverviewScreen(){
        this.tableOverviewScreen.enable();
    }

    private void hideTableOverviewScreen(){
        this.tableOverviewScreen.disable();

        this.tableList.clearSelectedIndex();
        this.table.clearSelectedIndex();
        this.table.clear();
    }

    //endregion

    //region CREATE_TABLE_SCREEN
    private void initCreateTableScreen() {
        this.createTableScreen.addWidget(cancelButton);
        this.createTableScreen.addWidget(saveButton);
        this.createTableScreen.addWidget(this.taskbar);
    }

    private void renderCreateTableScreen(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.createTableScreen.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void updateCreateTableScreen(){
        this.showCreateTableScreen();
        this.hideTableOverviewScreen();
        this.hideCreateEntryScreen();
        this.hideUpdateEntryScreen();
    }

    private void showCreateTableScreen(){
        this.createTableScreen.enable();
    }

    private void hideCreateTableScreen(){
        this.createTableScreen.disable();
    }

    //endregion

    //region CREATE_ENTRY_SCREEN
    private void initCreateEntryScreen() {
        this.createEntryScreen.addWidget(cancelButton);
        this.createEntryScreen.addWidget(saveButton);
        this.createEntryScreen.addWidget(this.taskbar);
    }
    private void renderCreateEntryScreen(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.createEntryScreen.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void updateCreateEntryScreen() {
        this.showCreateEntryScreen();
        this.hideTableOverviewScreen();
        this.hideCreateTableScreen();
        this.hideUpdateEntryScreen();
    }

    private void showCreateEntryScreen() {
        this.createEntryScreen.enable();
    }

    private void hideCreateEntryScreen() {
        this.createEntryScreen.disable();
    }

    //endregion

    //region UPDATE_ENTRY_SCREEN

    private void initUpdateEntryScreen() {
        this.updateEntryScreen.addWidget(cancelButton);
        this.updateEntryScreen.addWidget(saveButton);
        this.updateEntryScreen.addWidget(this.taskbar);
    }

    private void renderUpdateEntryScreen(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.updateEntryScreen.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void updateUpdateEntryScreen() {
        this.showUpdateEntryScreen();
        this.hideTableOverviewScreen();
        this.hideCreateTableScreen();
        this.hideCreateEntryScreen();
    }

    private void showUpdateEntryScreen() {
        updateEntryScreen.enable();
    }

    private void hideUpdateEntryScreen() {
        updateEntryScreen.disable();
    }

    //endregion

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(Texture.BLANK_GUI);

        this.blit(matrixStack, this.xLeft, this.yTop, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
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
