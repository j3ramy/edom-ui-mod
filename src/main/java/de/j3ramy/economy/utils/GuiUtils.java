package de.j3ramy.economy.utils;

import de.j3ramy.economy.EconomyMod;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiUtils {
    public static final int LETTER_SIZE = 5;
    public enum FontSize{
        DEFAULT,
        SMALL
    }

    public static int getCenteredTextOffset(int length){
        return (length / 2 * LETTER_SIZE) + 2;
    }

    public static StringTextComponent getFormattedLabel(int maxWordLength, String s){
        if(s == null || s.length() == 0)
            return new StringTextComponent("");

        if(s.length() > maxWordLength){
            String s1 = s.substring(0, s.length() - (s.length() - maxWordLength));
            s1 += "...";
            return new StringTextComponent(s1);
        }

        return new StringTextComponent(s);
    }

    public static float getScalingPositionMultiplier(float scaleFactor){
        return 1 / scaleFactor;
    }

    public static String removeVowels(String s){
        if(s.contains("ö"))
            s = s.replace("ö", "oe");

        if(s.contains("Ö"))
            s = s.replace("Ö", "Oe");

        if(s.contains("ä"))
            s = s.replace("ä", "ae");

        if(s.contains("Ä"))
            s = s.replace("Ä", "Ae");

        if(s.contains("ü"))
            s = s.replace("ü", "ue");

        if(s.contains("Ü"))
            s = s.replace("Ü", "Ue");

        return s;
    }

    public static String formatTime(Long time) {
        int hours24 = (int)(time / 1000L + 6L) % 24;
        int hours = hours24 % 24;
        int minutes = (int)((float) time / 16.666666F % 60.0F);

        return String.format("%02d:%02d", hours < 1 ? 12 : hours, minutes);
    }

    public static String getTranslationText(String translationKey){
        return new TranslationTextComponent("screen." + EconomyMod.MOD_ID + ".tooltip." + translationKey).getString();
    }
}
