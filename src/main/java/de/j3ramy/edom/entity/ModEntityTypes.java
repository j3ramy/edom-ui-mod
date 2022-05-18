package de.j3ramy.edom.entity;

import de.j3ramy.edom.EdomMod;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntityTypes {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, EdomMod.MOD_ID);

    public static final RegistryObject<EntityType<?>> TEST_CART = ENTITIES.register("test_cart",
            () -> EntityType.Builder.create(EntityClassification.MISC).setTrackingRange(8).build("test_cart"));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}
