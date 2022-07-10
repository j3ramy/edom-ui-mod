package de.j3ramy.economy.network;

import de.j3ramy.economy.screen.ServerScreen;
import de.j3ramy.economy.utils.ingame.server.Server;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class SCPacketSendServerData {
    private final Server server;

    public SCPacketSendServerData(PacketBuffer buf){
       server = new Server(Objects.requireNonNull(buf.readCompoundTag()));
    }

    public SCPacketSendServerData(Server server){
        this.server = server;
    }

    public void toBytes(PacketBuffer buf){
        buf.writeCompoundTag(server.getData());
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
