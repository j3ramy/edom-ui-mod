package de.j3ramy.economy.network;

import de.j3ramy.economy.tileentity.ServerTile;
import de.j3ramy.economy.utils.server.Server;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.function.Supplier;

public class CSPacketSendServerData {
    private final Server server;
    private final boolean isBackup;

    public CSPacketSendServerData(PacketBuffer buf){
        this.server = new Server(Objects.requireNonNull(buf.readCompoundTag()));
        this.isBackup = buf.readBoolean();
    }

    public CSPacketSendServerData(Server server, boolean isBackup){
        this.server = server;
        this.isBackup = isBackup;
    }

    public void toBytes(PacketBuffer buf){
        buf.writeCompoundTag(server.getData());
        buf.writeBoolean(isBackup);
    }
    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() ->{
            World world = Objects.requireNonNull(ctx.get().getSender()).world;

            ServerTile tile = (ServerTile) world.getTileEntity(server.getPos());
            if(tile == null)
                return;

            if(this.isBackup){
                ItemStack flashDrive = tile.itemHandler.getStackInSlot(0);
                CompoundNBT nbt = flashDrive.hasTag() ? flashDrive.getTag() : new CompoundNBT();

                assert nbt != null;
                nbt.put("server", this.server.getData());

                SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy_HHmmss");
                nbt.putString("name", "backup_" + this.server.getIp() + "_" + formatter.format(new Date()));

                flashDrive.setTag(nbt);
            }
            else{
                System.out.println(this.server.getPassword());
                tile.setServer(this.server);
            }

        });

        ctx.get().setPacketHandled(true);
    }
}
