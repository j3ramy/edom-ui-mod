package de.j3ramy.economy.container;

import de.j3ramy.economy.EconomyMod;
import de.j3ramy.economy.tileentity.AtmTile;
import de.j3ramy.economy.tileentity.CreditCardPrinterTile;
import de.j3ramy.economy.tileentity.MoneyChangerTile;
import de.j3ramy.economy.tileentity.ServerTile;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainers {

    public static DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, EconomyMod.MOD_ID);

    public static final RegistryObject<ContainerType<AtmContainer>> ATM_STORAGE_CONTAINER = CONTAINERS.register("atm_container",
            () -> IForgeContainerType.create(((windowId, inv, data) -> {

                World world = inv.player.getEntityWorld();
                BlockPos pos = data.readBlockPos();
                return new AtmContainer(windowId, world, pos, inv, inv.player, (AtmTile) world.getTileEntity(pos));
            })));

    public static final RegistryObject<ContainerType<MoneyChangerContainer>> MONEY_CHANGER_CONTAINER = CONTAINERS.register("money_changer_container",
            () -> IForgeContainerType.create(((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                World world = inv.player.getEntityWorld();

                return new MoneyChangerContainer(windowId, inv, inv.player, (MoneyChangerTile) world.getTileEntity(pos));
            })));

    public static final RegistryObject<ContainerType<CreditCardContainer>> CREDIT_CARD_CONTAINER = CONTAINERS.register("credit_card_container",
            () -> IForgeContainerType.create(((windowId, inv, data) -> new CreditCardContainer(windowId))));

    public static final RegistryObject<ContainerType<CreditCardPrinterContainer>> CREDIT_CARD_PRINTER_CONTAINER = CONTAINERS.register("credit_card_printer_container",
            () -> IForgeContainerType.create(((windowId, inv, data) -> new CreditCardPrinterContainer(windowId, inv, (CreditCardPrinterTile) inv.player.getEntityWorld().getTileEntity(data.readBlockPos())))));

    public static final RegistryObject<ContainerType<ServerContainer>> SERVER_CONTAINER = CONTAINERS.register("server_container",
            () -> IForgeContainerType.create(((windowId, inv, data) -> new ServerContainer(windowId, (ServerTile) inv.player.getEntityWorld().getTileEntity(data.readBlockPos())))));

    public static void register(IEventBus eventBus){
        CONTAINERS.register(eventBus);
    }
}
