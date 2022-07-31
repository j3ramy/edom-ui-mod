package de.j3ramy.economy.utils.server;

import net.minecraft.nbt.CompoundNBT;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Entry {
    private ArrayList<String> columnsContent = new ArrayList<>();

    public Entry(List<String> columnsContent){
        this.columnsContent.addAll(columnsContent);
    }

    public Entry(CompoundNBT nbt){
        int i = 0;
        while(nbt.contains("content" + i)){
            this.columnsContent.add(nbt.getString("content" + i));
            i++;
        }
    }

    public CompoundNBT getData(){
        CompoundNBT nbt = new CompoundNBT();

        for(int i = 0; i < this.columnsContent.size(); i++){
            nbt.putString("content" + i, this.columnsContent.get(i));
        }

        return nbt;
    }

    public ArrayList<String> getColumnsContent() {
        return this.columnsContent;
    }

    public String getColumnContent(int index){
        return this.columnsContent.get(index);
    }

    public void setColumnsContent(ArrayList<String> columnsContent) {
        this.columnsContent = columnsContent;
    }

    public static String getCurrentTimestamp(){
        SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        return formatter.format(new Date());
    }
}
