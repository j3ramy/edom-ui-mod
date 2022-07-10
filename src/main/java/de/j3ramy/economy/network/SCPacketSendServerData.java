package de.j3ramy.economy.network;

import de.j3ramy.economy.screen.ServerScreen;
import de.j3ramy.economy.utils.Server;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class SCPacketSendServerData {
    private final Server server;

    public SCPacketSendServerData(PacketBuffer buf){
        this.server = new Server(Server.ServerType.values()[buf.readInt()], buf.readString(), buf.readBlockPos(), buf.readString());
        CompoundNBT nbt = buf.readCompoundTag().getCompound("db");
        this.server.addDatabase(nbt.getCompound("db"));
    }

    public SCPacketSendServerData(Server server){
        this.server = server;
    }

    public void toBytes(PacketBuffer buf){
        buf.writeInt(server.getServerType().ordinal());
        buf.writeString(server.getIp());
        buf.writeBlockPos(server.getPos());
        buf.writeString(server.getDatabase().getName());
        buf.writeCompoundTag(server.getData().getCompound("db"));

    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() ->{
            Screen screen = Minecraft.getInstance().currentScreen;

            if(screen instanceof ServerScreen){
                ((ServerScreen)screen).setServer(server);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
