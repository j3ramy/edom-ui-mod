package de.j3ramy.economy.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class EthernetCable extends Item {
    public EthernetCable(Properties props) {
        super(props);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {

        if(worldIn.isRemote())
            return super.onItemRightClick(worldIn, playerIn, handIn);

        CompoundNBT nbt = new CompoundNBT();
        nbt.put("pos", NBTUtil.writeBlockPos(new BlockPos(5, 5, 5)));
        playerIn.getHeldItemMainhand().setTag(nbt);

        playerIn.sendMessage(new StringTextComponent(NBTUtil.readBlockPos(nbt.getCompound("pos")).getCoordinatesAsString()), playerIn.getUniqueID());

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
