package de.j3ramy.economy.tileentity;

import de.j3ramy.economy.block.ModBlocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntities {
    public static DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, de.j3ramy.economy.EconomyMod.MOD_ID);

    public static RegistryObject<TileEntityType<AtmTile>> ATM_STORAGE_TILE = TILE_ENTITIES.register("atm_tile", () -> TileEntityType.Builder.create(
            AtmTile::new, ModBlocks.ATM.get()).build(null));

    public static RegistryObject<TileEntityType<MoneyChangerTile>> MONEY_CHANGER_TILE = TILE_ENTITIES.register("money_changer_tile", () -> TileEntityType.Builder.create(
            MoneyChangerTile::new, ModBlocks.MONEY_CHANGER.get()).build(null));

    public static RegistryObject<TileEntityType<CreditCardPrinterTile>> CREDIT_CARD_PRINTER_TILE = TILE_ENTITIES.register("credit_card_printer_tile", () -> TileEntityType.Builder.create(
            CreditCardPrinterTile::new, ModBlocks.CREDIT_CART_PRINTER.get()).build(null));

    public static RegistryObject<TileEntityType<ServerTile>> SERVER_TILE = TILE_ENTITIES.register("server_tile", () -> TileEntityType.Builder.create(
            ServerTile::new, ModBlocks.SERVER.get()).build(null));

    public static RegistryObject<TileEntityType<ComputerTile>> COMPUTER_TILE = TILE_ENTITIES.register("computer_tile", () -> TileEntityType.Builder.create(
            ComputerTile::new, ModBlocks.COMPUTER.get()).build(null));

    public static RegistryObject<TileEntityType<SwitchTile>> SWITCH_TILE = TILE_ENTITIES.register("switch_tile", () -> TileEntityType.Builder.create(
            SwitchTile::new, ModBlocks.SWITCH.get()).build(null));

    public static RegistryObject<TileEntityType<NetworkSocketTile>> NETWORK_SOCKET_TILE = TILE_ENTITIES.register("network_socket_tile", () -> TileEntityType.Builder.create(
            NetworkSocketTile::new, ModBlocks.NETWORK_SOCKET.get()).build(null));

    public static RegistryObject<TileEntityType<RouterTile>> ROUTER_TILE = TILE_ENTITIES.register("router_tile", () -> TileEntityType.Builder.create(
            RouterTile::new, ModBlocks.ROUTER.get()).build(null));

    public static void register(IEventBus eventBus){
        TILE_ENTITIES.register(eventBus);
    }
}
