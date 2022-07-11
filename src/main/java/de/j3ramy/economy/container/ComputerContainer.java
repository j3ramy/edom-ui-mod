package de.j3ramy.economy.container;

import de.j3ramy.economy.block.ModBlocks;
import de.j3ramy.economy.tileentity.AtmTile;
import de.j3ramy.economy.tileentity.ComputerTile;
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

public class ComputerContainer extends Container {

    public TileEntity tileEntity;

    public ComputerContainer(int windowId, ComputerTile tile){
        super(ModContainers.COMPUTER_CONTAINER.get(), windowId);

        this.tileEntity = tile;

    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        if(tileEntity.getWorld() == null)
            return false;

        return isWithinUsableDistance(IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos()), playerIn, ModBlocks.COMPUTER.get());
    }



}
