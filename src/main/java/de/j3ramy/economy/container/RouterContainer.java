package de.j3ramy.economy.container;

import de.j3ramy.economy.block.ModBlocks;
import de.j3ramy.economy.tileentity.RouterTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IWorldPosCallable;

public class RouterContainer extends Container {

    private final RouterTile tileEntity;

    public RouterTile getTileEntity() {
        return this.tileEntity;
    }

    public RouterContainer(int windowId, RouterTile tile){
        super(ModContainers.ROUTER_CONTAINER.get(), windowId);

        this.tileEntity = tile;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        if(tileEntity.getWorld() == null)
            return false;

        return isWithinUsableDistance(IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos()), playerIn, ModBlocks.ROUTER.get());
    }
}
