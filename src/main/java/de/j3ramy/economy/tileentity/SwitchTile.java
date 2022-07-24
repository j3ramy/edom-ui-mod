package de.j3ramy.economy.tileentity;

import de.j3ramy.economy.item.ModItems;
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
                NetworkComponentData newData = new NetworkComponentData(new CompoundNBT());

                if(nbt == null || !nbt.contains("pos") ||
                        slot == 0 && newData.getComponent() != NetworkComponent.SWITCH ||
                        slot == 0 && newData.getComponent() != NetworkComponent.SERVER ||
                        world.getTileEntity(NBTUtil.readBlockPos(nbt.getCompound("pos"))) == null){

                    data.setPort(slot, newData);
                    data.setPortState(slot, itemHandler.getStackInSlot(slot).isEmpty() ? SwitchData.PortState.NOT_CONNECTED : SwitchData.PortState.CONNECTED_NO_INTERNET);

                    if(portsCopy[slot].getFrom() != BlockPos.ZERO){
                        TileEntity componentTile = world.getTileEntity(portsCopy[slot].getFrom());

                        if(componentTile instanceof RouterTile){
                            ((RouterTile) componentTile).getRouterData().setTo(BlockPos.ZERO);
                        }

                        portsCopy[slot] = new NetworkComponentData(newData.getData());
                    }

                    return;
                }


                newData.setFrom(NBTUtil.readBlockPos(nbt.getCompound("pos")));
                newData.setTo(pos);
                newData.setName(nbt.getString("from"));
                newData.setComponent(NetworkComponent.values()[nbt.getInt("component")]);

                data.setPort(slot, newData);
                data.setPortState(slot, SwitchData.PortState.CONNECTED);

                TileEntity componentTile = world.getTileEntity(newData.getFrom());
                if(componentTile instanceof RouterTile){
                    ((RouterTile) componentTile).getRouterData().setTo(pos);
                }

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

    private void checkComponentConnection(){

    }
}