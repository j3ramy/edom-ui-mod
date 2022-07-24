package de.j3ramy.economy.utils;

import net.minecraft.util.math.BlockPos;

public class Math {

    public static boolean areBlockPosEqual(BlockPos pos1, BlockPos pos2){
        return pos1.getX() == pos2.getX() && pos1.getY() == pos2.getY() && pos1.getZ() == pos2.getZ();
    }
}
