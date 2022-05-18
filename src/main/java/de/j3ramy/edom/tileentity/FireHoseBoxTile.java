package de.j3ramy.edom.tileentity;

import de.j3ramy.edom.item.ModItems;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.inventory.AnvilScreen;
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

public class FireHoseBoxTile extends TileEntity {

    public final ItemStackHandler itemHandler = createHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    //For data sync between tile entity and screen
    private final IIntArray data = new IIntArray() {
        public int get(int index) {
            return FireHoseBoxTile.this.waterLevel;
        }

        public void set(int index, int value) {
            FireHoseBoxTile.this.waterLevel = value;
        }

        public int size() {
            return 1;
        }
    };

    private int waterLevel = 0; //static = gleiche Variable und Wert für alle Instanzen -> Kein static
    private static final int MAX_WATER_LEVEL = 45;
    private static final int STEPS = 3;
    private static final int WATER_LEVEL_INTERVAL = MAX_WATER_LEVEL / STEPS;

    private static final int HOSE_SLOT = 0;
    private static final int FILL_SLOT = 1;

    public IIntArray getData() {
        return this.data;
    }

    public FireHoseBoxTile(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public FireHoseBoxTile(){
        this(ModTileEntities.FIRE_HOSE_BOX_TILE.get());
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        itemHandler.deserializeNBT(nbt.getCompound("inv"));

        if(nbt.contains("waterLevel"))
            this.waterLevel = nbt.getInt("waterLevel");

        super.read(state, nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.put("inv", itemHandler.serializeNBT());
        nbt.putInt("waterLevel", this.waterLevel);
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
        return new ItemStackHandler(2){
            @Override
            protected void onContentsChanged(int slot) {
                if(world == null)
                    return;

                if(!isItemValid(slot, getStackInSlot(slot))){
                    return;
                }

                switch (slot){
                    case HOSE_SLOT: refillHose(); break;
                    case FILL_SLOT: fillFireBox(); break;
                }
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                switch(slot){
                    case HOSE_SLOT: return stack.getItem() == ModItems.FIRE_HOSE.get();
                    case FILL_SLOT: return stack.getItem() == Items.WATER_BUCKET;
                }
                return false;
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {

                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    void fillFireBox(){
        if(this.waterLevel < MAX_WATER_LEVEL){
            itemHandler.getStackInSlot(FILL_SLOT).shrink(1);
            this.waterLevel += WATER_LEVEL_INTERVAL;
            data.set(0, this.waterLevel);
        }
    }

    void refillHose(){
        if(this.waterLevel == 0)
            return;

        ItemStack stack = itemHandler.getStackInSlot(HOSE_SLOT);
        if(!stack.isEmpty()){

            if(stack.getDamage() == 0)
                return;

            stack.setDamage(0);
            this.waterLevel -= WATER_LEVEL_INTERVAL;
            data.set(0, this.waterLevel);
        }
    }
}
