package de.j3ramy.economy.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.j3ramy.economy.EconomyMod;
import de.j3ramy.economy.container.ComputerContainer;
import de.j3ramy.economy.gui.widgets.ScrollableList;
import de.j3ramy.economy.gui.widgets.ScrollableTable;
import de.j3ramy.economy.gui.widgets.Tooltip;
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
        content2.add("Berdi2");
        content2.add("123456");
        content2.add("Nein");
        content2.add("Viel Geld");
        this.server.getDatabase().getTable("Table2").insert(new Entry(content2));

        initTableOverviewScreen();
        initScreen2();
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
            case SCREEN2:
                this.renderScreen2(matrixStack, mouseX, mouseY, partialTicks);
                this.updateScreen2();
                break;
        }
    }

    //region TABLE_OVERVIEW_SCREEN
    public void initTableOverviewScreen() {
        //<Buttons>
        this.addButton(this.createTableButton = new ImageButton(this.xPos + 5, this.yPos + 13, 20, 18, 0, 0, 19, Texture.PLUS_BUTTON, (button) -> {
            this.screenState = ComputerScreenState.SCREEN2;
        }));

        this.addButton(this.dropTableButton = new ImageButton(this.xPos + 30, this.yPos + 13, 20, 18, 0, 0, 19, Texture.DELETE_BUTTON, (button) -> {

        }));

        this.addButton(this.createEntryButton = new ImageButton(this.xPos + 180, this.yPos + 13, 20, 18, 0, 0, 19, Texture.PLUS_BUTTON, (button) ->{

        }));

        this.addButton(this.deleteEntryButton = new ImageButton(this.xPos + 205, this.yPos + 13, 20, 18, 0, 0, 19, Texture.DELETE_BUTTON, (button) ->{

        }));

        this.addButton(this.updateEntryButton = new ImageButton(this.xPos + 230, this.yPos + 13, 20, 18, 0, 0, 19, Texture.PEN_BUTTON, (button) ->{

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
        this.tableOverviewScreen.addList(this.tableList = new ScrollableList(this.xPos + 5, this.yPos + 36, 75, 105, 20));

        //List of Entries in Table
        this.tableOverviewScreen.addTable(table = new ScrollableTable(this.xPos + 85, this.yPos + 36, 165, 105, 20, true));
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

    //endregion

    //region SCREEN2
    private void initScreen2() {

    }

    private void renderScreen2(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.screen2.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void updateScreen2(){
        this.createTableButton.visible = false;
        this.dropTableButton.visible = false;
        this.createEntryButton.visible = false;
        this.deleteEntryButton.visible = false;
        this.updateEntryButton.visible = false;
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
