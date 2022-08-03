package de.j3ramy.economy.network;

import de.j3ramy.economy.EconomyMod;
import de.j3ramy.economy.gui.screen.CreditCartPrinterScreen;
import de.j3ramy.economy.item.ModItems;
import de.j3ramy.economy.tileentity.CreditCardPrinterTile;
import de.j3ramy.economy.utils.NetworkComponentUtils;
import de.j3ramy.economy.utils.data.BankAccountData;
import de.j3ramy.economy.utils.server.Entry;
import de.j3ramy.economy.utils.server.Server;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Supplier;

public class CSPacketSendBankAccountData {
    private final BankAccountData data = new BankAccountData();
    private final BlockPos pos;

    public CSPacketSendBankAccountData(PacketBuffer buf){
        this.data.setOwner(buf.readString());
        this.data.setAccountNumber(buf.readString());
        this.data.setValidity(buf.readString());
        this.data.setPin(buf.readString());

        this.pos = buf.readBlockPos();
    }

    public CSPacketSendBankAccountData(BankAccountData bankAccountData, BlockPos pos){
        this.data.setOwner(bankAccountData.getOwner());
        this.data.setAccountNumber(bankAccountData.getAccountNumber());
        this.data.setValidity(bankAccountData.getValidity());
        this.data.setPin(bankAccountData.getPin());

        this.pos = pos;
    }

    public void toBytes(PacketBuffer buf){
        buf.writeString(data.getOwner());
        buf.writeString(data.getAccountNumber());
        buf.writeString(data.getValidity());
        buf.writeString(data.getPin());

        buf.writeBlockPos(pos);
    }
    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() ->{
            World world = Objects.requireNonNull(ctx.get().getSender()).world;
            ServerPlayerEntity player = ctx.get().getSender();

            CreditCardPrinterTile tile = (CreditCardPrinterTile) world.getTileEntity(pos);
            if(tile != null){
                tile.getData().setWorld(world);
                Server server = NetworkComponentUtils.queryServer(tile.getData());

                if(server == null){
                    assert player != null;
                    player.sendMessage(new TranslationTextComponent("translation." + EconomyMod.MOD_ID + ".chat.no_connection"), player.getUniqueID());
                    return;
                }

                if(!server.getDatabase().doesTableExist("bank_account"))
                    return;

                Network.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SCPacketSendNetworkComponentData(tile.getData()));

                ArrayList<String> content = new ArrayList<>();
                content.add(data.getAccountNumber());
                content.add(data.getOwner());
                content.add(data.getPin());
                content.add(Integer.toString(data.getBalance()));
                content.add(data.getDateOfBirth());
                content.add("false");
                content.add(Entry.getCurrentTimestamp());
                content.add("");

                Entry entry = new Entry(content);
                server.getDatabase().getTable("bank_account").insert(entry);

                //drop result item if occupied
                ItemStack resultStack = tile.getItemHandler().getStackInSlot(1);
                if(resultStack.getItem() == ModItems.CREDIT_CARD.get()){
                    ItemEntity cardEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), resultStack);
                    world.addEntity(cardEntity);
                }

                //remove blank credit card
                tile.getItemHandler().getStackInSlot(0).shrink(1);

                //create new credit card and put it in result slot
                ItemStack stack = new ItemStack(ModItems.CREDIT_CARD.get(), 1);

                CompoundNBT nbt = new CompoundNBT();
                nbt.putString("owner", data.getOwner());
                nbt.putString("accountNumber", data.getAccountNumber());
                nbt.putString("validity", data.getValidity());
                nbt.putString("pin", data.getPin());

                stack.setTag(nbt);
                tile.getItemHandler().setStackInSlot(1, stack);

                //update slot occupation
                tile.getIntData().set(0, 0);
                tile.getIntData().set(1, 1);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
