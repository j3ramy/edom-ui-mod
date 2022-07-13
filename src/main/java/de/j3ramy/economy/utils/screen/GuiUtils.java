package de.j3ramy.economy.utils.screen;

import net.minecraft.util.text.StringTextComponent;

public class GuiUtils {
    public static final int LETTER_SIZE = 6;
    public enum FontSize{
        DEFAULT,
        SMALL
    }

    public static int getCenteredTextOffset(int length){
        return (length / 2 * 5) + 2;
    }

    public static StringTextComponent getFormattedLabel(int maxWordLength, String s){
        if(s == null || s.length() == 0)
            return new StringTextComponent("");

        if(s.length() >= maxWordLength){
            String s1 = s.substring(0, s.length() - (s.length() - maxWordLength));
            s1 += "...";
            return new StringTextComponent(s1);
        }

        return new StringTextComponent(s);
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
}
