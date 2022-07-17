package de.j3ramy.economy.utils.data;

import de.j3ramy.economy.item.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class SwitchData {
    private static final int OUTPUT_PORT_COUNT = 4;


    private ItemStackHandler itemHandler = new ItemStackHandler();
    private boolean isOn;
    private BlockPos input;
    private final BlockPos[] outputs = new BlockPos[OUTPUT_PORT_COUNT];


    public SwitchData(CompoundNBT nbt){
        this.itemHandler.deserializeNBT(nbt.getCompound("inv"));
        this.isOn = nbt.getBoolean("isOn");
        this.input = NBTUtil.readBlockPos(nbt.getCompound("input"));

        int i = 0;
        while(nbt.contains("output" + i)){
            this.outputs[i] = NBTUtil.readBlockPos(nbt.getCompound("output" + i));
        }
    }

    public CompoundNBT getData(){
        CompoundNBT nbt = new CompoundNBT();

        nbt.put("inv", this.itemHandler.serializeNBT());
        nbt.putBoolean("isOn", this.isOn);
        nbt.put("input", NBTUtil.writeBlockPos(this.input));

        for(int i = 0; i < this.outputs.length; i++){
            nbt.put("output" + i, NBTUtil.writeBlockPos(this.outputs[i]));
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

        this.outputs[slot] = pos;
    }

    public void setInput(BlockPos pos){
        this.input = pos;
    }
}
