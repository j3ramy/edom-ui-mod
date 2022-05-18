package de.j3ramy.edom.block;

import de.j3ramy.edom.Utils.Faction;
import de.j3ramy.edom.Utils.Factions;
import de.j3ramy.edom.Utils.Server;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BoundaryStoneBlock extends Block {
    public BoundaryStoneBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        tooltip.add(new StringTextComponent(TextFormatting.DARK_PURPLE + "Creative only"));
    }

    @Override
    public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        super.onBlockClicked(state, worldIn, pos, player);

        if(!worldIn.isRemote){
            if(!Server.IS_DEBUG && !Faction.isInFraction(player.getUniqueID().toString(), Factions.city_hall))
                return;

            worldIn.destroyBlock(pos, true);
        }
    }
}
