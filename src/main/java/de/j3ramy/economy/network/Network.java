package de.j3ramy.economy.network;

import de.j3ramy.economy.EconomyMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class Network {
    public static SimpleChannel INSTANCE;
    private static int ID;

    public static int getNextId(){
        return ID++;
    }

    public static void registerMessages(){
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(EconomyMod.MOD_ID, "network"), () -> "1.0", s -> true, s -> true);

        INSTANCE.registerMessage(getNextId(), CSPacketSendCreditCardData.class, CSPacketSendCreditCardData::toBytes, CSPacketSendCreditCardData::new, CSPacketSendCreditCardData::handle);
        INSTANCE.registerMessage(getNextId(), SCPacketSendCreditCardData.class, SCPacketSendCreditCardData::toBytes, SCPacketSendCreditCardData::new, SCPacketSendCreditCardData::handle);
        INSTANCE.registerMessage(getNextId(), SCPacketSendServerData.class, SCPacketSendServerData::toBytes, SCPacketSendServerData::new, SCPacketSendServerData::handle);
        INSTANCE.registerMessage(getNextId(), CSPacketSendServerData.class, CSPacketSendServerData::toBytes, CSPacketSendServerData::new, CSPacketSendServerData::handle);
        INSTANCE.registerMessage(getNextId(), CSPacketLoadBackup.class, CSPacketLoadBackup::toBytes, CSPacketLoadBackup::new, CSPacketLoadBackup::handle);
        INSTANCE.registerMessage(getNextId(), SCPacketSendSwitchData.class, SCPacketSendSwitchData::toBytes, SCPacketSendSwitchData::new, SCPacketSendSwitchData::handle);
        INSTANCE.registerMessage(getNextId(), CSPacketSendSwitchData.class, CSPacketSendSwitchData::toBytes, CSPacketSendSwitchData::new, CSPacketSendSwitchData::handle);
        INSTANCE.registerMessage(getNextId(), SCPacketSendNetworkComponentData.class, SCPacketSendNetworkComponentData::toBytes, SCPacketSendNetworkComponentData::new, SCPacketSendNetworkComponentData::handle);
        INSTANCE.registerMessage(getNextId(), CSPacketSendNetworkComponentData.class, CSPacketSendNetworkComponentData::toBytes, CSPacketSendNetworkComponentData::new, CSPacketSendNetworkComponentData::handle);
    }
}
