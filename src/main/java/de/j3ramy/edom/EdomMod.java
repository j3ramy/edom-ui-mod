package de.j3ramy.edom;

import de.j3ramy.edom.block.ModBlocks;
import de.j3ramy.edom.container.ModContainers;
import de.j3ramy.edom.entity.ModEntityTypes;
import de.j3ramy.edom.entity.entities.TestCartEntity;
import de.j3ramy.edom.entity.renderer.TestCartRenderer;
import de.j3ramy.edom.events.ModSoundEvents;
import de.j3ramy.edom.item.ModItems;
import de.j3ramy.edom.screen.*;
import de.j3ramy.edom.tileentity.ModTileEntities;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.block.RailBlock;
import net.minecraft.block.RailState;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.model.MinecartModel;
import net.minecraft.enchantment.PowerEnchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.CommandBlockMinecartEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EdomMod.MOD_ID)
public class EdomMod
{
    public static final String MOD_ID = "edom";

    // Directly reference a log4j logger.
    //private static final Logger LOGGER = LogManager.getLogger();

    public EdomMod() {
        // Register the setup method for modloading
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        //Items registrieren
        ModItems.register(modEventBus);

        //Blocks registrieren
        ModBlocks.register(modEventBus);

        //Tile entities registrieren
        ModTileEntities.register(modEventBus);

        ModContainers.register(modEventBus);

        ModSoundEvents.register(modEventBus);

        ModEntityTypes.register(modEventBus);


        modEventBus.addListener(this::setup);
        // Register the enqueueIMC method for modloading
        //modEventBus.addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        //modEventBus.addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        modEventBus.addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        event.enqueueWork(() -> {
            ScreenManager.registerFactory(ModContainers.ATM_STORAGE_CONTAINER.get(), AtmScreen::new);
            ScreenManager.registerFactory(ModContainers.MONEY_CHANGER_CONTAINER.get(), MoneyChangerScreen::new);
            ScreenManager.registerFactory(ModContainers.MAILBOX_CONTAINER.get(), MailboxScreen::new);
            ScreenManager.registerFactory(ModContainers.FIRST_AID_CABINET_CONTAINER.get(), FirstAidCabinetScreen::new);
            ScreenManager.registerFactory(ModContainers.FIRE_HOSE_BOX_CONTAINER.get(), FireHoseBoxScreen::new);
            ScreenManager.registerFactory(ModContainers.TIME_TRACKING_CONTAINER.get(), TimeTrackingScreen::new);

            RenderTypeLookup.setRenderLayer(ModBlocks.FIREALARM.get(), RenderType.getCutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.FIRST_AID_CABINET.get(), RenderType.getCutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.FIRE_HOSE_BOX.get(), RenderType.getCutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.TIME_TRACKING_TERMINAL.get(), RenderType.getCutout());

        });

        //Register entities
        RenderingRegistry.registerEntityRenderingHandler((EntityType<TestCartEntity>)ModEntityTypes.TEST_CART.get(), TestCartRenderer::new);
    }

    /*
    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {

    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }



    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }

     */
}
