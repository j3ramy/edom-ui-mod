package de.j3ramy.economy.network;

import de.j3ramy.economy.gui.screen.ServerScreen;
import de.j3ramy.economy.gui.screen.SwitchScreen;
import de.j3ramy.economy.utils.data.SwitchData;
import de.j3ramy.economy.utils.server.Server;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class SCPacketSendSwitchData {
    private final SwitchData data;

    public SCPacketSendSwitchData(PacketBuffer buf){
       data = new SwitchData(Objects.requireNonNull(buf.readCompoundTag()));
    }

    public SCPacketSendSwitchData(SwitchData data){
        this.data = data;
    }

    public void toBytes(PacketBuffer buf){
        buf.writeCompoundTag(data.getData());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() ->{
            Screen screen = Minecraft.getInstance().currentScreen;

            if(screen instanceof SwitchScreen){
                ((SwitchScreen)screen).setData(data);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
