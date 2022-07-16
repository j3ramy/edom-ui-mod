package de.j3ramy.economy.tileentity;

import de.j3ramy.economy.item.ModItems;
import de.j3ramy.economy.utils.server.Server;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ServerTile extends TileEntity {

    private final IIntArray data = new IIntArray() {
        public int get(int index) {
            switch(index){
                case 0: return ServerTile.this.itemHandler.getStackInSlot(0).getItem() == ModItems.USB_FLASH_DRIVE.get() ? 1 : 0;
                case 1: return ServerTile.this.itemHandler.getStackInSlot(0).hasTag() ? 1 : 0;
            }

            return -1;
        }

        public void set(int index, int value) {

        }
        public int size() {
            return 2;
        }
    };

    public IIntArray getData() {
        return this.data;
    }

    public final ItemStackHandler itemHandler = createHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    private Server server;
    public Server getServer() {
        return this.server;
    }
    public void setServer(Server server) {
        this.server = server;
    }


    public ServerTile(){
        super(ModTileEntities.SERVER_TILE.get());
    }


    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        itemHandler.deserializeNBT(nbt.getCompound("inv"));
        this.server = new Server(nbt.getCompound("server"));
        super.read(state, nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.put("inv", itemHandler.serializeNBT());

        if(this.server == null)
            return super.write(nbt);

        nbt.put("server", this.server.getData());
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
        return new ItemStackHandler(1){
            @Override
            protected void onContentsChanged(int slot) {

            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return stack.getItem() == ModItems.USB_FLASH_DRIVE.get();
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
