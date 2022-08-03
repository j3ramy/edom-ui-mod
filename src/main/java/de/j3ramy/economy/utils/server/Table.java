package de.j3ramy.economy.utils.server;

import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Table {
    private String name;
    private final List<String> columnNames = new ArrayList<>();
    private final List<Entry> entries = new ArrayList<>();
    private boolean adminOnly = false;


    public Table(String name, List<String> attributes){
        this.name = name;
        this.columnNames.addAll(attributes);
    }

    public Table(CompoundNBT nbt){
        this.name = nbt.getString("name");
        this.adminOnly = nbt.getBoolean("adminOnly");

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
        nbt.putBoolean("adminOnly", this.adminOnly);

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
    public ArrayList<String> getAllColumns(String attributeName){
        if(!this.doesAttributeExist(attributeName))
            return null;

        ArrayList<String> columnContents = new ArrayList<>();
        int attributeIndex = this.getAttributeIndex(attributeName);

        for(Entry entry : this.entries){
            columnContents.add(entry.getColumnContent(attributeIndex));
        }

        return columnContents;
    }

    public boolean doesAttributeExist(String attributeName){
        for(String name : this.columnNames){
            if(name.equals(attributeName))
                return true;
        }

        return false;
    }

    public int getAttributeIndex(String attributeName){
        return this.columnNames.indexOf(attributeName);
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

    public boolean insert(Entry entry){
        this.entries.add(entry);
        return true;
    }

    public boolean delete(int index){
        if(this.name.equals("user") && index == 0){
            return false;
        }

        this.entries.remove(index);
        return true;
    }

    public boolean truncate(){
        this.entries.clear();
        return true;
    }

    public boolean update(int index, ArrayList<String> newValues){
        this.entries.get(index).setColumnsContent(newValues);
        return true;
    }
    public int generateSequentialNumber(){
        return this.getSize();
    }

    public boolean isAdminOnly() {
        return this.adminOnly;
    }

    public void setAdminOnly(boolean adminOnly) {
        this.adminOnly = adminOnly;
    }
}
