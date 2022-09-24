package de.j3ramy.edomui.debug;

import de.j3ramy.edomui.EdomUiMod;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EdomUiMod.MOD_ID);

    public static final RegistryObject<Item> DEBUG_ITEM = ITEMS.register("debug_item", () -> new DebugItem(new Item.Properties().group(ModItemGroup.DEBUG_GROUP).maxStackSize(1)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
