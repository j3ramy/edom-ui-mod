package de.j3ramy.edomui.debug;

import de.j3ramy.edomui.EdomUiMod;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainers {

    public static DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, EdomUiMod.MOD_ID);

    public static final RegistryObject<ContainerType<DebugContainer>> DEBUG_CONTAINER = CONTAINERS.register("debug_container",
            () -> IForgeContainerType.create(((windowId, inv, data) -> new DebugContainer(windowId))));


    public static void register(IEventBus eventBus){
        CONTAINERS.register(eventBus);
    }
}
