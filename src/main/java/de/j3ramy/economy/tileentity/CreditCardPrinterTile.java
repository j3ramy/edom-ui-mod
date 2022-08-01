package de.j3ramy.economy.tileentity;

import de.j3ramy.economy.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class CreditCardPrinterTile extends NetworkComponentTile {

    private final IIntArray data = new IIntArray() {
        public int get(int index) {
            switch(index){
                case 0: return CreditCardPrinterTile.this.isInsertSlotOccupied ? 1 : 0;
                case 1: return CreditCardPrinterTile.this.isResultSlotOccupied ? 1 : 0;
            }

            return -1;
        }

        public void set(int index, int value) {
            switch(index){
                case 0: CreditCardPrinterTile.this.isInsertSlotOccupied = value == 1; break;
                case 1: CreditCardPrinterTile.this.isResultSlotOccupied = value == 1; break;
            }

        }
        public int size() {
            return 1;
        }
    };

    public IIntArray getIntData() {
        return this.data;
    }

    private boolean isInsertSlotOccupied = false;
    private boolean isResultSlotOccupied = false;

    private final ItemStackHandler itemHandler = createHandler();

    public ItemStackHandler getItemHandler() {
        return this.itemHandler;
    }

    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    public CreditCardPrinterTile(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public CreditCardPrinterTile(){
        this(ModTileEntities.CREDIT_CARD_PRINTER_TILE.get());
    }


    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        itemHandler.deserializeNBT(nbt.getCompound("inv"));

        this.isInsertSlotOccupied = nbt.getBoolean("isInsertSlotOccupied");
        this.isResultSlotOccupied = nbt.getBoolean("isResultSlotOccupied");

        super.read(state, nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.put("inv", itemHandler.serializeNBT());

        nbt.putBoolean("isInsertSlotOccupied", this.isInsertSlotOccupied);
        nbt.putBoolean("isResultSlotOccupied", this.isResultSlotOccupied);

        return super.write(nbt);
    }

    public String generateName(){
        return "CCP-" + (100000 + new Random().nextInt(999999));
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        //Place code for setting that atm storage is only clickable from one side...
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return handler.cast();

        return super.getCapability(cap, side);
    }

    final int slotCount = 2;
    private ItemStackHandler createHandler(){
        return new ItemStackHandler(slotCount){
            @Override
            protected void onContentsChanged(int slot) {

                assert world != null;
                if(!world.isRemote()){
                    switch(slot){
                        case 0:
                            if(itemHandler.getStackInSlot(0).getItem() == Items.AIR)
                                data.set(0, 0);

                            if(itemHandler.getStackInSlot(0).getItem() == ModItems.BLANK_CREDIT_CARD.get())
                                data.set(0, 1);

                            break;
                        case 1:
                            if(itemHandler.getStackInSlot(1).getItem() == Items.AIR)
                                data.set(1, 0);

                            if(itemHandler.getStackInSlot(1).getItem() == ModItems.CREDIT_CARD.get())
                                data.set(1, 1);

                            break;
                    }
                }

                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                switch(slot){
                    case 0: return stack.getItem() == ModItems.BLANK_CREDIT_CARD.get();
                    case 1: return stack.getItem() == Items.AIR;
                }

                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if(!isItemValid(slot, stack)){
                    return stack;
                }

                return super.insertItem(slot, stack, simulate);
            }
        };
    }

}
