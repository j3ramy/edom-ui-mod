package de.j3ramy.economy.tileentity;

import de.j3ramy.economy.item.ModItems;
import de.j3ramy.economy.utils.data.SwitchData;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SwitchTile extends TileEntity {
    private final ItemStackHandler itemHandler = this.createHandler();
    private final LazyOptional<IItemHandler> handler =  LazyOptional.of(() -> this.itemHandler);

    private SwitchData switchData = new SwitchData(new CompoundNBT());

    public SwitchTile(){
        super(ModTileEntities.SWITCH_TILE.get());

        this.switchData.setItemHandler(this.itemHandler);
    }


    public SwitchData getSwitchData() {
        return this.switchData;
    }

    public void setSwitchData(SwitchData switchData) {
        this.switchData = switchData;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        this.switchData = new SwitchData(nbt.getCompound("data"));

        super.read(state, nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.put("data", this.switchData.getData());

        return super.write(nbt);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return handler.cast();

        return super.getCapability(cap, side);
    }

    private ItemStackHandler createHandler(){
        return new ItemStackHandler(5){
            @Override
            protected void onContentsChanged(int slot) {

            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return stack.getItem() == ModItems.ETHERNET_CABLE.get();
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
        };
    }
}
