package de.j3ramy.economy.block;

import de.j3ramy.economy.container.CreditCardPrinterContainer;
import de.j3ramy.economy.item.ModItems;
import de.j3ramy.economy.tileentity.CreditCardPrinterTile;
import de.j3ramy.economy.tileentity.ModTileEntities;
import de.j3ramy.economy.utils.NetworkComponentUtils;
import de.j3ramy.economy.utils.enums.NetworkComponent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
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
import net.minecraft.util.Hand;
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

public class CreditCardPrinterBlock extends HorizontalBlock {

    static final VoxelShape SHAPE_N = Stream.of(
            Block.makeCuboidShape(10, 0, 3.625, 11, 0.5, 4.625),
            Block.makeCuboidShape(4, 0.5, 2.625, 12, 8.5, 14.625),
            Block.makeCuboidShape(7, 0.5, 1.625, 11, 5.5, 2.625),
            Block.makeCuboidShape(4, 0.5, 14.625, 9, 8.5, 15.625),
            Block.makeCuboidShape(5, 8.5, 3.625, 11, 9, 13.625),
            Block.makeCuboidShape(7, 3.5, 1.375, 11, 4.5, 1.625),
            Block.makeCuboidShape(10, 0, 12.625, 11, 0.5, 13.625),
            Block.makeCuboidShape(5, 0, 12.625, 6, 0.5, 13.625),
            Block.makeCuboidShape(5, 0, 3.625, 6, 0.5, 4.625)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    static final VoxelShape SHAPE_W = Stream.of(
            Block.makeCuboidShape(3.3125, 0, 4.3125, 4.3125, 0.5, 5.3125),
            Block.makeCuboidShape(2.3125, 0.5, 3.3125, 14.3125, 8.5, 11.3125),
            Block.makeCuboidShape(1.3125, 0.5, 4.3125, 2.3125, 5.5, 8.3125),
            Block.makeCuboidShape(14.3125, 0.5, 6.3125, 15.3125, 8.5, 11.3125),
            Block.makeCuboidShape(3.3125, 8.5, 4.3125, 13.3125, 9, 10.3125),
            Block.makeCuboidShape(1.0625, 3.5, 4.3125, 1.3125, 4.5, 8.3125),
            Block.makeCuboidShape(12.3125, 0, 4.3125, 13.3125, 0.5, 5.3125),
            Block.makeCuboidShape(12.3125, 0, 9.3125, 13.3125, 0.5, 10.3125),
            Block.makeCuboidShape(3.3125, 0, 9.3125, 4.3125, 0.5, 10.3125)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    static final VoxelShape SHAPE_S = Stream.of(
            Block.makeCuboidShape(5, 0, 12, 6, 0.5, 13),
            Block.makeCuboidShape(4, 0.5, 2, 12, 8.5, 14),
            Block.makeCuboidShape(5, 0.5, 14, 9, 5.5, 15),
            Block.makeCuboidShape(7, 0.5, 1, 12, 8.5, 2),
            Block.makeCuboidShape(5, 8.5, 3, 11, 9, 13),
            Block.makeCuboidShape(5, 3.5, 15, 9, 4.5, 15.25),
            Block.makeCuboidShape(5, 0, 3, 6, 0.5, 4),
            Block.makeCuboidShape(10, 0, 3, 11, 0.5, 4),
            Block.makeCuboidShape(10, 0, 12, 11, 0.5, 13)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    static final VoxelShape SHAPE_E = Stream.of(
            Block.makeCuboidShape(11.6875, 0, 9.3125, 12.6875, 0.5, 10.3125),
            Block.makeCuboidShape(1.6875, 0.5, 3.3125, 13.6875, 8.5, 11.3125),
            Block.makeCuboidShape(13.6875, 0.5, 6.3125, 14.6875, 5.5, 10.3125),
            Block.makeCuboidShape(0.6875, 0.5, 3.3125, 1.6875, 8.5, 8.3125),
            Block.makeCuboidShape(2.6875, 8.5, 4.3125, 12.6875, 9, 10.3125),
            Block.makeCuboidShape(14.6875, 3.5, 6.3125, 14.9375, 4.5, 10.3125),
            Block.makeCuboidShape(2.6875, 0, 9.3125, 3.6875, 0.5, 10.3125),
            Block.makeCuboidShape(2.6875, 0, 4.3125, 3.6875, 0.5, 5.3125),
            Block.makeCuboidShape(11.6875, 0, 4.3125, 12.6875, 0.5, 5.3125)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    public CreditCardPrinterBlock(Properties props) {
        super(props);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(!worldIn.isRemote){
            CreditCardPrinterTile tileEntity = (CreditCardPrinterTile) worldIn.getTileEntity(pos);

            if(tileEntity != null){
                if(tileEntity.getData() != null && tileEntity.getData().getName().isEmpty())
                    tileEntity.getData().setName(tileEntity.generateName());

                ItemStack stack = player.getHeldItemMainhand();
                if(stack.getItem() == ModItems.ETHERNET_CABLE.get()){
                    NetworkComponentUtils.onCableInteract(tileEntity, player, stack, pos, NetworkComponent.CCP);
                }
                else{
                    INamedContainerProvider containerProvider = createContainerProvider(worldIn, pos);
                    NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, tileEntity.getPos());
                }
            }



            /*
            StringTextComponent text = new StringTextComponent("Click Me");
            Style style = text.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://google.de")).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("HOVER")));

            player.sendMessage(text.setStyle(style), player.getUniqueID());

             */
        }

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
        return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    private INamedContainerProvider createContainerProvider(World world, BlockPos pos) {
        return new INamedContainerProvider(){

            @Override
            public Container createMenu(int i, PlayerInventory playerInv, PlayerEntity playerEntity) {
                return new CreditCardPrinterContainer(i, playerInv, (CreditCardPrinterTile) world.getTileEntity(pos));
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
        return ModTileEntities.CREDIT_CARD_PRINTER_TILE.get().create();
    }
}
