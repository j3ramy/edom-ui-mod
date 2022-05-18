package de.j3ramy.edom.block;

import de.j3ramy.edom.container.FireHoseBoxContainer;
import de.j3ramy.edom.tileentity.FireHoseBoxTile;
import de.j3ramy.edom.tileentity.ModTileEntities;
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
import net.minecraft.util.SoundEvents;
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
import java.util.Objects;
import java.util.stream.Stream;

public class FireHoseBox extends HorizontalBlock {

    static final VoxelShape SHAPE_N = Stream.of(
            Block.makeCuboidShape(0, 2, 0, 16, 16, 6),
            Block.makeCuboidShape(2, 8, 6, 3, 10, 6.5),
            Block.makeCuboidShape(11, 0, 3, 12, 2, 4),
            Block.makeCuboidShape(11, 0, 0, 12, 1, 3),
            Block.makeCuboidShape(9, 0, 0, 10, 1, 3),
            Block.makeCuboidShape(9, 0, 3, 10, 2, 4)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    static final VoxelShape SHAPE_W = Stream.of(
            Block.makeCuboidShape(0, 2, 0, 6, 16, 16),
            Block.makeCuboidShape(6, 8, 13, 6.5, 10, 14),
            Block.makeCuboidShape(3, 0, 4, 4, 2, 5),
            Block.makeCuboidShape(0, 0, 4, 3, 1, 5),
            Block.makeCuboidShape(0, 0, 6, 3, 1, 7),
            Block.makeCuboidShape(3, 0, 6, 4, 2, 7)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    static final VoxelShape SHAPE_S = Stream.of(
            Block.makeCuboidShape(0, 2, 10, 16, 16, 16),
            Block.makeCuboidShape(13, 8, 9.5, 14, 10, 10),
            Block.makeCuboidShape(4, 0, 12, 5, 2, 13),
            Block.makeCuboidShape(4, 0, 13, 5, 1, 16),
            Block.makeCuboidShape(6, 0, 13, 7, 1, 16),
            Block.makeCuboidShape(6, 0, 12, 7, 2, 13)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    static final VoxelShape SHAPE_E = Stream.of(
            Block.makeCuboidShape(10, 2, 0, 16, 16, 16),
            Block.makeCuboidShape(9.5, 8, 2, 10, 10, 3),
            Block.makeCuboidShape(12, 0, 11, 13, 2, 12),
            Block.makeCuboidShape(13, 0, 11, 16, 1, 12),
            Block.makeCuboidShape(13, 0, 9, 16, 1, 10),
            Block.makeCuboidShape(12, 0, 9, 13, 2, 10)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    public FireHoseBox(Properties props) {
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
            worldIn.playSound(null, pos, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1, 1);
            FireHoseBoxTile tileEntity = (FireHoseBoxTile) worldIn.getTileEntity(pos);

            if(tileEntity == null)
                return ActionResultType.SUCCESS;

            if(player.isCrouching())
                return ActionResultType.SUCCESS;

            INamedContainerProvider containerProvider = createContainerProvider(worldIn, pos);
            NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, tileEntity.getPos());

        }

        return ActionResultType.SUCCESS;
    }

    private INamedContainerProvider createContainerProvider(World world, BlockPos pos) {
        return new INamedContainerProvider(){

            @Override
            public Container createMenu(int i, PlayerInventory playerInv, PlayerEntity playerEntity) {
                FireHoseBoxTile te = (FireHoseBoxTile) world.getTileEntity(pos);


                assert te != null;
                return new FireHoseBoxContainer(i, world, pos, playerInv, playerEntity, te, Objects.requireNonNull(te.getData()));
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
        return ModTileEntities.FIRE_HOSE_BOX_TILE.get().create();
    }
}
