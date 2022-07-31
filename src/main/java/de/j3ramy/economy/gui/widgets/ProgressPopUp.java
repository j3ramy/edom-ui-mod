package de.j3ramy.economy.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.j3ramy.economy.utils.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.util.text.StringTextComponent;

import java.util.Random;

public class ProgressPopUp extends Screen {

    public final int WIDTH = 150;
    public final int HEIGHT = 90;

    private final int leftPos;
    private final int topPos;
    private final ContainerScreen<?> screen;
    private final String title;
    private boolean isHidden;
    private final int duration;
    private final IFinished finishedAction;
    private final ProgressBar progressBar;
    private final boolean shouldProgressStop;

    public interface IFinished {
        void onFinished(ProgressPopUp progressPopUp);
    }

    public ProgressPopUp(ContainerScreen<?> screen, String title, int duration, boolean shouldProgressStop, IFinished finishedAction){
        super(new StringTextComponent(""));

        this.leftPos = screen.width / 2 - WIDTH / 2;
        this.topPos = screen.height / 2 - HEIGHT / 2;

        this.screen = screen;
        this.title = title;
        this.duration = duration;
        this.finishedAction = finishedAction;
        this.progressBar = new ProgressBar(screen.width / 2 - 50, this.topPos + 40, 100, 12, Color.DARK_GRAY_HEX, Color.GREEN_HEX);
        this.shouldProgressStop = shouldProgressStop;

        if(this.shouldProgressStop)
            this.initProgressStop();
    }

    float drawStopStart = new Random().nextFloat();
    float drawStopEnd = new Random().nextFloat();
    private void initProgressStop(){
        while(drawStopEnd < drawStopStart)
            drawStopEnd = new Random().nextFloat();
    }

    public boolean isHidden() {
        return this.isHidden;
    }

    private void hide(){
        this.isHidden = true;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
        if(this.isHidden)
            return;

        screen.renderBackground(matrixStack);

        AbstractGui.fill(matrixStack, leftPos, topPos, leftPos + WIDTH, topPos + HEIGHT, Color.DARK_GRAY_HEX);

        //background
        int CONTENT_MARGIN = 5;
        int BACKGROUND_COLOR = Color.LIGHT_GRAY_HEX;
        AbstractGui.fill(matrixStack, leftPos + CONTENT_MARGIN, topPos + CONTENT_MARGIN + 10,
                leftPos + WIDTH - CONTENT_MARGIN, topPos + CONTENT_MARGIN + HEIGHT - 10,
                BACKGROUND_COLOR);

        //title text
        AbstractGui.drawCenteredString(matrixStack, screen.getMinecraft().fontRenderer, this.title, screen.width / 2, topPos + 4, Color.WHITE);

        this.drawProgressBar(matrixStack);
        this.update();
    }

    private void drawProgressBar(MatrixStack matrixStack){
        this.progressBar.render(matrixStack);

        if(this.progressBar.isFull()){
            this.finishedAction.onFinished(this);
            this.hide();
        }
    }


    private void update(){
        this.progressBar.setProgress(this.duration);
        this.progressBar.setBarStopped(this.shouldProgressStop && this.progressBar.progress > this.drawStopStart && this.progressBar.progress < this.drawStopEnd);
    }

    private class ProgressBar{

        private final int xPos;
        private final int yPos;
        private final int width;
        private final int height;
        private int backgroundColor;
        private int barColor;
        private float progress;
        private final float maxProgress = 1f;
        private boolean isBarStopped;

        public ProgressBar(int x, int y, int width, int height, int backgroundColor, int barColor){
            this.xPos = x;
            this.yPos = y;
            this.width = width;
            this.height = height;
            this.backgroundColor = backgroundColor;
            this.barColor = barColor;
        }

        private float progressStop;
        public void render(MatrixStack matrixStack){

            AbstractGui.fill(matrixStack, this.xPos - 1, this.yPos - 1, this.xPos + this.width + 1, this.yPos + this.height + 1, this.backgroundColor);

            float widthProgress = this.progress / this.maxProgress * this.width;
            if(!this.isBarStopped){
                this.progressStop = widthProgress;
            }

            AbstractGui.fill(matrixStack, this.xPos, this.yPos, (int) (this.xPos + (this.isBarStopped ? this.progressStop : widthProgress)),
                    this.yPos + this.height, this.barColor);
        }

        public float getMaxProgress() {
            return this.maxProgress;
        }

        public void setBarStopped(boolean barStopped) {
            this.isBarStopped = barStopped;
        }

        public int getWidth() {
            return this.width;
        }

        public void setProgress(int duration){
            int frameRate = Minecraft.getInstance().gameSettings.framerateLimit;
            this.progress += this.maxProgress / duration / frameRate;
        }

        public boolean isFull(){
            return this.progress >= this.maxProgress;
        }

        public void setBarColor(int barColor) {
            this.barColor = barColor;
        }

        public void setBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
        }
    }
}

