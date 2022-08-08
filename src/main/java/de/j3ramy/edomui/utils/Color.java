package de.j3ramy.edomui.utils;

public class Color {
    public static final int DARK_RED = 0xffAA0000;
    public static final int RED = 0xffFF5555;
    public static final int ORANGE = 0xffFFAA00;
    public static final int YELLOW = 0xffFFFF55;
    public static final int DARK_GREEN = 0xff00AA00;
    public static final int GREEN = 0xff55FF55;
    public static final int AQUA = 0xff55FFFF;
    public static final int DARK_AQUA = 0xff00AAAA;
    public static final int DARK_BLUE = 0xff0000AA;
    public static final int BLUE = 0xff5555FF;
    public static final int PURPLE = 0xffFF55FF;
    public static final int DARK_PURPLE = 0xffAA00AA;
    public static final int WHITE = 0xffFFFFFF;
    public static final int BLACK = 0xff0e0e0e;
    public static final int GRAY = 0xffAAAAAA;
    public static final int DARK_GRAY = 0xff555555;
    public static final int PINK = 0xfffd8cee;
    public static final int BROWN = 0xff73532e;

    //Use for custom colors
    //Pass hex without #
    public static int customColor(String hex){
        return Integer.parseInt(hex, 16);
    }

}
