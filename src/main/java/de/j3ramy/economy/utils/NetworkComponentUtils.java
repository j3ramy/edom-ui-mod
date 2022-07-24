package de.j3ramy.economy.utils;

import de.j3ramy.economy.EconomyMod;
import de.j3ramy.economy.tileentity.NetworkComponentTile;
import de.j3ramy.economy.tileentity.SwitchTile;
import de.j3ramy.economy.utils.data.NetworkComponentData;
import de.j3ramy.economy.utils.data.SwitchData;
import de.j3ramy.economy.utils.enums.NetworkComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

public class NetworkComponentUtils {
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
}
