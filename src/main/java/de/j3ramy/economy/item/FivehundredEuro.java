package de.j3ramy.economy.item;

import de.j3ramy.economy.utils.NetworkComponentUtils;
import de.j3ramy.economy.utils.data.NetworkComponentData;
import de.j3ramy.economy.utils.server.Entry;
import de.j3ramy.economy.utils.server.Server;
import de.j3ramy.economy.utils.server.Table;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.ArrayList;

public class FivehundredEuro extends Item {
    public FivehundredEuro(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if(worldIn.isRemote())
            return super.onItemRightClick(worldIn, playerIn, handIn);

        ArrayList<NetworkComponentData> wifis = NetworkComponentUtils.lookForWifi(worldIn, playerIn.getPosition());

        if(wifis.size() == 0)
            return super.onItemRightClick(worldIn, playerIn, handIn);

        NetworkComponentData router = wifis.get(0);
        if(router == null)
            return super.onItemRightClick(worldIn, playerIn, handIn);

        System.out.println(router.getName());

        Server server = NetworkComponentUtils.queryServer(router, worldIn);
        if(server != null){
            for(Table table : server.getDatabase().getTables())
                System.out.println(table.getName());
            System.out.println("----------------------------------------");
            for(Entry entry : server.getDatabase().getTable("user").getAllEntries())
                System.out.println(entry.getColumnsContent());
        }

        //... use server for whatever

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
