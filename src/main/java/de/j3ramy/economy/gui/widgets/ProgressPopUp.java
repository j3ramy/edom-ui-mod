package de.j3ramy.economy.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.j3ramy.economy.utils.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;

import java.util.Random;

public class ProgressPopUp extends Widget {
    private final String title;
    private final int duration;
    private final IFinished finishedAction;
    private final ProgressBar progressBar;
    private final boolean shouldProgressStop;

    public interface IFinished {
        void onFinished();
    }


    public ProgressPopUp(int x, int y, int width, int height, String title, int duration, boolean shouldProgressStop, IFinished finishedAction){
        super(x, y, width, height);

        this.title = title;
        this.duration = duration;
        this.finishedAction = finishedAction;
        this.progressBar = new ProgressBar(this.leftPos + this.width / 2 - 50, this.topPos + 50, 100, 12);
        this.shouldProgressStop = shouldProgressStop;

        if(this.shouldProgressStop)
            this.initProgressStop();
    }

    private final float drawStopStart = new Random().nextFloat();
    private float drawStopEnd = new Random().nextFloat();
    private void initProgressStop(){
        while(drawStopEnd < drawStopStart)
            drawStopEnd = new Random().nextFloat();
    }

    @Override
    public void render(MatrixStack matrixStack){
        if(this.isHidden)
            return;

        super.render(matrixStack);
        this.renderBackground(matrixStack);

        AbstractGui.fill(matrixStack, this.leftPos, this.topPos, this.leftPos + this.width, this.topPos + this.height, Color.LIGHT_GRAY_HEX);

        //background
        int CONTENT_MARGIN = 5;
        AbstractGui.fill(matrixStack, this.leftPos + CONTENT_MARGIN, this.topPos + CONTENT_MARGIN + 10,
                this.leftPos + this.width - CONTENT_MARGIN, this.topPos + CONTENT_MARGIN + this.height - 10,
                this.backgroundColor);

        //title text
        AbstractGui.drawCenteredString(matrixStack, this.font, this.title, this.leftPos + this.width / 2, topPos + 4, this.textColor);

        this.drawProgressBar(matrixStack);
    }

    private void drawProgressBar(MatrixStack matrixStack){
        this.progressBar.render(matrixStack);

        if(this.progressBar.isFull()){
            this.finishedAction.onFinished();
            this.setHidden(true);
        }
    }

    public void update(int x, int y){
        if(this.isHidden())
            return;

        super.update(x, y);

        this.progressBar.setProgress(this.duration);
        this.progressBar.setBarStopped(this.shouldProgressStop && this.progressBar.progress > this.drawStopStart && this.progressBar.progress < this.drawStopEnd);
    }

    private static class ProgressBar{

        private final int xPos, yPos, width, height;
        private int barBackgroundColor = Color.LIGHT_GRAY_HEX;
        private int barColor = Color.WHITE_HEX;
        private float progress;
        private final float maxProgress = 1f;
        private boolean isBarStopped;


        public ProgressBar(int x, int y, int width, int height){
            this.xPos = x;
            this.yPos = y;
            this.width = width;
            this.height = height;
        }


        public void setBarColor(int barColor) {
            this.barColor = barColor;
        }

        public void setBackgroundColor(int backgroundColor) {
            this.barBackgroundColor = backgroundColor;
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


        private float progressStop;
        public void render(MatrixStack matrixStack){

            AbstractGui.fill(matrixStack, this.xPos - 1, this.yPos - 1, this.xPos + this.width + 1, this.yPos + this.height + 1, this.barBackgroundColor);

            float widthProgress = this.progress / this.maxProgress * this.width;
            if(!this.isBarStopped){
                this.progressStop = widthProgress;
            }

            AbstractGui.fill(matrixStack, this.xPos, this.yPos, (int) (this.xPos + (this.isBarStopped ? this.progressStop : widthProgress)),
                    this.yPos + this.height, this.barColor);
        }

        public boolean isFull(){
            return this.progress >= this.maxProgress;
        }
    }
}

