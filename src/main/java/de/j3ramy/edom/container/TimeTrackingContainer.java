package de.j3ramy.edom.container;

import de.j3ramy.edom.block.ModBlocks;
import de.j3ramy.edom.tileentity.TimeTrackingTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TimeTrackingContainer extends Container {

    private final TimeTrackingTile tileEntity;

    public TimeTrackingContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player, TimeTrackingTile tile){
        super(ModContainers.TIME_TRACKING_CONTAINER.get(), windowId);

        this.tileEntity = tile;
    }

    public TileEntity getTileEntity() {
        return tileEntity;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        if(tileEntity.getWorld() == null)
            return false;

        return isWithinUsableDistance(IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos()), playerIn, ModBlocks.TIME_TRACKING_TERMINAL.get());
    }
}
