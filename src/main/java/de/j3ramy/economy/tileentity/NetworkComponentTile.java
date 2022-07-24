package de.j3ramy.economy.tileentity;

import de.j3ramy.economy.utils.data.NetworkComponentData;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class NetworkComponentTile extends TileEntity {

    protected NetworkComponentData data = new NetworkComponentData(new CompoundNBT());

    public NetworkComponentTile(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public NetworkComponentData getData() {
        return this.data;
    }

    public void setData(NetworkComponentData networkComponentData) {
        this.data = networkComponentData;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        this.data = new NetworkComponentData(nbt.getCompound("data"));

        super.read(state, nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.put("data", this.data.getData());

        return super.write(nbt);
    }
}
