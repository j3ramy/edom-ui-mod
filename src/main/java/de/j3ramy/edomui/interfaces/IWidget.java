package de.j3ramy.edomui.interfaces;

import com.mojang.blaze3d.matrix.MatrixStack;

public interface IWidget {
    void update(int x, int y);
    void render(MatrixStack matrixStack);
}
