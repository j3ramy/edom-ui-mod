package de.j3ramy.edom.tileentity;

import de.j3ramy.edom.EdomMod;
import de.j3ramy.edom.block.ModBlocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntities {
    public static DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, EdomMod.MOD_ID);

    public static RegistryObject<TileEntityType<AtmTile>> ATM_STORAGE_TILE = TILE_ENTITIES.register("atm_tile", () -> TileEntityType.Builder.create(
            AtmTile::new, ModBlocks.ATM.get()).build(null));

    public static RegistryObject<TileEntityType<MoneyChangerTile>> MONEY_CHANGER_TILE = TILE_ENTITIES.register("money_changer_tile", () -> TileEntityType.Builder.create(
            MoneyChangerTile::new, ModBlocks.MONEY_CHANGER.get()).build(null));

    public static RegistryObject<TileEntityType<FirealarmTile>> FIREALARM_TILE = TILE_ENTITIES.register("firealarm_tile", () -> TileEntityType.Builder.create(
            FirealarmTile::new, ModBlocks.FIREALARM.get()).build(null));

    public static RegistryObject<TileEntityType<MailboxTile>> MAILBOX_TILE = TILE_ENTITIES.register("mailbox_tile", () -> TileEntityType.Builder.create(
            MailboxTile::new, ModBlocks.MAILBOX.get()).build(null));

    public static RegistryObject<TileEntityType<FirstAidCabinetTile>> FIRST_AID_CABINET_TILE = TILE_ENTITIES.register("first_aid_cabinet_tile",
            () -> TileEntityType.Builder.create(FirstAidCabinetTile::new, ModBlocks.FIRST_AID_CABINET.get()).build(null));

    public static RegistryObject<TileEntityType<FireHoseBoxTile>> FIRE_HOSE_BOX_TILE = TILE_ENTITIES.register("fire_hose_box_tile",
            () -> TileEntityType.Builder.create(FireHoseBoxTile::new, ModBlocks.FIRE_HOSE_BOX.get()).build(null));

    public static RegistryObject<TileEntityType<TimeTrackingTile>> TIME_TRACKING_TILE = TILE_ENTITIES.register("time_tracking_tile",
            () -> TileEntityType.Builder.create(TimeTrackingTile::new, ModBlocks.TIME_TRACKING_TERMINAL.get()).build(null));

    public static void register(IEventBus eventBus){
        TILE_ENTITIES.register(eventBus);
    }
}
