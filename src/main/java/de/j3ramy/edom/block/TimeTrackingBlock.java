package de.j3ramy.edom.block;

import de.j3ramy.edom.container.TimeTrackingContainer;
import de.j3ramy.edom.events.ModSoundEvents;
import de.j3ramy.edom.tileentity.ModTileEntities;
import de.j3ramy.edom.tileentity.TimeTrackingTile;
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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class TimeTrackingBlock extends HorizontalBlock {

    static final VoxelShape SHAPE_N = Stream.of(
            Block.makeCuboidShape(6, 7.7, 0, 6.3, 8, 0.75),
            Block.makeCuboidShape(5, 8, 0, 11, 12, 1),
            Block.makeCuboidShape(5.5, 8.5, 1, 8.5, 10.5, 1.2),
            Block.makeCuboidShape(11, 8.25, 0.25, 11.1, 11.25, 0.75)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    static final VoxelShape SHAPE_W = Stream.of(
            Block.makeCuboidShape(0, 7.7, 9.7, 0.75, 8, 10),
            Block.makeCuboidShape(0, 8, 5, 1, 12, 11),
            Block.makeCuboidShape(1, 8.5, 7.5, 1.12, 10.5, 10.5),
            Block.makeCuboidShape(0.25, 8.25, 4.9, 0.75, 11.25, 5)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();


    static final VoxelShape SHAPE_S = Stream.of(
            Block.makeCuboidShape(9.7, 7.7, 15.25, 10, 8, 16),
            Block.makeCuboidShape(5, 8, 15, 11, 12, 16),
            Block.makeCuboidShape(7.5, 8.5, 14.8, 10.5, 10.5, 15),
            Block.makeCuboidShape(4.9, 8.25, 15.25, 5, 11.25, 15.75)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();


    static final VoxelShape SHAPE_E = Stream.of(
            Block.makeCuboidShape(15.25, 7.7, 6, 16, 8, 6.3),
            Block.makeCuboidShape(15, 8, 5, 16, 12, 11),
            Block.makeCuboidShape(14.8, 8.5, 5.5, 15, 10.5, 8.5),
            Block.makeCuboidShape(15.25, 8.25, 11, 15.75, 11.25, 11.1)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();


    public TimeTrackingBlock(Properties props) {
        super(props);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        switch (state.get(HORIZONTAL_FACING)){
            case SOUTH: return SHAPE_S;
            case EAST: return SHAPE_E;
            case WEST: return SHAPE_W;
            default: return SHAPE_N;
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing());
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

        if(!worldIn.isRemote){
            TimeTrackingTile tileEntity = (TimeTrackingTile) worldIn.getTileEntity(pos);

            if(tileEntity == null)
                return ActionResultType.SUCCESS;

            if(player.isCrouching())
                return ActionResultType.SUCCESS;

            worldIn.playSound(null, pos, ModSoundEvents.TIME_TRACKING_TERMINAL.get(), SoundCategory.BLOCKS, 1, 1);
            //INamedContainerProvider containerProvider = createContainerProvider(worldIn, pos);
            //NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, tileEntity.getPos());

        }

        return ActionResultType.SUCCESS;
    }

    private INamedContainerProvider createContainerProvider(World world, BlockPos pos) {
        return new INamedContainerProvider(){

            @Override
            public Container createMenu(int i, PlayerInventory playerInv, PlayerEntity playerEntity) {
                TimeTrackingTile te = (TimeTrackingTile) world.getTileEntity(pos);


                assert te != null;
                return new TimeTrackingContainer(i, world, pos, playerInv, playerEntity, te);
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
        return ModTileEntities.TIME_TRACKING_TILE.get().create();
    }
}
