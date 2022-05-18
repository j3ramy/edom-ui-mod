package de.j3ramy.edom.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FirstAidKit extends Item {
    public FirstAidKit(Properties props) {
        super(props);
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn) {
        super.onCreated(stack, worldIn, playerIn);

        if(!worldIn.isRemote){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDateTime now = LocalDateTime.now();

            CompoundNBT nbt = new CompoundNBT();
            nbt.putString("expire_date", dtf.format(now));

            stack.setTag(nbt);
        }
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        context.getPlayer().addPotionEffect(new EffectInstance(Effects.REGENERATION, 200, 3));
        context.getItem().shrink(1);
        return super.onItemUse(context);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World p_77624_2_, List<ITextComponent> tooltip, ITooltipFlag p_77624_4_) {
        super.addInformation(stack, p_77624_2_, tooltip, p_77624_4_);

        CompoundNBT nbt = stack.getTag();
        if(nbt != null && nbt.contains("expire_date")){
            tooltip.add(new StringTextComponent("Hergestellt am: " + nbt.getString("expire_date")));
        }
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return 1;
    }
}
