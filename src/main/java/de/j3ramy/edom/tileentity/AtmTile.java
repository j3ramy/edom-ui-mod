package de.j3ramy.edom.tileentity;

import de.j3ramy.edom.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AtmTile extends TileEntity {

    private final ItemStackHandler itemHandler = createHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    public AtmTile(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public AtmTile(){
        this(ModTileEntities.ATM_STORAGE_TILE.get());
    }

    public ItemStackHandler getItemHandler(){
        return itemHandler;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        itemHandler.deserializeNBT(nbt.getCompound("inv"));
        super.read(state, nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.put("inv", itemHandler.serializeNBT());
        return super.write(nbt);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        //Place code for setting that atm storage is only clickable from one side...
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return handler.cast();

        return super.getCapability(cap, side);
    }

    final int slotCount = 8;
    private ItemStackHandler createHandler(){
        return new ItemStackHandler(slotCount){
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                boolean isValid = false;
                for(int i = 0; i < slotCount; i++){
                    if(stack.getItem() == ModItems.FIVE_EURO.get() || stack.getItem() == ModItems.TEN_EURO.get() || stack.getItem() == ModItems.TWENTY_EURO.get()
                            || stack.getItem() == ModItems.FIFTY_EURO.get() || stack.getItem() == ModItems.ONEHUNDRED_EURO.get()
                            || stack.getItem() == ModItems.TWOHUNDRED_EURO.get() || stack.getItem() == ModItems.FIVEHUNDRED_EURO.get())
                    {
                        isValid = true;
                    }
                }

                return isValid;
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

    public void clearAtm(){
        for(int i = 0; i < 8; i++){
            itemHandler.setStackInSlot(i, new ItemStack(Items.AIR));
        }
    }
}
