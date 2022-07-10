package de.j3ramy.economy.utils;

import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;

public class Server {

    public enum ServerType{
        BANK,
        CUSTOM
    }

    private boolean isOn;
    private final boolean isSet;
    private final ServerType serverType;
    private final String ip;
    private final BlockPos pos;
    private Database db;

    public Server(ServerType serverType, String ip, BlockPos pos, String customDbName){
        this.serverType = serverType;
        this.ip = ip;
        this.pos = pos;

        this.db = new Database(customDbName);

        this.isOn = false;
        this.isSet = true;
    }

    public Server(CompoundNBT nbt){
        this.serverType = ServerType.values()[nbt.getInt("serverType")];
        this.ip = nbt.getString("ip");
        this.pos = NBTUtil.readBlockPos(nbt.getCompound("pos"));
        this.db = new Database(nbt.getCompound("db"));

        this.isOn = false;
        this.isSet = true;
    }

    public CompoundNBT getData(){
        CompoundNBT nbt = new CompoundNBT();

        nbt.putBoolean("isOn", this.isOn);
        nbt.putBoolean("isSet", this.isSet);
        nbt.putInt("serverType", this.serverType.ordinal());
        nbt.putString("ip", this.ip);
        nbt.put("pos", NBTUtil.writeBlockPos(this.pos));
        nbt.put("db", this.db.getData());

        return nbt;
    }

    public void addDatabase(CompoundNBT nbt){
        this.db = new Database(nbt);
    }

    public void deleteDatabase(){
        this.db = null;
    }

    public Database getDatabase() {
        return this.db;
    }

    public void turnOn(){
        this.isOn = true;
    }

    public void turnOff(){
        this.isOn = false;
    }

    public boolean isOn() {
        return this.isOn;
    }

    public boolean isSet(){
        return this.isSet;
    }

    public ServerType getServerType() {
        return this.serverType;
    }

    public String getIp() {
        return this.ip;
    }

    public BlockPos getPos() {
        return this.pos;
    }
}
