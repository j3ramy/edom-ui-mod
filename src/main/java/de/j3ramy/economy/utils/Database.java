package de.j3ramy.economy.utils;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

public class Database {

    private String name;
    private final ListNBT tableList;

    public Database(String name){
        this.name = name;

        this.tableList = new ListNBT();
    }

    public Database(CompoundNBT nbt){
        this.name = nbt.getString("name");
        this.tableList = nbt.getList("tableList", 8);
    }

    public CompoundNBT getData(){
        CompoundNBT nbt = new CompoundNBT();

        nbt.putString("name", this.name);
        nbt.put("tableList", this.tableList);

        return nbt;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ListNBT getTables() {
        return this.tableList;
    }

    public void createTable(String name, ListNBT columnNames){
        this.tableList.add(new DBTable(name, columnNames).getData());
    }

    public void dropTable(int index){
        this.tableList.remove(index);
    }
}
