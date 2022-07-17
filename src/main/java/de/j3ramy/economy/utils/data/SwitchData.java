package de.j3ramy.economy.utils.data;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemStackHandler;

public class SwitchData {
    private static final int OUTPUT_PORT_COUNT = 4;


    private ItemStackHandler itemHandler = new ItemStackHandler();
    private boolean isOn;
    private BlockPos inputServer;/*, dnsServer;*/
    private final BlockPos[] outputServer = new BlockPos[OUTPUT_PORT_COUNT];


    public SwitchData(CompoundNBT nbt){
        this.itemHandler.deserializeNBT(nbt.getCompound("inv"));
        this.isOn = nbt.getBoolean("isOn");
        this.inputServer = NBTUtil.readBlockPos(nbt.getCompound("input"));
        //this.dnsServer = NBTUtil.readBlockPos(nbt.getCompound("dns"));

        int i = 0;
        while(nbt.contains("output" + i)){
            this.outputServer[i] = NBTUtil.readBlockPos(nbt.getCompound("output" + i));
        }
    }

    public CompoundNBT getData(){
        CompoundNBT nbt = new CompoundNBT();

        nbt.put("inv", this.itemHandler.serializeNBT());
        nbt.putBoolean("isOn", this.isOn);
        nbt.put("input", NBTUtil.writeBlockPos(this.inputServer));
        //nbt.put("dns", NBTUtil.writeBlockPos(this.dnsServer));

        for(int i = 0; i < this.outputServer.length; i++){
            nbt.put("output" + i, NBTUtil.writeBlockPos(this.outputServer[i]));
        }

        return nbt;
    }

    public ItemStackHandler getItemHandler() {
        return this.itemHandler;
    }

    public void setItemHandler(ItemStackHandler itemHandler) {
        this.itemHandler = itemHandler;
    }

    public void setOn(boolean on) {
        this.isOn = on;
    }

    public void setOutput(int slot, BlockPos pos){
        if(slot == 0)
            return;

        this.outputServer[slot] = pos;
    }

    public void setInput(BlockPos pos){
        this.inputServer = pos;
    }

    /*
    public void setDnsServer(BlockPos dnsServer) {
        this.dnsServer = dnsServer;
    }

     */
}
