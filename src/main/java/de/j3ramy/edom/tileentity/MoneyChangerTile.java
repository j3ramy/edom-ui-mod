package de.j3ramy.edom.tileentity;

import de.j3ramy.edom.item.FiveEuro;
import de.j3ramy.edom.item.ModItems;
import de.j3ramy.edom.item.TenEuro;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
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

public class MoneyChangerTile extends TileEntity {

    public final ItemStackHandler itemHandler = createHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    public MoneyChangerTile(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public MoneyChangerTile(){
        this(ModTileEntities.MONEY_CHANGER_TILE.get());
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

    private ItemStackHandler createHandler(){
        return new ItemStackHandler(3){
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
                changeMoney();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {

                switch(slot){
                    case 0: return stack.getItem() == ModItems.FIVE_EURO.get() || stack.getItem() == ModItems.TEN_EURO.get();
                    case 1:
                    case 2:
                        return false;
                }

                return super.isItemValid(slot, stack);
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if(!isItemValid(slot, stack)){
                    return stack;
                }

                return super.insertItem(slot, stack, simulate);
            }

            public void changeMoney(){
                int sizeOneEuro = itemHandler.getStackInSlot(1).getCount();
                int sizeTwoEuro = itemHandler.getStackInSlot(2).getCount();


                if(itemHandler.getStackInSlot(0).getItem() instanceof FiveEuro){
                    itemHandler.getStackInSlot(0).shrink(1);
                    itemHandler.setStackInSlot(1, new ItemStack(ModItems.ONE_EURO.get(), sizeOneEuro + 1));
                    itemHandler.setStackInSlot(2, new ItemStack(ModItems.TWO_EURO.get(), sizeTwoEuro + 2));
                }

                if(itemHandler.getStackInSlot(0).getItem() instanceof TenEuro){
                    itemHandler.getStackInSlot(0).shrink(1);
                    itemHandler.setStackInSlot(1, new ItemStack(ModItems.ONE_EURO.get(), sizeOneEuro + 2));
                    itemHandler.setStackInSlot(2, new ItemStack(ModItems.TWO_EURO.get(), sizeTwoEuro + 4));
                }
            }
        };
    }
}
