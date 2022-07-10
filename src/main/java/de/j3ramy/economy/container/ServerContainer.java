package de.j3ramy.economy.container;

import de.j3ramy.economy.block.ModBlocks;
import de.j3ramy.economy.tileentity.ServerTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IWorldPosCallable;

public class ServerContainer extends Container {

    public ServerTile tileEntity;

    public ServerTile getTileEntity() {
        return this.tileEntity;
    }

    public ServerContainer(int windowId, ServerTile tile){
        super(ModContainers.SERVER_CONTAINER.get(), windowId);

        this.tileEntity = tile;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        if(tileEntity.getWorld() == null)
            return false;

        return isWithinUsableDistance(IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos()), playerIn, ModBlocks.SERVER.get());
    }


}
