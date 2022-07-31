package de.j3ramy.economy.block;

import de.j3ramy.economy.EconomyMod;
import de.j3ramy.economy.item.ModItemGroup;
import de.j3ramy.economy.item.ModItems;
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
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, EconomyMod.MOD_ID);

    public static final RegistryObject<Block> ATM = registerBlock("atm", () ->
            new AtmBlock(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(-1f)));

    public static final RegistryObject<Block> MONEY_CHANGER = registerBlock("money_changer", () ->
            new MoneyChangerBlock(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(-1f)));

    public static final RegistryObject<Block> CREDIT_CART_PRINTER = registerBlock("credit_card_printer", () ->
            new CreditCardPrinterBlock(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(10f).notSolid().setRequiresTool()));

    public static final RegistryObject<Block> SERVER = registerBlock("server", () ->
            new ServerBlock(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(10f).notSolid().setRequiresTool()));

    public static final RegistryObject<Block> COMPUTER = registerBlock("computer", () ->
            new ComputerBlock(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(10f).setRequiresTool()));

    public static final RegistryObject<Block> ROUTER = registerBlock("router", () ->
            new RouterBlock(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(10f).notSolid().setRequiresTool()));

    public static final RegistryObject<Block> SWITCH = registerBlock("switch", () ->
            new SwitchBlock(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(10f).setRequiresTool()));

    public static final RegistryObject<Block> NETWORK_SOCKET = registerBlock("network_socket", () ->
            new NetworkSocketBlock(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(10f).notSolid().setRequiresTool()));


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
        props.group(ModItemGroup.ECONOMY_GROUP);

        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), props));
    }
}
