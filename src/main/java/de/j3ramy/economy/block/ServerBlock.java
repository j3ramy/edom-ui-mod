/*
* Per hard disk one table
* */

package de.j3ramy.economy.block;

import de.j3ramy.economy.container.ServerContainer;
import de.j3ramy.economy.item.ModItems;
import de.j3ramy.economy.network.Network;
import de.j3ramy.economy.network.SCPacketSendServerData;
import de.j3ramy.economy.tileentity.ModTileEntities;
import de.j3ramy.economy.tileentity.ServerTile;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.logging.log4j.util.PropertySource;
import org.apache.logging.log4j.util.SystemPropertiesPropertySource;

import javax.annotation.Nullable;

public class ServerBlock extends HorizontalBlock {
    public ServerBlock(Properties props) {
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

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {

        if(!world.isRemote){
            ServerTile tileEntity = (ServerTile) world.getTileEntity(pos);
            if(tileEntity == null)
                return ActionResultType.SUCCESS;

            ItemStack stack = player.getHeldItemMainhand();
            if(stack.getItem() == ModItems.ETHERNET_CABLE.get() && tileEntity.getServer().isSet()){
                NetworkComponentUtils.onCableInteract(tileEntity, player, stack, pos, NetworkComponent.SERVER);
            }
            else{
                INamedContainerProvider containerProvider = createContainerProvider(world, pos);
                NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, tileEntity.getPos());

                if(tileEntity.getServer() != null)
                    Network.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SCPacketSendServerData(tileEntity.getServer()));
            }
        }

        return ActionResultType.SUCCESS;
    }

    private INamedContainerProvider createContainerProvider(World world, BlockPos pos) {
        return new INamedContainerProvider(){

            @Override
            public Container createMenu(int i, PlayerInventory playerInv, PlayerEntity playerEntity) {
                return new ServerContainer(i, playerInv, (ServerTile) world.getTileEntity(pos));
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

        NetworkComponentUtils.destroySwitchConnection(worldIn, pos);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.SERVER_TILE.get().create();
    }

}
