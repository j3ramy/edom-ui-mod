package de.j3ramy.economy.network;

import de.j3ramy.economy.tileentity.ServerTile;
import de.j3ramy.economy.tileentity.SwitchTile;
import de.j3ramy.economy.utils.data.SwitchData;
import de.j3ramy.economy.utils.server.Server;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.function.Supplier;

public class CSPacketSendSwitchData {
    private final SwitchData data;
    private final BlockPos pos;

    public CSPacketSendSwitchData(PacketBuffer buf){
        this.data = new SwitchData(Objects.requireNonNull(buf.readCompoundTag()));
        this.pos = buf.readBlockPos();
    }

    public CSPacketSendSwitchData(SwitchData server, BlockPos pos){
        this.data = server;
        this.pos = pos;
    }

    public void toBytes(PacketBuffer buf){
        buf.writeCompoundTag(data.getData());
        buf.writeBlockPos(pos);
    }
    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() ->{
            World world = Objects.requireNonNull(ctx.get().getSender()).world;

            SwitchTile tile = (SwitchTile) world.getTileEntity(pos);

            if(tile != null){
                tile.setSwitchData(data);

                for(int i = 0; i < SwitchData.PORT_COUNT; i++){
                    tile.getItemHandler().setStackInSlot(i, tile.getItemHandler().getStackInSlot(i));
                }

                Network.INSTANCE.send(PacketDistributor.PLAYER.with(() -> ctx.get().getSender()), new SCPacketSendSwitchData(data));
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
