package de.j3ramy.economy.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

public class ComputerTile extends TileEntity {

    public ComputerTile(){
        super(ModTileEntities.COMPUTER_TILE.get());
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        return super.write(nbt);
    }

}
