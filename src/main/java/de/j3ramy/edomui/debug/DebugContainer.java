package de.j3ramy.edomui.debug;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;

public class DebugContainer extends Container {
    public DebugContainer(int windowId){
        super(ModContainers.DEBUG_CONTAINER.get(), windowId);

    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }
}
