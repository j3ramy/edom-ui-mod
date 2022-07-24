package de.j3ramy.economy.network;

import de.j3ramy.economy.gui.screen.RouterScreen;
import de.j3ramy.economy.utils.data.NetworkComponentData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class SCPacketSendRouterData {
    private final NetworkComponentData data;

    public SCPacketSendRouterData(PacketBuffer buf){
       data = new NetworkComponentData(Objects.requireNonNull(buf.readCompoundTag()));
    }

    public SCPacketSendRouterData(NetworkComponentData data){
        this.data = data;
    }

    public void toBytes(PacketBuffer buf){
        buf.writeCompoundTag(data.getData());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() ->{
            Screen screen = Minecraft.getInstance().currentScreen;

            if(screen instanceof RouterScreen){
                ((RouterScreen)screen).setData(data);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
