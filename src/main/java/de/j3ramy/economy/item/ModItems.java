package de.j3ramy.economy.item;

import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, de.j3ramy.economy.EconomyMod.MOD_ID);

    public static final RegistryObject<Item> CREDIT_CARD = ITEMS.register("credit_card", () -> new CreditCard(new Item.Properties().group(ModItemGroup.ECONOMY_GROUP).maxStackSize(1)));
    public static final RegistryObject<Item> BLANK_CREDIT_CARD = ITEMS.register("blank_credit_card", () -> new BlankCreditCard(new Item.Properties().group(ModItemGroup.ECONOMY_GROUP).maxStackSize(16)));
    public static final RegistryObject<Item> ONE_EURO = ITEMS.register("one_euro", () -> new OneEuro(new Item.Properties().group(ModItemGroup.ECONOMY_GROUP)));
    public static final RegistryObject<Item> TWO_EURO = ITEMS.register("two_euro", () -> new TwoEuro(new Item.Properties().group(ModItemGroup.ECONOMY_GROUP)));
    public static final RegistryObject<Item> FIVE_EURO = ITEMS.register("five_euro", () -> new FiveEuro(new Item.Properties().group(ModItemGroup.ECONOMY_GROUP)));
    public static final RegistryObject<Item> TEN_EURO = ITEMS.register("ten_euro", () -> new TenEuro(new Item.Properties().group(ModItemGroup.ECONOMY_GROUP)));
    public static final RegistryObject<Item> TWENTY_EURO = ITEMS.register("twenty_euro", () -> new TwentyEuro(new Item.Properties().group(ModItemGroup.ECONOMY_GROUP)));
    public static final RegistryObject<Item> FIFTY_EURO = ITEMS.register("fifty_euro", () -> new FiftyEuro(new Item.Properties().group(ModItemGroup.ECONOMY_GROUP)));
    public static final RegistryObject<Item> ONEHUNDRED_EURO = ITEMS.register("onehundred_euro", () -> new OnehundredEuro(new Item.Properties().group(ModItemGroup.ECONOMY_GROUP)));
    public static final RegistryObject<Item> TWOHUNDRED_EURO = ITEMS.register("twohundred_euro", () -> new TwohundredEuro(new Item.Properties().group(ModItemGroup.ECONOMY_GROUP)));
    public static final RegistryObject<Item> FIVEHUNDRED_EURO = ITEMS.register("fivehundred_euro", () -> new FivehundredEuro(new Item.Properties().group(ModItemGroup.ECONOMY_GROUP)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
