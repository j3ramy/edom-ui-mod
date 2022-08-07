package de.j3ramy.edomui.utils;

import de.j3ramy.edomui.EdomUiMod;
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

    public static String formatTime(Long time) {
        int hours24 = (int)(time / 1000L + 6L) % 24;
        int hours = hours24 % 24;
        int minutes = (int)((float) time / 16.666666F % 60.0F);

        return String.format("%02d:%02d", hours < 1 ? 12 : hours, minutes);
    }
}
