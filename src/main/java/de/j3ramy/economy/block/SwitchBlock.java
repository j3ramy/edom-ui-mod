package de.j3ramy.economy.block;

import de.j3ramy.economy.tileentity.ModTileEntities;
import de.j3ramy.economy.tileentity.ServerTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class SwitchBlock extends HorizontalBlock {

    static final VoxelShape SHAPE_N = Stream.of(
            Block.makeCuboidShape(0, 0, 0, 16, 3, 8)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    static final VoxelShape SHAPE_W = Stream.of(
            Block.makeCuboidShape(0, 0, 0, 8, 3, 16)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    static final VoxelShape SHAPE_S = Stream.of(
            Block.makeCuboidShape(0, 0, 8, 16, 3, 16)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    static final VoxelShape SHAPE_E = Stream.of(
            Block.makeCuboidShape(8, 0, 0, 16, 3, 16)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    public SwitchBlock(Properties props) {
        super(props);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

        return ActionResultType.SUCCESS;
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
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.CREDIT_CARD_PRINTER_TILE.get().create();
    }

    @Override
    public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
        super.onPlayerDestroy(worldIn, pos, state);

        if(worldIn.isRemote())
            return;

        System.out.println("Switch destroyed");

        //Scan for server around block
        //...
    }
}
