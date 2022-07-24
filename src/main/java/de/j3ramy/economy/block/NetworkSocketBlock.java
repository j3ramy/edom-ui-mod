package de.j3ramy.economy.block;

import de.j3ramy.economy.container.NetworkSocketContainer;
import de.j3ramy.economy.tileentity.ModTileEntities;
import de.j3ramy.economy.tileentity.NetworkSocketTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class NetworkSocketBlock extends HorizontalBlock {
    protected static final VoxelShape NS_EAST = Block.makeCuboidShape(15.0D, 2.0D, 6.0D, 16.0D, 6.0D, 10.0D); //X
    protected static final VoxelShape NS_WEST = Block.makeCuboidShape(0.0D, 2.0D, 6.0D, 1.0D, 6.0D, 10.0D);//X
    protected static final VoxelShape NS_SOUTH = Block.makeCuboidShape(6.0D, 2.0D, 15.0D, 10.0D, 6.0D, 16.0D);//X
    protected static final VoxelShape NS_NORTH = Block.makeCuboidShape(6.0D, 2.0D, 0.0D, 10.0D, 6.0D, 1.0D);//X

    public NetworkSocketBlock(Properties props) {
        super(props);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing());
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch(state.get(HORIZONTAL_FACING)) {
            case NORTH:
                return NS_NORTH;
            case SOUTH:
                return NS_SOUTH;
            case WEST:
                return NS_WEST;
            case EAST:
            default:
                return NS_EAST;
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {

        if(!world.isRemote){
            System.out.println("TEST");
            NetworkSocketTile tileEntity = (NetworkSocketTile) world.getTileEntity(pos);

            if(tileEntity == null)
                return ActionResultType.SUCCESS;

            INamedContainerProvider containerProvider = createContainerProvider(world, pos);
            NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, tileEntity.getPos());
        }

        return ActionResultType.SUCCESS;
    }

    private INamedContainerProvider createContainerProvider(World world, BlockPos pos) {
        return new INamedContainerProvider(){

            @Override
            public Container createMenu(int i, PlayerInventory playerInv, PlayerEntity playerEntity) {
                return new NetworkSocketContainer(i, playerInv, (NetworkSocketTile) world.getTileEntity(pos));
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
        return ModTileEntities.NETWORK_SOCKET_TILE.get().create();
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.offset(state.get(HORIZONTAL_FACING))).isSolid();
    }
}
