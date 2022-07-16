package de.j3ramy.economy.utils.server;

import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Table {
    private String name;
    private final List<String> columnNames = new ArrayList<>();
    private final List<Entry> entries = new ArrayList<>();


    public Table(String name, List<String> attributes){
        this.name = name;
        this.columnNames.addAll(attributes);
    }

    public Table(CompoundNBT nbt){
        this.name = nbt.getString("name");

        int i = 0;
        while(nbt.contains("columnName" + i)){
            this.columnNames.add(nbt.getString("columnName" + i));
            i++;
        }

        int j = 0;
        while(nbt.contains("entry" + j)){
            this.entries.add(new Entry(nbt.getCompound("entry" + j)));
            j++;
        }
    }

    public CompoundNBT getData(){
        CompoundNBT nbt = new CompoundNBT();

        nbt.putString("name", this.name);

        for(int i = 0; i < this.columnNames.size(); i++){
            nbt.putString("columnName" + i, this.columnNames.get(i));
        }

        for(int i = 0; i < this.entries.size(); i++){
            nbt.put("entry" + i, this.entries.get(i).getData());
        }

        return nbt;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getColumnNames() {
        return this.columnNames;
    }

    public List<Entry> getAllEntries() {
        return this.entries;
    }

    @Nullable
    public Entry getEntry(int index){
        return this.entries.get(index);
    }

    public int getSize(){
        return this.entries.size();
    }

    public int getColumnCount(){
        return this.columnNames.size();
    }

    public void insert(Entry entry){
        this.entries.add(entry);
    }

    public void delete(int index){
        this.entries.remove(index);
    }

    public void truncate(){
        this.entries.clear();
    }

    public void update(int index, List<String> newValues){
        this.entries.get(index).setColumnsContent(newValues);
    }
}
