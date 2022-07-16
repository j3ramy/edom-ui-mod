package de.j3ramy.economy.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class USBFlashDrive extends Item {
    public USBFlashDrive(Properties props) {
        super(props);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        if(!stack.hasTag())
            return;

        assert stack.getTag() != null;
        tooltip.add(new StringTextComponent(TextFormatting.DARK_GREEN + "" + TextFormatting.ITALIC + stack.getTag().getString("name")));
    }
}
