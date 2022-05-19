package de.j3ramy.edom.item;

import de.j3ramy.edom.EdomMod;
import de.j3ramy.edom.entity.ModEntityTypes;
import de.j3ramy.edom.entity.entities.TestCartEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.monster.piglin.PiglinBruteEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EdomMod.MOD_ID);

    public static final RegistryObject<Item> CREDIT_CARD = ITEMS.register("credit_card", () -> new CreditCard(new Item.Properties().group(ModItemGroup.EDOM_ECONOMY_GROUP).maxStackSize(1)));
    public static final RegistryObject<Item> ID_CARD = ITEMS.register("id_card", () -> new IdCard(new Item.Properties().group(ModItemGroup.EDOM_CITY_GROUP).maxStackSize(1)));
    public static final RegistryObject<Item> ONE_EURO = ITEMS.register("one_euro", () -> new OneEuro(new Item.Properties().group(ModItemGroup.EDOM_ECONOMY_GROUP)));
    public static final RegistryObject<Item> TWO_EURO = ITEMS.register("two_euro", () -> new TwoEuro(new Item.Properties().group(ModItemGroup.EDOM_ECONOMY_GROUP)));
    public static final RegistryObject<Item> FIVE_EURO = ITEMS.register("five_euro", () -> new FiveEuro(new Item.Properties().group(ModItemGroup.EDOM_ECONOMY_GROUP)));
    public static final RegistryObject<Item> TEN_EURO = ITEMS.register("ten_euro", () -> new TenEuro(new Item.Properties().group(ModItemGroup.EDOM_ECONOMY_GROUP)));
    public static final RegistryObject<Item> TWENTY_EURO = ITEMS.register("twenty_euro", () -> new TwentyEuro(new Item.Properties().group(ModItemGroup.EDOM_ECONOMY_GROUP)));
    public static final RegistryObject<Item> FIFTY_EURO = ITEMS.register("fifty_euro", () -> new FiftyEuro(new Item.Properties().group(ModItemGroup.EDOM_ECONOMY_GROUP)));
    public static final RegistryObject<Item> ONEHUNDRED_EURO = ITEMS.register("onehundred_euro", () -> new OnehundredEuro(new Item.Properties().group(ModItemGroup.EDOM_ECONOMY_GROUP)));
    public static final RegistryObject<Item> TWOHUNDRED_EURO = ITEMS.register("twohundred_euro", () -> new TwohundredEuro(new Item.Properties().group(ModItemGroup.EDOM_ECONOMY_GROUP)));
    public static final RegistryObject<Item> FIVEHUNDRED_EURO = ITEMS.register("fivehundred_euro", () -> new FivehundredEuro(new Item.Properties().group(ModItemGroup.EDOM_ECONOMY_GROUP)));

    public static final RegistryObject<Item> POLICE_GLOVES = ITEMS.register("police_gloves", () -> new PoliceGloves(new Item.Properties().group(ModItemGroup.EDOM_POLICE_GROUP).maxStackSize(1)));
    public static final RegistryObject<Item> FIRST_AID_KIT = ITEMS.register("first_aid_kit", () -> new FirstAidKit(new Item.Properties().group(ModItemGroup.EDOM_SAFETY_GROUP).maxStackSize(16)));
    public static final RegistryObject<Item> SERVICE_CARD = ITEMS.register("service_card", () -> new ServiceCard(new Item.Properties().group(ModItemGroup.EDOM_CITY_GROUP).maxStackSize(1)));
    public static final RegistryObject<Item> FIRE_HOSE = ITEMS.register("fire_hose", () -> new FireHose(new Item.Properties().group(ModItemGroup.EDOM_SAFETY_GROUP).maxStackSize(1)));

    public static final RegistryObject<Item> TEST_CART = ITEMS.register("test_cart", () -> new TestCartItem(new Item.Properties().group(ModItemGroup.COASTER_GROUP).maxStackSize(1)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
