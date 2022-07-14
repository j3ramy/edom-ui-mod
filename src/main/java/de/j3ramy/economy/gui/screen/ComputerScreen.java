package de.j3ramy.economy.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.j3ramy.economy.EconomyMod;
import de.j3ramy.economy.container.ComputerContainer;
import de.j3ramy.economy.gui.widgets.*;
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
    private final ModScreen screen;

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
    private final ResourceLocation PLUS_BUTTON = new ResourceLocation(EconomyMod.MOD_ID, "textures/gui/elements/plus_button_gui.png");
    private final ResourceLocation TRASHCAN_BUTTON = new ResourceLocation(EconomyMod.MOD_ID, "textures/gui/elements/trashcan_button_gui.png");
    private final ResourceLocation PEN_BUTTON = new ResourceLocation(EconomyMod.MOD_ID, "textures/gui/elements/pen_button_gui.png");

    private Server server = new Server(new CompoundNBT());
    public void setServer(Server server) {
        this.server = server;
    }


    public ComputerScreen(ComputerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);

        this.screen = new ModScreen();
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

        this.searchField = new TextFieldWidget(this.font, (this.guiLeft + 38) * 4/3, (this.yPos + 13) * 4/3, 80, 12, new StringTextComponent(""));
        this.searchField.setText(new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".placeholder.search").getString());
        this.searchField.setCanLoseFocus(true);
        this.searchField.setTextColor(Color.WHITE);
        this.children.add(this.searchField);

        this.screen.addList(this.tableList = new ScrollableList(this.xPos + 5, this.yPos + 46, 75, 95, 20));

        String[] columnNames = new String[1];
        this.screen.addTable(table = new ScrollableTable(this.xPos + 85, this.yPos + 46, 165, 95, 20, columnNames, true));
    }

    private void initButtons(){
        this.addButton(this.createTableButton = new ImageButton(this.xPos + 5, this.yPos + 23, 20, 18, 0, 0, 19, PLUS_BUTTON, (button) -> {
            this.createTable();
        }));

        this.addButton(this.dropTableButton = new ImageButton(this.xPos + 30, this.yPos + 23, 20, 18, 0, 0, 19, TRASHCAN_BUTTON, (button) -> {
            this.dropTable();
        }));

        this.addButton(this.createEntryButton = new ImageButton(this.xPos + 143, this.yPos + 13, 20, 18, 0, 0, 19, PLUS_BUTTON, (button) ->{
            this.createEntry();
        }));

        this.addButton(this.deleteEntryButton = new ImageButton(this.xPos + 168, this.yPos + 13, 20, 18, 0, 0, 19, TRASHCAN_BUTTON, (button) ->{
            this.deleteEntry();
        }));

        this.addButton(this.updateEntryButton = new ImageButton(this.xPos + 193, this.yPos + 13, 20, 18, 0, 0, 19, PEN_BUTTON, (button) ->{
            this.updateEntry();
        }));
    }

    private void initTooltips(){
        this.screen.addTooltip(createTableButtonTooltip = new Tooltip(this.getTranslationText("create_table")));
        this.screen.addTooltip(dropTableButtonTooltip = new Tooltip(this.getTranslationText("drop_table")));
        this.screen.addTooltip(createEntryButtonTooltip = new Tooltip(this.getTranslationText("create_entry")));
        this.screen.addTooltip(deleteEntryButtonTooltip = new Tooltip(this.getTranslationText("delete_entry")));
        this.screen.addTooltip(updateEntryButtonTooltip = new Tooltip(this.getTranslationText("update_entry")));
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

        GlStateManager.pushMatrix();
        GlStateManager.scalef(.75f, .75f, .75f);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, "IPAddress" + "/" + "DBName", (this.xPos + 5) * 4/3, (this.yPos + 13) * 4/3, Color.WHITE);
        this.searchField.render(matrixStack, mouseX, mouseY, partialTicks);
        GlStateManager.popMatrix();
        createTableButtonTooltip.isVisible = createTableButton.isHovered();
        dropTableButtonTooltip.isVisible = dropTableButton.isHovered();
        createEntryButtonTooltip.isVisible = createEntryButton.isHovered();
        deleteEntryButtonTooltip.isVisible = deleteEntryButton.isHovered();
        updateEntryButtonTooltip.isVisible = updateEntryButton.isHovered();
        this.screen.render(matrixStack, mouseX, mouseY, partialTicks);
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
