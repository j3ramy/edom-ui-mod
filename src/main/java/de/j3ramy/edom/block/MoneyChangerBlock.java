package de.j3ramy.edom.block;

import de.j3ramy.edom.container.MoneyChangerContainer;
import de.j3ramy.edom.tileentity.ModTileEntities;
import de.j3ramy.edom.tileentity.MoneyChangerTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class MoneyChangerBlock extends HorizontalBlock {
    public MoneyChangerBlock(Properties props) {
        super(props);
    }

    MoneyChangerTile tileEntity;
    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
    }


    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        if(!world.isRemote){
            tileEntity = (MoneyChangerTile) world.getTileEntity(pos);

            Direction blockDirection = getBlockFacing(state.getBlockState());

            //Check if clicked side is always front
            if(blockDirection != result.getFace())
                return ActionResultType.SUCCESS;

            if(!player.isCrouching()){
                if(tileEntity != null){
                    INamedContainerProvider containerProvider = createContainerProvider(world, pos);
                    NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, tileEntity.getPos());
                }
                else{
                    throw new IllegalStateException("Container provider is missing");
                }
            }
        }
        return ActionResultType.SUCCESS;
    }

    private INamedContainerProvider createContainerProvider(World world, BlockPos pos) {
        return new INamedContainerProvider(){

            @Override
            public Container createMenu(int i, PlayerInventory playerInv, PlayerEntity playerEntity) {
                return new MoneyChangerContainer(i, world, pos, playerInv, playerEntity, tileEntity);
            }

            @Override
            public ITextComponent getDisplayName() {
                return new TranslationTextComponent("screen.edom.money_changer");
            }
        };
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.MONEY_CHANGER_TILE.get().create();
    }

    Direction getBlockFacing(BlockState state){
        if(state.toString().contains("north"))
            return Direction.NORTH;

        if(state.toString().contains("south"))
            return Direction.SOUTH;

        if(state.toString().contains("east"))
            return Direction.EAST;

        if(state.toString().contains("west"))
            return Direction.WEST;

        return Direction.NORTH;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        tooltip.add(new StringTextComponent(TextFormatting.DARK_PURPLE + "Creative only"));
    }
}
