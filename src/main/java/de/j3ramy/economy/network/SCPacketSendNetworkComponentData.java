package de.j3ramy.economy.network;

import de.j3ramy.economy.gui.screen.CreditCartPrinterScreen;
import de.j3ramy.economy.gui.screen.RouterScreen;
import de.j3ramy.economy.utils.NetworkComponentUtils;
import de.j3ramy.economy.utils.data.NetworkComponentData;
import de.j3ramy.economy.utils.server.Server;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Supplier;

public class SCPacketSendNetworkComponentData {
    private final NetworkComponentData data;

    public SCPacketSendNetworkComponentData(PacketBuffer buf){
       data = new NetworkComponentData(Objects.requireNonNull(buf.readCompoundTag()));
    }

    public SCPacketSendNetworkComponentData(NetworkComponentData data){
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

            if(screen instanceof CreditCartPrinterScreen){
                Server server = NetworkComponentUtils.queryServer(data);

                if(server != null && server.getDatabase().doesTableExist("bank_account")){
                    ArrayList<String> accountNumbers = server.getDatabase().getTable("bank_account").getAllColumns("accountNr");
                    ((CreditCartPrinterScreen) screen).setAccountNumbers(accountNumbers);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
