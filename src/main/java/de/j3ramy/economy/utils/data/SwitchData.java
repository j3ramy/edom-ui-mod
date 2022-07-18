package de.j3ramy.economy.utils.data;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemStackHandler;

public class SwitchData {
    public static final int PORT_COUNT = 5;

    public enum PortState{
        CONNECTED,
        CONNECTED_NO_INTERNET,
        NOT_CONNECTED
    }

    private boolean isOn;
    private final BlockPos[] ports = new BlockPos[PORT_COUNT];
    private final PortState[] portStates = new PortState[PORT_COUNT];


    public SwitchData(CompoundNBT nbt){

        this.isOn = nbt.getBoolean("isOn");
        //this.dnsServer = NBTUtil.readBlockPos(nbt.getCompound("dns"));

        for(int i = 0; i < this.ports.length; i++){
            this.ports[i] = new BlockPos(BlockPos.ZERO);
            this.portStates[i] = PortState.NOT_CONNECTED;

            if(nbt.contains("port_" + i))
                this.ports[i] = NBTUtil.readBlockPos(nbt.getCompound("port_" + i));

            if(nbt.contains("portState_" + i))
                this.portStates[i] = PortState.values()[nbt.getInt("portState_" + i)];
        }
    }

    public CompoundNBT getData(){
        CompoundNBT nbt = new CompoundNBT();

        nbt.putBoolean("isOn", this.isOn);
        //nbt.put("dns", NBTUtil.writeBlockPos(this.dnsServer));

        for(int i = 0; i < this.ports.length; i++){
            nbt.put("port_" + i, NBTUtil.writeBlockPos(this.ports[i]));
            nbt.putInt("portState_" + i, this.portStates[i].ordinal());
        }

        return nbt;
    }

    public void setPortState(int port, PortState portState) {
        this.portStates[port] = portState;
    }

    public PortState[] getPortStates() {
        return this.portStates;
    }
    public PortState getPortState(int index){
        return this.portStates[index];
    }

    public void setOn(boolean on) {
        this.isOn = on;
    }

    public void setPort(int port, BlockPos pos){
        this.ports[port] = pos;
    }

    public BlockPos getPort(int index) {
        return this.ports[index];
    }

    public BlockPos[] getPorts() {
        return this.ports;
    }

    /*
    public void setDnsServer(BlockPos dnsServer) {
        this.dnsServer = dnsServer;
    }

     */
}
