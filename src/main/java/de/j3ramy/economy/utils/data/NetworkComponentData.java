package de.j3ramy.economy.utils.data;

import de.j3ramy.economy.utils.enums.NetworkComponent;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;

public class NetworkComponentData {

    private String name;
    private BlockPos from; //Normally Component position
    private BlockPos to; //Normally Switch position
    private NetworkComponent component;


    public NetworkComponentData(CompoundNBT nbt){
        this.name = nbt.getString("name");
        this.from = NBTUtil.readBlockPos(nbt.getCompound("from"));
        this.to = NBTUtil.readBlockPos(nbt.getCompound("to"));
        this.component = NetworkComponent.values()[nbt.getInt("component")];
    }

    public CompoundNBT getData(){
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("name", this.name);
        nbt.put("from", NBTUtil.writeBlockPos(this.from));
        nbt.put("to", NBTUtil.writeBlockPos(this.to));
        nbt.putInt("component", this.component.ordinal());

        return nbt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setFrom(BlockPos from) {
        this.from = from;
    }

    public BlockPos getFrom() {
        return this.from;
    }

    public void setTo(BlockPos to) {
        this.to = to;
    }

    public BlockPos getTo() {
        return this.to;
    }

    public boolean isSet(){
        return !this.name.isEmpty();
    }

    public void setComponent(NetworkComponent component) {
        this.component = component;
    }

    public NetworkComponent getComponent() {
        return this.component;
    }
}
