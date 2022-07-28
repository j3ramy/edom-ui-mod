package de.j3ramy.economy.utils;

import de.j3ramy.economy.EconomyMod;
import de.j3ramy.economy.tileentity.NetworkComponentTile;
import de.j3ramy.economy.tileentity.RouterTile;
import de.j3ramy.economy.tileentity.ServerTile;
import de.j3ramy.economy.tileentity.SwitchTile;
import de.j3ramy.economy.utils.data.NetworkComponentData;
import de.j3ramy.economy.utils.data.SwitchData;
import de.j3ramy.economy.utils.enums.NetworkComponent;
import de.j3ramy.economy.utils.server.Server;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import org.antlr.v4.runtime.misc.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class NetworkComponentUtils {
    //region CABLE
    public static void onCableInteract(NetworkComponentTile tileEntity, PlayerEntity player, ItemStack cable, BlockPos pos, NetworkComponent component){
        if(tileEntity.getData().getName().isEmpty() && component != NetworkComponent.SERVER){
            player.sendMessage(new TranslationTextComponent("translation."+ EconomyMod.MOD_ID + ".chat.ethernet_cable.no_name_set"), player.getUniqueID());
            return;
        }

        //disconnect
        if(cable.hasTag()){
            CompoundNBT nbt = cable.getTag();
            if(nbt == null || !nbt.contains("pos"))
                return;

            if(Math.areBlockPosEqual(NBTUtil.readBlockPos(nbt.getCompound("pos")), pos)){
                disconnectCableFromComponent(tileEntity, cable);
                player.sendMessage(new TranslationTextComponent("translation."+ EconomyMod.MOD_ID + ".chat.ethernet_cable.removed_cable"), player.getUniqueID());
            }
            else
                player.sendMessage(new TranslationTextComponent("translation."+ EconomyMod.MOD_ID + ".chat.ethernet_cable.cable_already_connected_to_other"), player.getUniqueID());

            return;
        }

        //check if cable already connected
        if(tileEntity.getData().isSet()){
            player.sendMessage(new TranslationTextComponent("translation."+ EconomyMod.MOD_ID + ".chat.ethernet_cable.cable_already_connected_to_this"), player.getUniqueID());
            return;
        }

        //connect
        tileEntity.getData().setComponent(component);
        connectCableToComponent(tileEntity, cable, pos);

        player.sendMessage(new TranslationTextComponent("translation."+ EconomyMod.MOD_ID + ".chat.ethernet_cable.cable_connected",
                tileEntity.getData().getName(), "(" + tileEntity.getData().getComponent().name() + ")"), player.getUniqueID());
    }

    private static void connectCableToComponent(NetworkComponentTile tileEntity, ItemStack cable, BlockPos pos){
        tileEntity.getData().setFrom(pos);

        CompoundNBT nbt = new CompoundNBT();
        nbt.put("pos", NBTUtil.writeBlockPos(pos));
        nbt.putString("from", tileEntity.getData().getName());
        nbt.putString("component", tileEntity.getData().getComponent().name());
        cable.setTag(nbt);
    }

    private static void disconnectCableFromComponent(NetworkComponentTile tileEntity, ItemStack cable){
        tileEntity.getData().setFrom(BlockPos.ZERO);
        tileEntity.getData().setTo(BlockPos.ZERO);

        cable.setTag(new CompoundNBT());
    }

    //region SWITCH
    public static void destroySwitchConnection(World worldIn, BlockPos pos){
        NetworkComponentTile tileEntity = (NetworkComponentTile) worldIn.getTileEntity(pos);

        if(tileEntity == null)
            return;

        BlockPos toPos = tileEntity.getData().getTo();
        TileEntity toTileEntity = worldIn.getTileEntity(toPos);

        if(toTileEntity instanceof SwitchTile){

            for(int i = 0; i < ((SwitchTile) toTileEntity).getSwitchData().getPorts().length; i++){
                BlockPos portPos = ((SwitchTile) toTileEntity).getSwitchData().getPort(i).getFrom();

                if(Math.areBlockPosEqual(pos, portPos)){
                    ((SwitchTile) toTileEntity).getItemHandler().getStackInSlot(i).setTag(new CompoundNBT());
                }
            }
        }
    }

    public static void connectToPort(int portNr, CompoundNBT nbt, SwitchData data, World world, BlockPos pos){
        NetworkComponentData newData = new NetworkComponentData(new CompoundNBT());
        newData.setFrom(NBTUtil.readBlockPos(nbt.getCompound("pos")));
        newData.setTo(pos);
        newData.setName(nbt.getString("from"));
        newData.setComponent(NetworkComponent.valueOf(nbt.getString("component")));

        data.setPort(portNr, newData);
        data.setPortState(portNr, SwitchData.PortState.CONNECTED);

        TileEntity componentTile = world.getTileEntity(newData.getFrom());
        if(componentTile instanceof NetworkComponentTile){
            ((NetworkComponentTile) componentTile).getData().setTo(pos);
        }
    }

    public static void disconnectFromPort(int portNr, SwitchData data, NetworkComponentData[] portsCopy, ItemStackHandler itemHandler, World world){
        data.setPort(portNr, new NetworkComponentData(new CompoundNBT()));
        data.setPortState(portNr, itemHandler.getStackInSlot(portNr).isEmpty() ? SwitchData.PortState.NOT_CONNECTED : SwitchData.PortState.CONNECTED_NO_INTERNET);

        if(portsCopy[portNr].getFrom() != BlockPos.ZERO){
            TileEntity componentTile = world.getTileEntity(portsCopy[portNr].getFrom());

            if(componentTile instanceof NetworkComponentTile){
                ((NetworkComponentTile) componentTile).getData().setTo(BlockPos.ZERO);
            }

            portsCopy[portNr] = new NetworkComponentData(new CompoundNBT());
        }
    }
    //endregion
    //endregion

    private static final int WIFI_SEARCH_RANGE = 50;
    @NotNull
    public static ArrayList<NetworkComponentData> lookForWifi(@NotNull World world, @NotNull BlockPos pos){
        ArrayList<NetworkComponentData> foundComponents = new ArrayList<>();

        if(world.isRemote())
            return foundComponents;

        BlockPos startPos = new BlockPos(pos.getX() - (WIFI_SEARCH_RANGE / 2), pos.getY() - (WIFI_SEARCH_RANGE / 2), pos.getZ() - (WIFI_SEARCH_RANGE / 2));
        BlockPos endPos = new BlockPos(pos.getX() + (WIFI_SEARCH_RANGE / 2), pos.getY() + (WIFI_SEARCH_RANGE / 2), pos.getZ() + (WIFI_SEARCH_RANGE / 2));
        Iterable<BlockPos> blocks = BlockPos.getAllInBoxMutable(startPos, endPos);

        for(BlockPos blockPos : blocks){
            TileEntity tileEntity = world.getTileEntity(blockPos);

            if(tileEntity instanceof RouterTile){
                NetworkComponentData data = (((RouterTile)tileEntity).getData());
                if(data.emitsWifi())
                    foundComponents.add(data);
            }
        }

        return foundComponents;
    }

    /*
    private enum WifiStrength{
        WEAK,
        NORMAL,
        STRONG
    }

    private static final float WEAK_PERCENTAGE = 0.35f;
    private static final float STRONG_PERCENTAGE = 0.70f;
    public static WifiStrength getWifiStrength(BlockPos routerPos, BlockPos componentPos){
        int distance = routerPos.manhattanDistance(componentPos);
        float percentage = 1 - (float)distance / WIFI_SEARCH_RANGE;
        System.out.println(distance);
        if(percentage >= STRONG_PERCENTAGE)
            return WifiStrength.STRONG;
        else if(percentage >= WEAK_PERCENTAGE)
            return WifiStrength.NORMAL;
        else
            return WifiStrength.WEAK;
    }

     */

    @Nullable
    public static Server getServer(NetworkComponentData connectedRouter, World world){
        if(world == null || !connectedRouter.emitsWifi())
            return null;

        BlockPos switchPos = connectedRouter.getTo();
        SwitchTile switchTile = (SwitchTile) world.getTileEntity(switchPos);

        if(switchTile != null){

            NetworkComponentData port = switchTile.getSwitchData().getPort(0);

            if(port != null && port.getComponent() == NetworkComponent.SERVER){
                ServerTile serverTile = (ServerTile) world.getTileEntity(port.getFrom());

                if(serverTile != null){
                    return serverTile.getServer();
                }
            }
        }

        return null;
    }
}
