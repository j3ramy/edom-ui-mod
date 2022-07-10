package de.j3ramy.economy.utils;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

public class DBTable {
    private String name;
    private final ListNBT columnNames;
    private final ListNBT entries;

    public DBTable(String name, ListNBT columnNames){
        this.name = name;
        this.columnNames = columnNames;

        this.entries = new ListNBT();
    }

    public DBTable(CompoundNBT nbt){
        this.name = nbt.getString("name");
        this.columnNames = nbt.getList("columnNames", 8);
        this.entries = nbt.getList("entries", 8);
    }

    public CompoundNBT getData(){
        CompoundNBT nbt = new CompoundNBT();

        nbt.putString("name", this.name);
        nbt.put("columnNames", this.columnNames);
        nbt.put("entries", this.entries);

        return nbt;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ListNBT getColumnNames() {
        return this.columnNames;
    }

    public ListNBT getEntries() {
        return this.entries;
    }

    public int getSize(){
        return this.entries.size();
    }

    public int getAttributeCount(){
        return this.columnNames.size();
    }

    public void insert(Entry entry){
        this.entries.add(entry.getData());
    }

    public void delete(int index){
        this.entries.remove(index);
    }

    public void truncate(){
        this.entries.clear();
    }
}
