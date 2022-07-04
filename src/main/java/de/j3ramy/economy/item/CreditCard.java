package de.j3ramy.economy.item;

import de.j3ramy.economy.container.CreditCardContainer;
import de.j3ramy.economy.network.Network;
import de.j3ramy.economy.network.SCPacketSendCreditCardData;
import de.j3ramy.economy.utils.CreditCardData;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;

public class CreditCard extends Item {
    public CreditCard(Properties props) {
        super(props);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {

        if(!worldIn.isRemote()){
            ItemStack stack = playerIn.getHeldItem(handIn);

            CompoundNBT nbt;
            if(!stack.hasTag())
                return ActionResult.resultPass(stack);
            else
                nbt = stack.getTag();

            assert nbt != null;
            if(nbt.contains("owner") && nbt.contains("accountNumber") && nbt.contains("validity") && nbt.contains("pin")){
                INamedContainerProvider containerProvider = createContainerProvider();
                NetworkHooks.openGui((ServerPlayerEntity) playerIn, containerProvider, playerIn.getPosition());

                CreditCardData data = new CreditCardData(nbt.getString("owner"), nbt.getString("accountNumber"), nbt.getString("validity"), nbt.getString("pin"));
                Network.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) playerIn), new SCPacketSendCreditCardData(data));
            }
        }

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World p_77624_2_, List<ITextComponent> tooltip, ITooltipFlag p_77624_4_) {
        super.addInformation(stack, p_77624_2_, tooltip, p_77624_4_);

        if(stack.hasTag()){
            CompoundNBT nbt = stack.getTag();
            assert nbt != null;
            if(nbt.contains("owner")){
                tooltip.add(new StringTextComponent(TextFormatting.ITALIC + "" + TextFormatting.GRAY + nbt.getString("owner")));
            }
        }
    }

    private INamedContainerProvider createContainerProvider() {
        return new INamedContainerProvider(){

            @Override
            public Container createMenu(int i, PlayerInventory playerInv, PlayerEntity playerEntity) {
                return new CreditCardContainer(i);
            }

            @Override
            public ITextComponent getDisplayName() {
                return new TranslationTextComponent("");
            }
        };
    }
}
