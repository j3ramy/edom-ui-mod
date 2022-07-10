package de.j3ramy.economy.network;

import de.j3ramy.economy.item.ModItems;
import de.j3ramy.economy.tileentity.CreditCardPrinterTile;
import de.j3ramy.economy.tileentity.ServerTile;
import de.j3ramy.economy.utils.CreditCardData;
import de.j3ramy.economy.utils.Server;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class CSPacketSendServerData {
    private final String ip;
    private final Server.ServerType serverType;
    private final String dbName;
    private final BlockPos pos;

    public CSPacketSendServerData(PacketBuffer buf){
        this.ip = buf.readString();
        this.serverType = Server.ServerType.values()[buf.readInt()];
        this.dbName = buf.readString();
        this.pos = buf.readBlockPos();
    }

    public CSPacketSendServerData(Server server, BlockPos pos){
        this.ip = server.getIp();
        this.serverType = server.getServerType();
        this.dbName = server.getDatabase().getName();

        this.pos = pos;
    }

    public void toBytes(PacketBuffer buf){
        buf.writeString(ip);
        buf.writeInt(serverType.ordinal());
        buf.writeString(dbName);

        buf.writeBlockPos(pos);
    }
    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() ->{
            World world = Objects.requireNonNull(ctx.get().getSender()).world;

            ServerTile tile = (ServerTile) world.getTileEntity(pos);
            if(tile != null){
                Server server = new Server(serverType, ip, pos, dbName);
                tile.setServer(server);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
