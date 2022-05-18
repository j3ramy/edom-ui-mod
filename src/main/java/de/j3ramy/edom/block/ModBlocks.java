package de.j3ramy.edom.block;

import de.j3ramy.edom.EdomMod;
import de.j3ramy.edom.item.ModItemGroup;
import de.j3ramy.edom.item.ModItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, EdomMod.MOD_ID);

    public static final RegistryObject<Block> ATM = registerBlock("atm", () ->
            new AtmBlock(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(-1f)));

    public static final RegistryObject<Block> MONEY_CHANGER = registerBlock("money_changer", () ->
            new MoneyChangerBlock(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(-1f)));

    public static final RegistryObject<Block> FIREALARM = registerBlock("firealarm", () ->
            new FireAlarmBlock(AbstractBlock.Properties.create(Material.IRON).notSolid().hardnessAndResistance(1f).tickRandomly()));

    public static final RegistryObject<Block> BOUNDARY_STONE = registerBlock("boundary_stone", () ->
            new BoundaryStoneBlock(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(-1f)));

    public static final RegistryObject<Block> MAILBOX = registerBlock("mailbox", () ->
            new MailboxBlock(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(-1)));

    public static final RegistryObject<Block> FIRST_AID_CABINET = registerBlock("first_aid_cabinet", () ->
            new FirstAidCabinetBlock(AbstractBlock.Properties.create(Material.IRON).notSolid().hardnessAndResistance(1f)));

    public static final RegistryObject<Block> FIRE_HOSE_BOX = registerBlock("fire_hose_box", () ->
            new FireHoseBox(AbstractBlock.Properties.create(Material.IRON).notSolid().hardnessAndResistance(1f)));

    public static final RegistryObject<Block> TIME_TRACKING_TERMINAL = registerBlock("time_tracking_terminal", () ->
            new TimeTrackingBlock(AbstractBlock.Properties.create(Material.IRON).notSolid().hardnessAndResistance(1f)));


    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }

    private static<T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block){

        RegistryObject<T> blockRegister = BLOCKS.register(name, block);
        registerBlockItem(name, blockRegister);

        return blockRegister;
    }

    private static<T extends Block> void registerBlockItem(String name, RegistryObject<T> block){

        Item.Properties props = new Item.Properties();
        switch(name){
            case "atm":
            case "money_changer":
                props.group(ModItemGroup.EDOM_ECONOMY_GROUP); break;
            case "firealarm":
            case "first_aid_cabinet":
            case "fire_hose_box":
                props.group(ModItemGroup.EDOM_SAFETY_GROUP); break;
            case "boundary_stone":
            case "mailbox":
            case "time_tracking_terminal":
                props.group(ModItemGroup.EDOM_CITY_GROUP); break;
        }

        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), props));
    }
}
