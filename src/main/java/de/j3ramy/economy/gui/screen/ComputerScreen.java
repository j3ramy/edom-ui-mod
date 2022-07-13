package de.j3ramy.economy.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.j3ramy.economy.EconomyMod;
import de.j3ramy.economy.container.ComputerContainer;
import de.j3ramy.economy.gui.widgets.Button;
import de.j3ramy.economy.gui.widgets.DropDown;
import de.j3ramy.economy.utils.ingame.server.Server;
import de.j3ramy.economy.utils.screen.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ComputerScreen extends ContainerScreen<ComputerContainer> {

    private final ResourceLocation GUI = new ResourceLocation(EconomyMod.MOD_ID, "textures/gui/screen_gui.png");
    private final int TEXTURE_WIDTH = 256;
    private final int TEXTURE_HEIGHT = 148;
    private final ModScreen screen;

    private int xPos;
    private int yPos;

    private DropDown dropDown;
    private Button button;


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

        this.screen.addButton(this.button = new Button(this.xPos + 10, this.yPos, 100, 20, new TranslationTextComponent("screen.economy.button.save"), (onclick)->{
            System.out.println("Selected Option: " + this.dropDown.getSelectedText());
        }));

        String[] options = new String[]{"Option A", "Option B", "Option C"};
        this.screen.addDropDown(this.dropDown = new DropDown(options, this.xPos, this.yPos + 50, 100, 20, "Choose Option"));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        this.playerInventoryTitleX = 1000;

        //ohne Schatten
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, "Test Text", (this.xPos + 10), (this.yPos + 100), Color.RED);
        //mit Schatten
        drawString(matrixStack, this.font, "Test Text", (this.xPos + 10), (this.yPos + 120), Color.RED);

        //skalierten Text
        GlStateManager.pushMatrix();
        GlStateManager.scalef(.5f, .5f, .5f);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, "Test Text", (this.xPos + 30) * 2, (this.yPos + 100) * 2, Color.GREEN);
        GlStateManager.popMatrix();

        this.update();
        this.screen.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void update(){
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
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(GUI);

        this.blit(matrixStack, this.xPos, this.yPos, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }
}
