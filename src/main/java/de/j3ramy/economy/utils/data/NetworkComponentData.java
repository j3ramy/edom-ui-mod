package de.j3ramy.economy.utils.data;

import de.j3ramy.economy.utils.Math;
import de.j3ramy.economy.utils.enums.NetworkComponent;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;

public class NetworkComponentData {

    private String name; //set when name edited in screen
    private BlockPos from; //normally Component position; set when cable connected
    private BlockPos to; //normally Switch position; set when put in switch port
    private NetworkComponent component = NetworkComponent.NONE;


    public NetworkComponentData(CompoundNBT nbt){
        this.name = nbt.getString("name");
        this.from = NBTUtil.readBlockPos(nbt.getCompound("from"));
        this.to = NBTUtil.readBlockPos(nbt.getCompound("to"));

        if(!nbt.getString("component").isEmpty())
            this.component = NetworkComponent.valueOf(nbt.getString("component"));
    }

    public CompoundNBT getData(){
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("name", this.name);
        nbt.put("from", NBTUtil.writeBlockPos(this.from));
        nbt.put("to", NBTUtil.writeBlockPos(this.to));
        nbt.putString("component", this.component.name());

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
        return !Math.areBlockPosEqual(this.from, BlockPos.ZERO);
    }

    public void setComponent(NetworkComponent component) {
        this.component = component;
    }

    public NetworkComponent getComponent() {
        return this.component;
    }
}
