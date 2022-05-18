package de.j3ramy.edom.container;

import de.j3ramy.edom.EdomMod;
import de.j3ramy.edom.tileentity.*;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class ModContainers {

    public static DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, EdomMod.MOD_ID);

    public static final RegistryObject<ContainerType<AtmContainer>> ATM_STORAGE_CONTAINER = CONTAINERS.register("atm_container",
            () -> IForgeContainerType.create(((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                World world = inv.player.getEntityWorld();

                return new AtmContainer(windowId, world, pos, inv, inv.player, (AtmTile) world.getTileEntity(pos));
            })));

    public static final RegistryObject<ContainerType<MoneyChangerContainer>> MONEY_CHANGER_CONTAINER = CONTAINERS.register("money_changer_container",
            () -> IForgeContainerType.create(((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                World world = inv.player.getEntityWorld();

                return new MoneyChangerContainer(windowId, world, pos, inv, inv.player, (MoneyChangerTile) world.getTileEntity(pos));
            })));

    public static final RegistryObject<ContainerType<MailboxContainer>> MAILBOX_CONTAINER = CONTAINERS.register("mailbox_container",
            () -> IForgeContainerType.create(((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                World world = inv.player.getEntityWorld();

                return new MailboxContainer(windowId, world, pos, inv, inv.player, (MailboxTile) world.getTileEntity(pos));
            })));

    public static final RegistryObject<ContainerType<FirstAidCabinetContainer>> FIRST_AID_CABINET_CONTAINER = CONTAINERS.register("first_aid_cabinet_container",
            () -> IForgeContainerType.create(((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                World world = inv.player.getEntityWorld();

                return new FirstAidCabinetContainer(windowId, world, pos, inv, inv.player, (FirstAidCabinetTile) world.getTileEntity(pos));
            })));

    public static final RegistryObject<ContainerType<FireHoseBoxContainer>> FIRE_HOSE_BOX_CONTAINER = CONTAINERS.register("fire_hose_box_container",
            () -> IForgeContainerType.create(((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                World world = inv.player.getEntityWorld();

                FireHoseBoxTile te = (FireHoseBoxTile) world.getTileEntity(pos);
                assert te != null;
                return new FireHoseBoxContainer(windowId, world, pos, inv, inv.player, te, Objects.requireNonNull(te.getData()));
            })));

    public static final RegistryObject<ContainerType<TimeTrackingContainer>> TIME_TRACKING_CONTAINER = CONTAINERS.register("time_tracking_container",
            () -> IForgeContainerType.create(((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                World world = inv.player.getEntityWorld();

                TimeTrackingTile te = (TimeTrackingTile) world.getTileEntity(pos);
                assert te != null;
                return new TimeTrackingContainer(windowId, world, pos, inv, inv.player, te);
            })));

    public static void register(IEventBus eventBus){
        CONTAINERS.register(eventBus);
    }
}
