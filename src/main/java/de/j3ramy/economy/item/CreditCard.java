package de.j3ramy.economy.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class CreditCard extends Item {
    public CreditCard(Properties props) {

        super(props);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World p_77624_2_, List<ITextComponent> tooltip, ITooltipFlag p_77624_4_) {
        super.addInformation(stack, p_77624_2_, tooltip, p_77624_4_);

        CompoundNBT nbt = stack.getTag();
        if(nbt != null){
            tooltip.add(new StringTextComponent(nbt.getString("owner")));
        }

        tooltip.add(new StringTextComponent(TextFormatting.DARK_PURPLE + "Creative only"));

    }
}
