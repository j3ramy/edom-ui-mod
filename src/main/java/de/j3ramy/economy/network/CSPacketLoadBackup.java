package de.j3ramy.economy.network;

import de.j3ramy.economy.tileentity.ServerTile;
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

public class CSPacketLoadBackup {
    BlockPos pos;

    public CSPacketLoadBackup(PacketBuffer buf){
        this.pos = buf.readBlockPos();
    }

    public CSPacketLoadBackup(BlockPos pos){
        this.pos = pos;
    }

    public void toBytes(PacketBuffer buf){
        buf.writeBlockPos(pos);
    }
    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() ->{
            World world = Objects.requireNonNull(ctx.get().getSender()).world;

            ServerTile tile = (ServerTile) world.getTileEntity(pos);
            if(tile == null)
                return;

            ItemStack drive = tile.itemHandler.getStackInSlot(0);

            if(drive.hasTag()){
                assert drive.getTag() != null;
                System.out.println(drive.getTag().getString("name"));
                tile.setServer(new Server(drive.getTag().getCompound("server")));
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
