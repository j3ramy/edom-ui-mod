package de.j3ramy.economy.network;

import de.j3ramy.economy.gui.screen.CreditCardScreen;
import de.j3ramy.economy.utils.data.BankAccountData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SCPacketSendBankAccountData {
    private final BankAccountData bankAccountData = new BankAccountData();

    public SCPacketSendBankAccountData(PacketBuffer buf){
        this.bankAccountData.setOwner(buf.readString());
        this.bankAccountData.setAccountNumber(buf.readString());
        this.bankAccountData.setValidity(buf.readString());
        this.bankAccountData.setPin(buf.readString());
    }

    public SCPacketSendBankAccountData(BankAccountData bankAccountData){
        this.bankAccountData.setOwner(bankAccountData.getOwner());
        this.bankAccountData.setAccountNumber(bankAccountData.getAccountNumber());
        this.bankAccountData.setValidity(bankAccountData.getValidity());
        this.bankAccountData.setPin(bankAccountData.getPin());
    }

    public void toBytes(PacketBuffer buf){
        buf.writeString(bankAccountData.getOwner());
        buf.writeString(bankAccountData.getAccountNumber());
        buf.writeString(bankAccountData.getValidity());
        buf.writeString(bankAccountData.getPin());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() ->{
            Screen screen = Minecraft.getInstance().currentScreen;

            if(screen instanceof CreditCardScreen){
                ((CreditCardScreen)screen).setCreditCardData(bankAccountData);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
