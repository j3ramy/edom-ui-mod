package de.j3ramy.edom.block;

import de.j3ramy.edom.container.MailboxContainer;
import de.j3ramy.edom.tileentity.MailboxTile;
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

public class MailboxBlock extends HorizontalBlock {
    public MailboxBlock(Properties props) {
        super(props);
    }

    public MailboxTile tileEntity;

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
            tileEntity = (MailboxTile) world.getTileEntity(pos);

            if(tileEntity == null)
                return ActionResultType.SUCCESS;

            if(player.isCrouching())
                return ActionResultType.SUCCESS;

            Direction blockDirection = getBlockFacing(state.getBlockState());

            if(blockDirection == result.getFace()){
                //Frontside

                if(player.getHeldItemMainhand().isEmpty())
                    return ActionResultType.SUCCESS;

                for(int i = 0; i < tileEntity.SLOT_COUNT; i++){
                    if(tileEntity.getItemHandler().getStackInSlot(i).isEmpty()){
                        tileEntity.getItemHandler().setStackInSlot(i, new ItemStack(player.getHeldItemMainhand().getItem(), player.getHeldItemMainhand().getCount()));
                        player.getHeldItemMainhand().setCount(0);

                        player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "Post eingeworfen"), player.getUniqueID());
                        return ActionResultType.SUCCESS;
                    }
                }

                player.sendMessage(new StringTextComponent(TextFormatting.RED + "Briefkasten voll. Warte, bis er leer ist"), player.getUniqueID());
            }
            else if(blockDirection == result.getFace().getOpposite()){
                //Backside
                INamedContainerProvider containerProvider = createContainerProvider(world, pos);
                NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, tileEntity.getPos());
            }
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        super.onBlockClicked(state, worldIn, pos, player);

        if(!worldIn.isRemote){
            MailboxTile tile = (MailboxTile) worldIn.getTileEntity(pos);

            if(tile == null)
                return;

            if(!isInvEmpty(tile)){
                player.sendMessage(new StringTextComponent(TextFormatting.RED + "Der Briefkasten ist nicht leer"), player.getUniqueID());
                return;
            }

            worldIn.destroyBlock(pos, true);
        }
    }

    private INamedContainerProvider createContainerProvider(World world, BlockPos pos) {
        return new INamedContainerProvider(){

            @Override
            public Container createMenu(int i, PlayerInventory playerInv, PlayerEntity playerEntity) {
                return new MailboxContainer(i, world, pos, playerInv, playerEntity, tileEntity);
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
        return ModTileEntities.MAILBOX_TILE.get().create();
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

     boolean isInvEmpty(MailboxTile tile){
        boolean isEmpty = true;
        for(int i = 0; i < tile.SLOT_COUNT; i++){
            if(!tile.getItemHandler().getStackInSlot(i).isEmpty())
                isEmpty = false;
        }

        return isEmpty;
    }
}
