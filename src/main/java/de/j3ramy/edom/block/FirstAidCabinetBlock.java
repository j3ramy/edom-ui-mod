package de.j3ramy.edom.block;

import de.j3ramy.edom.container.FirstAidCabinetContainer;
import de.j3ramy.edom.tileentity.FirstAidCabinetTile;
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
import java.util.stream.Stream;

public class FirstAidCabinetBlock extends HorizontalBlock {

    static final VoxelShape SHAPE_N = Stream.of(
            Block.makeCuboidShape(2, 0, 10, 14, 1, 16),
            Block.makeCuboidShape(3, 5, 11, 13, 6, 15),
            Block.makeCuboidShape(3, 10, 11, 13, 11, 15),
            Block.makeCuboidShape(3, 1, 15, 13, 15, 16),
            Block.makeCuboidShape(3, 1, 10, 13, 15, 11),
            Block.makeCuboidShape(11, 8, 9.5, 12, 9, 10),
            Block.makeCuboidShape(7, 10, 9.8, 8, 13, 10),
            Block.makeCuboidShape(8, 11, 9.8, 9, 12, 10),
            Block.makeCuboidShape(6, 11, 9.8, 7, 12, 10),
            Block.makeCuboidShape(2, 15, 10, 14, 16, 16),
            Block.makeCuboidShape(2, 1, 10, 3, 15, 16),
            Block.makeCuboidShape(13, 1, 10, 14, 15, 16)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    static final VoxelShape SHAPE_W = Stream.of(
            Block.makeCuboidShape(10, 0, 2, 16, 1, 14),
            Block.makeCuboidShape(11, 5, 3, 15, 6, 13),
            Block.makeCuboidShape(11, 10, 3, 15, 11, 13),
            Block.makeCuboidShape(15, 1, 3, 16, 15, 13),
            Block.makeCuboidShape(10, 1, 3, 11, 15, 13),
            Block.makeCuboidShape(9.5, 8, 4, 10, 9, 5),
            Block.makeCuboidShape(9.8, 10, 8, 10, 13, 9),
            Block.makeCuboidShape(9.8, 11, 7, 10, 12, 8),
            Block.makeCuboidShape(9.8, 11, 9, 10, 12, 10),
            Block.makeCuboidShape(10, 15, 2, 16, 16, 14),
            Block.makeCuboidShape(10, 1, 13, 16, 15, 14),
            Block.makeCuboidShape(10, 1, 2, 16, 15, 3)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    static final VoxelShape SHAPE_S = Stream.of(
            Block.makeCuboidShape(2, 0, 0, 14, 1, 6),
            Block.makeCuboidShape(3, 5, 1, 13, 6, 5),
            Block.makeCuboidShape(3, 10, 1, 13, 11, 5),
            Block.makeCuboidShape(3, 1, 0, 13, 15, 1),
            Block.makeCuboidShape(3, 1, 5, 13, 15, 6),
            Block.makeCuboidShape(4, 8, 6, 5, 9, 6.5),
            Block.makeCuboidShape(8, 10, 6, 9, 13, 6.2),
            Block.makeCuboidShape(7, 11, 6, 8, 12, 6.2),
            Block.makeCuboidShape(9, 11, 6, 10, 12, 6.2),
            Block.makeCuboidShape(2, 15, 0, 14, 16, 6),
            Block.makeCuboidShape(13, 1, 0, 14, 15, 6),
            Block.makeCuboidShape(2, 1, 0, 3, 15, 6)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    static final VoxelShape SHAPE_E = Stream.of(
            Block.makeCuboidShape(0, 0, 2, 6, 1, 14),
            Block.makeCuboidShape(1, 5, 3, 5, 6, 13),
            Block.makeCuboidShape(1, 10, 3, 5, 11, 13),
            Block.makeCuboidShape(0, 1, 3, 1, 15, 13),
            Block.makeCuboidShape(5, 1, 3, 6, 15, 13),
            Block.makeCuboidShape(6, 8, 11, 6.5, 9, 12),
            Block.makeCuboidShape(6, 10, 7, 6.2, 13, 8),
            Block.makeCuboidShape(6, 11, 8, 6.2, 12, 9),
            Block.makeCuboidShape(6, 11, 6, 6.2, 12, 7),
            Block.makeCuboidShape(0, 15, 2, 6, 16, 14),
            Block.makeCuboidShape(0, 1, 2, 6, 15, 3),
            Block.makeCuboidShape(0, 1, 13, 6, 15, 14)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    FirstAidCabinetTile tileEntity;

    public FirstAidCabinetBlock(Properties props) {
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
        return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

        if(!worldIn.isRemote){
            worldIn.playSound(null, pos, SoundEvents.BLOCK_BARREL_OPEN, SoundCategory.BLOCKS, 1, 1);
            tileEntity = (FirstAidCabinetTile) worldIn.getTileEntity(pos);

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
                return new FirstAidCabinetContainer(i, world, pos, playerInv, playerEntity, tileEntity);
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
        return ModTileEntities.FIRST_AID_CABINET_TILE.get().create();
    }
}
