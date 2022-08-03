package de.j3ramy.economy.utils.server;

import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private final String name;
    private final List<Table> tableList = new ArrayList<>();

    public Database(String name){
        this.name = name;
    }

    public Database(CompoundNBT nbt){
        this.name = nbt.getString("name");

        int i = 0;
        while(nbt.contains("table" + i)){
            this.tableList.add(new Table(nbt.getCompound("table" + i)));
            i++;
        }
    }

    public CompoundNBT getData(){
        CompoundNBT nbt = new CompoundNBT();

        nbt.putString("name", this.name);

        for(int i = 0; i < this.tableList.size(); i++){
            nbt.put("table" + i, this.tableList.get(i).getData());
        }

        return nbt;
    }

    public String getName() {
        return this.name;
    }

    public int getTotalEntryCount(){
        int i = 0;

        for(Table table : this.tableList)
            i += table.getAllEntries().size();

        return i;
    }

    public List<Table> getTables(){
        return this.tableList;
    }

    @Nullable
    public Table getTable(String name){
        for(Table table : this.tableList){
            if(table.getName().equals(name))
                return table;
        }

        return null;
    }

    public boolean doesTableExist(String name){
        for(Table table : this.tableList){
            if(table.getName().equals(name))
                return true;
        }

        return false;
    }

    public boolean dropTable(int index){
        if(index == 0)
            return false;

        this.tableList.remove(index);
        return true;
    }

    public int getTableIndex(String name){
        for(int i = 0; i < this.tableList.size(); i++){
            if(this.tableList.get(i).getName().equals(name))
                return i;
        }

        return -1;
    }

    public boolean createTable(String name, List<String> attributes){
        for(Table table : this.tableList){
            if(table.getName().equals(name))
                return false;
        }

        this.tableList.add(new Table(name, attributes));
        return true;
    }

    public int getTableCount(){
        return this.tableList.size();
    }
}
