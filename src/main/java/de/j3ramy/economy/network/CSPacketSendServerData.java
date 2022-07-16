package de.j3ramy.economy.network;

import de.j3ramy.economy.tileentity.ServerTile;
import de.j3ramy.economy.utils.server.Server;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class CSPacketSendServerData {
    private final Server server;

    public CSPacketSendServerData(PacketBuffer buf){
        this.server = new Server(Objects.requireNonNull(buf.readCompoundTag()));
    }

    public CSPacketSendServerData(Server server){
        this.server = server;
    }

    public void toBytes(PacketBuffer buf){
        buf.writeCompoundTag(server.getData());
    }
    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() ->{
            World world = Objects.requireNonNull(ctx.get().getSender()).world;

            ServerTile tile = (ServerTile) world.getTileEntity(server.getPos());
            if(tile != null){
                tile.setServer(server);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
