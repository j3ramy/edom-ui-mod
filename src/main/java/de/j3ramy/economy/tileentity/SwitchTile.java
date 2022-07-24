package de.j3ramy.economy.tileentity;

import de.j3ramy.economy.item.ModItems;
import de.j3ramy.economy.utils.NetworkComponentUtils;
import de.j3ramy.economy.utils.data.NetworkComponentData;
import de.j3ramy.economy.utils.data.SwitchData;
import de.j3ramy.economy.utils.enums.NetworkComponent;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
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

    private SwitchData data = new SwitchData(new CompoundNBT());

    public SwitchTile(){
        super(ModTileEntities.SWITCH_TILE.get());
    }


    public SwitchData getSwitchData() {
        return this.data;
    }

    public void setSwitchData(SwitchData switchData) {
        this.data = switchData;
    }

    public ItemStackHandler getItemHandler() {
        return this.itemHandler;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        itemHandler.deserializeNBT(nbt.getCompound("inv"));
        this.data = new SwitchData(nbt.getCompound("data"));

        super.read(state, nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.put("inv", itemHandler.serializeNBT());
        nbt.put("data", this.data.getData());

        return super.write(nbt);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return handler.cast();

        return super.getCapability(cap, side);
    }

    public ItemStackHandler createHandler(){
        return new ItemStackHandler(SwitchData.PORT_COUNT){

            private final NetworkComponentData[] portsCopy = new NetworkComponentData[SwitchData.PORT_COUNT];
            @Override
            public void onContentsChanged(int slot) {
                if(world == null || world.isRemote())
                    return;

                this.portsCopy[slot] = data.getPort(slot);
                CompoundNBT nbt = itemHandler.getStackInSlot(slot).getTag();

                //if nbt is null or keys not exist or slot is 0 and cable is not connected to a server
                if(nbt == null || !nbt.contains("pos") || world.getTileEntity(NBTUtil.readBlockPos(nbt.getCompound("pos"))) == null ||
                slot == 0 && nbt.contains("component") && NetworkComponent.valueOf(nbt.getString("component")) != NetworkComponent.SERVER ||
                slot > 0 && nbt.contains("component") && NetworkComponent.valueOf(nbt.getString("component")) == NetworkComponent.SERVER)
                    NetworkComponentUtils.disconnectFromPort(slot, data, portsCopy, itemHandler, world);
                else
                    NetworkComponentUtils.connectToPort(slot, nbt, data, world, pos);

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