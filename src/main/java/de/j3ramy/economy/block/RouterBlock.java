package de.j3ramy.economy.block;

import de.j3ramy.economy.EconomyMod;
import de.j3ramy.economy.container.RouterContainer;
import de.j3ramy.economy.item.ModItems;
import de.j3ramy.economy.network.Network;
import de.j3ramy.economy.network.SCPacketSendRouterData;
import de.j3ramy.economy.tileentity.ModTileEntities;
import de.j3ramy.economy.tileentity.RouterTile;
import de.j3ramy.economy.tileentity.SwitchTile;
import de.j3ramy.economy.utils.Math;
import de.j3ramy.economy.utils.enums.NetworkComponent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;

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
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(!world.isRemote){
            RouterTile tileEntity = (RouterTile) world.getTileEntity(pos);
            if(tileEntity == null)
                return ActionResultType.SUCCESS;

            System.out.println(tileEntity.getRouterData().getTo());
            ItemStack stack = player.getHeldItemMainhand();
            if(stack.getItem() == ModItems.ETHERNET_CABLE.get()){
                if(tileEntity.getRouterData().getName().isEmpty()){
                    player.sendMessage(new TranslationTextComponent("translation."+ EconomyMod.MOD_ID + ".chat.ethernet_cable.no_name_set"), player.getUniqueID());
                    return ActionResultType.FAIL;
                }

                //set "to" (not implemented)
                if(stack.hasTag()){
                    CompoundNBT nbt = stack.getTag();
                    if(nbt == null || !nbt.contains("pos"))
                        return ActionResultType.FAIL;

                    //disconnect
                    if(Math.areBlockPosEqual(NBTUtil.readBlockPos(nbt.getCompound("pos")), pos)){
                        stack.setTag(new CompoundNBT());
                        tileEntity.getRouterData().setFrom(BlockPos.ZERO);
                        tileEntity.getRouterData().setTo(BlockPos.ZERO);
                        player.sendMessage(new TranslationTextComponent("translation."+ EconomyMod.MOD_ID + ".chat.ethernet_cable.removed_cable"), player.getUniqueID());
                        return ActionResultType.FAIL;
                    }

                    //tileEntity.getRouterData().setTo(NBTUtil.readBlockPos(stack.getTag().getCompound("pos")));
                    //stack.shrink(1);
                }
                //set "from"
                else{
                    if(!Math.areBlockPosEqual(tileEntity.getRouterData().getFrom(), BlockPos.ZERO)){
                        player.sendMessage(new TranslationTextComponent("translation."+ EconomyMod.MOD_ID + ".chat.ethernet_cable.cable_already_connected"), player.getUniqueID());
                        return ActionResultType.FAIL;
                    }

                    CompoundNBT nbt = new CompoundNBT();
                    nbt.put("pos", NBTUtil.writeBlockPos(pos));
                    nbt.putString("from", tileEntity.getRouterData().getName());
                    nbt.putInt("component", NetworkComponent.ROUTER.ordinal());

                    stack.setTag(nbt);

                    tileEntity.getRouterData().setFrom(NBTUtil.readBlockPos(stack.getTag().getCompound("pos")));
                }

                tileEntity.getRouterData().setComponent(NetworkComponent.ROUTER);
                player.sendMessage(new TranslationTextComponent("translation."+ EconomyMod.MOD_ID + ".chat.ethernet_cable.cable_connected",
                        tileEntity.getRouterData().getName(), "(" + tileEntity.getRouterData().getComponent().name() + ")"), player.getUniqueID());

                return ActionResultType.SUCCESS;
            }

            INamedContainerProvider containerProvider = createContainerProvider(world, pos);
            NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, tileEntity.getPos());

            if(tileEntity.getRouterData() != null)
                Network.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SCPacketSendRouterData(tileEntity.getRouterData()));
        }

        return ActionResultType.SUCCESS;
    }

    private INamedContainerProvider createContainerProvider(World world, BlockPos pos) {
        return new INamedContainerProvider(){

            @Override
            public Container createMenu(int i, PlayerInventory playerInv, PlayerEntity playerEntity) {
                return new RouterContainer(i, (RouterTile) world.getTileEntity(pos));
            }

            @Override
            public ITextComponent getDisplayName() {
                return new TranslationTextComponent("");
            }
        };
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBlockHarvested(worldIn, pos, state, player);

        if(worldIn.isRemote())
            return;

        RouterTile routerTile = (RouterTile) worldIn.getTileEntity(pos);

        if(routerTile == null)
            return;

        BlockPos toPos = routerTile.getRouterData().getTo();
        TileEntity toTileEntity = worldIn.getTileEntity(toPos);

        if(toTileEntity instanceof SwitchTile){

            for(int i = 0; i < ((SwitchTile) toTileEntity).getSwitchData().getPorts().length; i++){
                BlockPos portPos = ((SwitchTile) toTileEntity).getSwitchData().getPort(i).getFrom();

                if(Math.areBlockPosEqual(toPos, portPos)){
                    System.out.println("REMOVE");
                    ((SwitchTile) toTileEntity).getItemHandler().getStackInSlot(i).setTag(new CompoundNBT());
                }
            }
        }
    }

    @Override
    public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
        super.onPlayerDestroy(worldIn, pos, state);


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
        return ModTileEntities.ROUTER_TILE.get().create();
    }
}
