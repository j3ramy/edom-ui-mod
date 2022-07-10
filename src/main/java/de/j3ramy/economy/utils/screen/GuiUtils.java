package de.j3ramy.economy.utils.screen;

import net.minecraft.util.text.StringTextComponent;

public class GuiUtils {
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
}
