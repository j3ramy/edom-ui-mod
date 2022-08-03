package de.j3ramy.economy.utils.server;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class Server {

    public enum DBType{
        BANK,
        CUSTOM
    }

    private final DBType serverType;
    private String ip;
    private BlockPos pos;
    private final Database db;
    private String password = "";
    private String adminUsername = "";
    private String adminPassword = "";
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
        this.adminUsername = nbt.getString("adminUsername");
        this.adminPassword = nbt.getString("adminPassword");

        if(this.getDatabase().getTableCount() == 0)
            this.initialFillDatabase();
    }

    private void initialFillDatabase(){
        ArrayList<String> attributes = new ArrayList<>();
        attributes.add("username");
        attributes.add("password");
        attributes.add("isAdmin");
        attributes.add("readOnly");
        attributes.add("logins");
        attributes.add("createdAt");
        attributes.add("lastLogin");

        this.db.createTable("user", attributes);
        this.db.getTable("user").setAdminOnly(true);

        attributes.clear();
        attributes.add(this.adminUsername);
        attributes.add(this.adminPassword);
        attributes.add("true");
        attributes.add("false");
        attributes.add("0");
        attributes.add(Entry.getCurrentTimestamp());
        attributes.add("");
        this.db.getTable("user").insert(new Entry(attributes));

        switch(this.serverType){
            case CUSTOM:
                break;
            case BANK:
                attributes.clear();
                attributes.add("accountNr");
                attributes.add("owner");
                attributes.add("pin");
                attributes.add("balance");
                attributes.add("dateOfBirth");
                attributes.add("isLocked");
                attributes.add("createdAt");
                attributes.add("lastLogin");

                this.db.createTable("bank_account", attributes);

                //DEBUG
                /*
                attributes.clear();
                attributes.add(CreditCardData.generateAccountNumber());
                attributes.add("-");
                attributes.add("-");
                attributes.add("-");
                attributes.add("-");
                attributes.add("-");
                attributes.add("-");
                this.db.getTable("bank_account").insert(new Entry(attributes));
                attributes.clear();
                attributes.add(CreditCardData.generateAccountNumber());
                attributes.add("-");
                attributes.add("-");
                attributes.add("-");
                attributes.add("-");
                attributes.add("-");
                attributes.add("-");
                this.db.getTable("bank_account").insert(new Entry(attributes));
                attributes.clear();
                attributes.add(CreditCardData.generateAccountNumber());
                attributes.add("-");
                attributes.add("-");
                attributes.add("-");
                attributes.add("-");
                attributes.add("-");
                attributes.add("-");
                this.db.getTable("bank_account").insert(new Entry(attributes));

                 */
                //DEBUG END

                attributes.clear();
                attributes.add("pos");

                this.db.createTable("atm", attributes);

                attributes.clear();
                attributes.add("type");
                attributes.add("amount");
                attributes.add("from");
                attributes.add("to");
                attributes.add("time");

                this.db.createTable("transaction", attributes);
                break;

        }
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
        nbt.putString("adminUsername", this.adminUsername);
        nbt.putString("adminPassword", this.adminPassword);

        return nbt;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public Database getDatabase() {
        return this.db;
    }

    public void increaseAccesses(){
        this.accesses += 1;
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

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminUsername() {
        return this.adminUsername;
    }

    public String getAdminPassword() {
        return this.adminPassword;
    }
}
