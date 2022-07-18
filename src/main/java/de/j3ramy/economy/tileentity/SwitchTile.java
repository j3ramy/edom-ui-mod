package de.j3ramy.economy.tileentity;

import de.j3ramy.economy.item.ModItems;
import de.j3ramy.economy.network.Network;
import de.j3ramy.economy.network.SCPacketSendSwitchData;
import de.j3ramy.economy.utils.data.SwitchData;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;
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
            @Override
            public void onContentsChanged(int slot) {
                assert world != null;
                if(world.isRemote())
                    return;

                CompoundNBT nbt = itemHandler.getStackInSlot(slot).getTag();

                if(itemHandler.getStackInSlot(slot).isEmpty()){
                    data.setPort(slot, BlockPos.ZERO);
                    data.setPortState(slot, SwitchData.PortState.NOT_CONNECTED);
                }
                else if(nbt == null || !nbt.contains("pos")){
                    data.setPort(slot, BlockPos.ZERO);
                    data.setPortState(slot, SwitchData.PortState.CONNECTED_NO_INTERNET);
                }
                else{
                    data.setPort(slot, NBTUtil.readBlockPos(nbt.getCompound("pos")));
                    data.setPortState(slot, SwitchData.PortState.CONNECTED);
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
}
