package de.j3ramy.economy.tileentity;

import de.j3ramy.economy.utils.Server;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class ServerTile extends TileEntity {

    /*
    private final IIntArray data = new IIntArray() {
        public int get(int index) {
            switch(index){

            }

            return -1;
        }

        public void set(int index, int value) {
            switch(index){

            }
        }
        public int size() {
            return 1;
        }
    };

    public IIntArray getData() {
        return this.data;
    }

     */

    private Server server;

    public Server getServer() {
        return this.server;
    }

    public void setServer(Server server) {
        this.server = server;
    }


    public ServerTile(){
        super(ModTileEntities.SERVER_TILE.get());
    }


    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        this.server = new Server(nbt.getCompound("server"));
        super.read(state, nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        if(this.server == null)
            return super.write(nbt);

        System.out.println(this.server.getIp());
        nbt.put("server", this.server.getData());
        return super.write(nbt);
    }

}
