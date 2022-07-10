package de.j3ramy.economy.utils.ingame.server;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class Server {

    public enum ServerType{
        BANK,
        CUSTOM
    }

    private final ServerType serverType;
    private final String ip;
    private final BlockPos pos;
    private Database db;
    private boolean isOn;
    private boolean isSet;

    public Server(ServerType serverType, String ip, BlockPos pos){
        this.serverType = serverType;
        this.ip = ip;
        this.pos = pos;

        this.isOn = false;
        this.isSet = true;
    }

    public Server(CompoundNBT nbt){
        this.serverType = ServerType.values()[nbt.getInt("serverType")];
        this.ip = nbt.getString("ip");
        this.pos = NBTUtil.readBlockPos(nbt.getCompound("pos"));
        this.db = new Database(nbt.getCompound("db"));
        this.isOn = false;
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

    public void initDatabase(String dbName){
        this.db = new Database(dbName);
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
        return !this.getIp().isEmpty();
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
