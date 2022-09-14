package de.j3ramy.edomui.utils;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiUtils {
    public static final int LETTER_WIDTH = 5;
    public static final int LETTER_HEIGHT = 7;


    public static String getFormattedLabel(int maxWordLength, String s){
        if(s == null || s.length() == 0)
            return "";

        if(s.length() > maxWordLength + 1){
            String s1 = s.substring(0, s.length() - (s.length() - maxWordLength));
            s1 += "...";
            return s1;
        }

        return s;
    }

    public static float getScalingPositionMultiplier(float scaleFactor){
        return 1 / scaleFactor;
    }

    public static String getFormattedTime(Long time) {
        int hours24 = (int)(time / 1000L + 6L) % 24;
        int hours = hours24 % 24;
        int minutes = (int)((float) time / 16.666666F % 60.0F);

        return String.format("%02d:%02d", hours < 1 ? 12 : hours, minutes);
    }

    //draw text without minecraft standard font shadow
    public static void drawText(MatrixStack matrixStack, int x, int y, String text, int color){
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, text, x, y, color);
    }

    public static void drawCenteredText(MatrixStack matrixStack, int y, String text, int color){
        if(Minecraft.getInstance().currentScreen != null){
            int screenWidth = Minecraft.getInstance().currentScreen.width;

            Minecraft.getInstance().fontRenderer.drawString(matrixStack, text,
                    screenWidth / 2f - Minecraft.getInstance().fontRenderer.getStringWidth(text) / 2f, y, color);
        }
    }
}
