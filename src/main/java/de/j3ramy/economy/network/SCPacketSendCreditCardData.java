package de.j3ramy.economy.network;

import de.j3ramy.economy.gui.screen.CreditCardScreen;
import de.j3ramy.economy.utils.data.CreditCardData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SCPacketSendCreditCardData {
    private final CreditCardData creditCardData = new CreditCardData();

    public SCPacketSendCreditCardData(PacketBuffer buf){
        this.creditCardData.setOwner(buf.readString());
        this.creditCardData.setAccountNumber(buf.readString());
        this.creditCardData.setValidity(buf.readString());
        this.creditCardData.setPin(buf.readString());
    }

    public SCPacketSendCreditCardData(CreditCardData creditCardData){
        this.creditCardData.setOwner(creditCardData.getOwner());
        this.creditCardData.setAccountNumber(creditCardData.getAccountNumber());
        this.creditCardData.setValidity(creditCardData.getValidity());
        this.creditCardData.setPin(creditCardData.getPin());
    }

    public void toBytes(PacketBuffer buf){
        buf.writeString(creditCardData.getOwner());
        buf.writeString(creditCardData.getAccountNumber());
        buf.writeString(creditCardData.getValidity());
        buf.writeString(creditCardData.getPin());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() ->{
            Screen screen = Minecraft.getInstance().currentScreen;

            if(screen instanceof CreditCardScreen){
                ((CreditCardScreen)screen).setCreditCardData(creditCardData);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
