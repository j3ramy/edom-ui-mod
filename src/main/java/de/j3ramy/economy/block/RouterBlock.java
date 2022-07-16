package de.j3ramy.economy.block;

import de.j3ramy.economy.container.CreditCardPrinterContainer;
import de.j3ramy.economy.tileentity.CreditCardPrinterTile;
import de.j3ramy.economy.tileentity.ModTileEntities;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.security.cert.X509Certificate;
import java.util.stream.Stream;

public class RouterBlock extends DirectionalBlock {

    protected static final VoxelShape X_POSITIVE = Block.makeCuboidShape(0.0D, 4.0D, 4.0D, 2.0D, 12.0D, 12.0D); //X
    protected static final VoxelShape X_NEGATIVE = Block.makeCuboidShape(14.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D);//X
    protected static final VoxelShape Y_POSITIVE = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 2.0D, 12.0D); //X
    protected static final VoxelShape Y_NEGATIVE = Block.makeCuboidShape(4.0D, 14.0D, 4.0D, 12.0D, 16.0D, 12.0D); //X
    protected static final VoxelShape Z_POSITIVE = Block.makeCuboidShape(4.0D, 4.0D, 0.0D, 12.0D, 12.0D, 2.0D);//X
    protected static final VoxelShape Z_NEGATIVE = Block.makeCuboidShape(4.0D, 4.0D, 14.0D, 12.0D, 12.0D, 16.0D);//X

    public RouterBlock(Properties props) {
        super(props);
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.UP));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        Vector3i dir = state.get(FACING).getDirectionVec();

        if(dir.getX() == 1)
            return X_POSITIVE;

        if(dir.getX() == -1)
            return X_NEGATIVE;

        if(dir.getY() == 1)
            return Y_POSITIVE;

        if(dir.getY() == -1)
            return Y_NEGATIVE;

        if(dir.getZ() == 1)
            return Z_POSITIVE;

        if(dir.getZ() == -1)
            return Z_NEGATIVE;

        return Y_POSITIVE;
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.with(FACING, mirrorIn.mirror(state.get(FACING)));
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction direction = context.getFace();
        BlockState blockstate = context.getWorld().getBlockState(context.getPos().offset(direction.getOpposite()));
        return blockstate.matchesBlock(this) && blockstate.get(FACING) == direction ? this.getDefaultState().with(FACING, direction.getOpposite()) : this.getDefaultState().with(FACING, direction);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return null; //ModTileEntities.CREDIT_CARD_PRINTER_TILE.get().create();
    }
}
