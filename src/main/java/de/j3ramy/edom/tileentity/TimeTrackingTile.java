package de.j3ramy.edom.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TimeTrackingTile extends TileEntity {

    private String owner;
    private String company;

    public TimeTrackingTile(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public TimeTrackingTile(){
        this(ModTileEntities.TIME_TRACKING_TILE.get());
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        //itemHandler.deserializeNBT(nbt.getCompound("inv"));
        super.read(state, nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        //nbt.put("inv", itemHandler.serializeNBT());
        return super.write(nbt);
    }
}
