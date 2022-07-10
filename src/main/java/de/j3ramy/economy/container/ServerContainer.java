package de.j3ramy.economy.container;

import de.j3ramy.economy.block.ModBlocks;
import de.j3ramy.economy.tileentity.AtmTile;
import de.j3ramy.economy.tileentity.ServerTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

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
