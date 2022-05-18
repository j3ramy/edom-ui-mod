package de.j3ramy.edom.item;

import de.j3ramy.edom.http.IdCardHttp;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class IdCard extends Item {

    String[] data = new String[0];

    public IdCard(Properties props) {
        super(props);

    }

    @Override
    public ActionResultType onItemUse(ItemUseContext ctx) {
        if(!ctx.getItem().hasTag())
            return ActionResultType.SUCCESS;

        if(!ctx.getItem().getTag().contains("owner"))
            return ActionResultType.SUCCESS;

        data = IdCardHttp.getCitizenData(ctx.getItem().getTag().getString("owner"));
        return super.onItemUse(ctx);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World p_77624_2_, List<ITextComponent> tooltip, ITooltipFlag p_77624_4_) {
        super.addInformation(stack, p_77624_2_, tooltip, p_77624_4_);

        CompoundNBT nbt = stack.getTag();
        if(nbt != null){

            String bday;

            if(data.length == 0)
                return;

            if(data[2].contains("-")){
                String[] bdayArr = data[2].split("-");
                bday = bdayArr[2] + "." + bdayArr[1] + "." + bdayArr[0];
            }
            else
                bday = data[2];


            String[] gueltigArr = data[5].split("-");
            String gueltig;
            TextFormatting tf = TextFormatting.WHITE;
            if(data[5].contains("Abgelaufen")){
                tf = TextFormatting.RED;
                gueltig = gueltigArr[0];
            }
            else{
                gueltig = gueltigArr[2] + "." + gueltigArr[1] + "." + gueltigArr[0];
            }

            String s = "Vorname: " + data[0] + "\nNachname: " + data[1] + "\nGeburtsdatum: " + bday +
                    "\nHerkunft: " + data[3] + "\nWohnort: " + data[4] + "\n" + "\nGueltig bis: " + tf + gueltig + "\n" + TextFormatting.WHITE + data[6];

            tooltip.add(new StringTextComponent(s));
        }

        tooltip.add(new StringTextComponent(TextFormatting.DARK_PURPLE + "Creative only"));
    }
}
