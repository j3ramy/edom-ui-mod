package de.j3ramy.economy.utils;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

public class Entry {
    public ListNBT columnsContent;

    public Entry(ListNBT columnsContent){
        this.columnsContent = columnsContent;
    }

    public Entry(CompoundNBT nbt){
        this.columnsContent = nbt.getList("columnsContent", 8);
    }

    public CompoundNBT getData(){
        CompoundNBT nbt = new CompoundNBT();

        nbt.put("columnsContent", this.columnsContent);

        return nbt;
    }

    public ListNBT getColumnsContent() {
        return this.columnsContent;
    }

    public String getColumnContent(int index){
        return this.columnsContent.getString(index);
    }
}
