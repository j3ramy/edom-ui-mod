package de.j3ramy.economy.network;

import de.j3ramy.economy.item.ModItems;
import de.j3ramy.economy.tileentity.CreditCardPrinterTile;
import de.j3ramy.economy.utils.NetworkComponentUtils;
import de.j3ramy.economy.utils.data.CreditCardData;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class CSPacketSendCreditCardData {
    private final CreditCardData creditCardData = new CreditCardData();
    private final BlockPos pos;

    public CSPacketSendCreditCardData(PacketBuffer buf){
        this.creditCardData.setOwner(buf.readString());
        this.creditCardData.setAccountNumber(buf.readString());
        this.creditCardData.setValidity(buf.readString());
        this.creditCardData.setPin(buf.readString());

        this.pos = buf.readBlockPos();
    }

    public CSPacketSendCreditCardData(CreditCardData creditCardData, BlockPos pos){
        this.creditCardData.setOwner(creditCardData.getOwner());
        this.creditCardData.setAccountNumber(creditCardData.getAccountNumber());
        this.creditCardData.setValidity(creditCardData.getValidity());
        this.creditCardData.setPin(creditCardData.getPin());

        this.pos = pos;
    }

    public void toBytes(PacketBuffer buf){
        buf.writeString(creditCardData.getOwner());
        buf.writeString(creditCardData.getAccountNumber());
        buf.writeString(creditCardData.getValidity());
        buf.writeString(creditCardData.getPin());

        buf.writeBlockPos(pos);
    }
    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() ->{
            World world = Objects.requireNonNull(ctx.get().getSender()).world;

            CreditCardPrinterTile tile = (CreditCardPrinterTile) world.getTileEntity(pos);
            if(tile != null){
                System.out.println(NetworkComponentUtils.queryServer(tile.getData(), world).getIp());
                /*
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
                nbt.putString("owner", creditCardData.getOwner());
                nbt.putString("accountNumber", creditCardData.getAccountNumber());
                nbt.putString("validity", creditCardData.getValidity());
                nbt.putString("pin", creditCardData.getPin());

                stack.setTag(nbt);
                tile.getItemHandler().setStackInSlot(1, stack);

                //update slot occupation
                tile.getIntData().set(0, 0);
                tile.getIntData().set(1, 1);

                 */
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
