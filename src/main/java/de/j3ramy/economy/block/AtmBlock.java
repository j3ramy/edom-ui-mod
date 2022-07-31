package de.j3ramy.economy.block;

import de.j3ramy.economy.container.AtmContainer;
import de.j3ramy.economy.item.CreditCard;
import de.j3ramy.economy.tileentity.AtmTile;
import de.j3ramy.economy.tileentity.ModTileEntities;
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

public class AtmBlock extends HorizontalBlock {
    public AtmBlock(Properties props) {
        super(props);
    }

    public AtmTile tileEntity;

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new StringTextComponent(TextFormatting.DARK_PURPLE + "Creative only"));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        if(!world.isRemote){
            Direction blockDirection = getBlockFacing(state.getBlockState());

            //Check if clicked side is always front
            if(blockDirection != result.getFace())
                return ActionResultType.SUCCESS;

            tileEntity = (AtmTile) world.getTileEntity(pos);

            if(tileEntity == null)
                return ActionResultType.SUCCESS;

            if(player.isCrouching())
                return ActionResultType.SUCCESS;

            if(player.getHeldItemMainhand().getItem() instanceof CreditCard){

                //Has card owner
                if(!player.getHeldItemMainhand().hasTag()){
                    player.sendMessage(new StringTextComponent(TextFormatting.RED + "Karte ist keinem Benutzer zugewiesen"), player.getUniqueID());
                    return ActionResultType.SUCCESS;
                }
                else{
                    INamedContainerProvider containerProvider = createContainerProvider(world, pos);
                    NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, tileEntity.getPos());
                }
            }
        }

        return ActionResultType.SUCCESS;
    }

    private INamedContainerProvider createContainerProvider(World world, BlockPos pos) {
        return new INamedContainerProvider(){

            @Override
            public Container createMenu(int i, PlayerInventory playerInv, PlayerEntity playerEntity) {
                return new AtmContainer(i, world, pos, playerInv, playerEntity, tileEntity);
            }

            @Override
            public ITextComponent getDisplayName() {
                return new TranslationTextComponent("");
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
        return ModTileEntities.ATM_STORAGE_TILE.get().create();
    }

    /*
    @Override
    public void onPlayerDestroy(IWorld world, BlockPos pos, BlockState state) {
        if(!world.isRemote()){
            AtmHttpMethods.deleteAtmInDatabase(pos);
        }
    }
    */

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
}
