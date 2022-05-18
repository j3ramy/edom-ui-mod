package de.j3ramy.edom.tileentity;

import de.j3ramy.edom.events.ModSoundEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

public class FirealarmTile extends TileEntity implements ITickableTileEntity {

    public int yRadius = 4;

    public FirealarmTile(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public FirealarmTile(){
        this(ModTileEntities.FIREALARM_TILE.get());
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);

        if(nbt.contains("yRange"))
            yRadius = nbt.getInt("yRange");
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("yRange", yRadius);
        return super.write(compound);
    }

    final int checkInterval = 100; //5 second interval
    final int radius = 5;
    int ticks = 0;
    boolean isOn = false;
    @Override
    public void tick() {
        assert world != null;
        if(!world.isRemote){
            if(ticks % checkInterval == 0 && !isOn)
                checkFire();

            if(isOn && hasSoundFinished(ticks))
                isOn = false;

            ticks++;
        }
    }

    void checkFire(){
        for(int i = 0; i < radius; i++){
            for(int j = 0; j < radius; j++){
                for(int k = 0; k < yRadius; k++){
                    int translate = radius / 2;
                    BlockPos position = new BlockPos(pos.getX() - translate + i, pos.getY() - k, pos.getZ() - translate + j);

                    assert world != null;
                    Block block = world.getBlockState(position).getBlock();
                    if(block == Blocks.FIRE){
                        //sound.playSound(world, pos);
                        world.playSound(null, pos, ModSoundEvents.FIREALARM.get(), SoundCategory.BLOCKS, 3.5f, 1);
                        ticks = 0;
                        isOn = true;
                    }
                }
            }
        }
    }

    final int soundDuration = 1600; //80 seconds
    boolean hasSoundFinished(int tick){
        return tick > soundDuration;
    }
}
