package de.j3ramy.economy.network;

import de.j3ramy.economy.tileentity.RouterTile;
import de.j3ramy.economy.utils.data.NetworkComponentData;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class CSPacketSendNetworkComponentData {
    private final NetworkComponentData data;
    private final BlockPos pos;

    public CSPacketSendNetworkComponentData(PacketBuffer buf){
        this.data = new NetworkComponentData(Objects.requireNonNull(buf.readCompoundTag()));
        this.pos = buf.readBlockPos();
    }

    public CSPacketSendNetworkComponentData(NetworkComponentData data, BlockPos pos){
        this.data = data;
        this.pos = pos;
    }

    public void toBytes(PacketBuffer buf){
        buf.writeCompoundTag(data.getData());
        buf.writeBlockPos(pos);
    }
    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() ->{
            World world = Objects.requireNonNull(ctx.get().getSender()).world;

            RouterTile tile = (RouterTile) world.getTileEntity(pos);

            if(tile != null){
                tile.setData(data);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
