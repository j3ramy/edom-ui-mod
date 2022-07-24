package de.j3ramy.economy.item;

import de.j3ramy.economy.utils.enums.NetworkComponent;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EthernetCable extends Item {
    public EthernetCable(Properties props) {
        super(props);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        if(stack.hasTag()){
            CompoundNBT nbt = stack.getTag();

            if(nbt == null || !nbt.contains("from") || !nbt.contains("pos") || !nbt.contains("component"))
                return;

            BlockPos pos = NBTUtil.readBlockPos(nbt.getCompound("pos"));
            String component = NetworkComponent.values()[nbt.getInt("component")].name();
            tooltip.add(new StringTextComponent(TextFormatting.GRAY + component + ": " + nbt.getString("from") + " [" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]"));
        }
    }
}
