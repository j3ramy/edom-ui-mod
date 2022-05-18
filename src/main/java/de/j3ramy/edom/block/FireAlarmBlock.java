package de.j3ramy.edom.block;

import de.j3ramy.edom.tileentity.FirealarmTile;
import de.j3ramy.edom.tileentity.ModTileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

public class FireAlarmBlock extends Block {
    private static final VoxelShape SHAPE_N = Stream.of(
            Block.makeCuboidShape(6, 15.5, 6, 10, 16, 10),
            Block.makeCuboidShape(6.5, 15, 6.5, 9.5, 15.5, 9.5),
            Block.makeCuboidShape(7, 14.75, 7, 8, 15, 8)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    public FireAlarmBlock(Properties props) {
        super(props);
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return SHAPE_N;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.FIREALARM_TILE.get().create();
    }

    /*
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState p_180633_3_, @Nullable LivingEntity player, ItemStack p_180633_5_) {
        super.onBlockPlacedBy(world, pos, p_180633_3_, player, p_180633_5_);

        if(!world.isRemote){
            Block topBlock = world.getBlockState(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ())).getBlock();
            if(topBlock == Blocks.AIR){
                world.removeBlock(pos, false);

                ItemStack activeStack = player.getHeldItemMainhand();
                if(activeStack.isEmpty())
                    player.setHeldItem(Hand.MAIN_HAND, new ItemStack(ModBlocks.FIREALARM.get(), 1));
                else{
                    activeStack.setCount(activeStack.getCount() + 1);
                }
            }
        }
    }

     */


    final boolean isYRangeFixed = true;
    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isRemote) {
            if(isYRangeFixed)
                return ActionResultType.SUCCESS;

            FirealarmTile tile = (FirealarmTile) world.getTileEntity(pos);

            //if(tile != null){
            //    if(tile.isOn)
            //        tile.sound.volume = 0;
            //}

            if(tile == null)
                return ActionResultType.SUCCESS;

            switch(tile.yRadius){
                case 3:
                    tile.yRadius = 4;
                    player.sendMessage(new StringTextComponent("Deckenhoehe auf " + TextFormatting.GREEN + tile.yRadius + TextFormatting.WHITE + " gesetzt"), player.getUniqueID());
                    break;
                case 4:
                    tile.yRadius = 5;
                    player.sendMessage(new StringTextComponent("Deckenhoehe auf " + TextFormatting.GREEN + tile.yRadius + TextFormatting.WHITE + " gesetzt"), player.getUniqueID());
                    break;
                case 5:
                    tile.yRadius = 6;
                    player.sendMessage(new StringTextComponent("Deckenhoehe auf " + TextFormatting.YELLOW + tile.yRadius + TextFormatting.WHITE + " gesetzt"), player.getUniqueID());
                    break;
                case 6:
                    tile.yRadius = 10;
                    player.sendMessage(new StringTextComponent("Deckenhoehe auf " + TextFormatting.RED + tile.yRadius + TextFormatting.WHITE + " gesetzt"), player.getUniqueID());
                    break;
                case 10:
                    tile.yRadius = 3;
                    player.sendMessage(new StringTextComponent("Deckenhoehe auf " + TextFormatting.GREEN + tile.yRadius + TextFormatting.WHITE + " gesetzt"), player.getUniqueID());
                    break;
            }

            tile.markDirty();

        }


        return ActionResultType.SUCCESS;
    }


    @Override
    public void addInformation(ItemStack p_190948_1_, @Nullable IBlockReader p_190948_2_, List<ITextComponent> tooltip, ITooltipFlag p_190948_4_) {
        super.addInformation(p_190948_1_, p_190948_2_, tooltip, p_190948_4_);

        if(Screen.hasShiftDown()){
            tooltip.add(new TranslationTextComponent("tooltip.edom.firealarm_shift"));
        }
        else{
            tooltip.add(new TranslationTextComponent("tooltip.edom.firealarm"));
        }
    }
}
