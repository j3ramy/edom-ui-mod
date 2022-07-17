package de.j3ramy.economy.utils.server;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;

public class Server {

    public enum DBType{
        BANK,
        CUSTOM
    }

    private final DBType serverType;
    private final String ip;
    private BlockPos pos;
    private final Database db;
    private String password = "";
    private boolean isOn;
    private int accesses;

    public void setOn(boolean on) {
        this.isOn = on;
    }

    public Server(DBType serverType, String ip, BlockPos pos){
        this.serverType = serverType;
        this.ip = ip;
        this.pos = pos;
        this.isOn = false;

        this.db = new Database("db_" + this.serverType.name().toLowerCase());
    }

    public Server(CompoundNBT nbt){
        this.serverType = DBType.values()[nbt.getInt("serverType")];
        this.ip = nbt.getString("ip");
        this.pos = NBTUtil.readBlockPos(nbt.getCompound("pos"));
        this.db = new Database(nbt.getCompound("db"));
        this.password = nbt.getString("password");
        this.isOn = nbt.getBoolean("isOn");
        this.accesses = nbt.getInt("accesses");
    }

    public CompoundNBT getData(){
        CompoundNBT nbt = new CompoundNBT();

        nbt.putBoolean("isOn", this.isOn);
        nbt.putInt("serverType", this.serverType.ordinal());
        nbt.putString("ip", this.ip);
        nbt.put("pos", NBTUtil.writeBlockPos(this.pos));
        nbt.put("db", this.db.getData());
        nbt.putString("password", this.password);
        nbt.putInt("accesses", this.accesses);

        return nbt;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public Database getDatabase() {
        return this.db;
    }

    public int getAccesses() {
        return this.accesses;
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

    public String getIp() {
        return this.ip;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }
}
