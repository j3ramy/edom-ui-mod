package de.j3ramy.economy;

import de.j3ramy.economy.block.ModBlocks;
import de.j3ramy.economy.container.ModContainers;
import de.j3ramy.economy.events.ModSoundEvents;
import de.j3ramy.economy.item.ModItems;
import de.j3ramy.economy.network.Network;
import de.j3ramy.economy.screen.AtmScreen;
import de.j3ramy.economy.screen.CreditCardScreen;
import de.j3ramy.economy.screen.CreditCartPrinterScreen;
import de.j3ramy.economy.screen.MoneyChangerScreen;
import de.j3ramy.economy.tileentity.ModTileEntities;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EconomyMod.MOD_ID)
public class EconomyMod
{
    public static final String MOD_ID = "economy";

    // Directly reference a log4j logger.
    //private static final Logger LOGGER = LogManager.getLogger();

    public EconomyMod() {
        // Register the setup method for modloading
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        //Network registrieren
        Network.registerMessages();

        //Items registrieren
        ModItems.register(modEventBus);

        //Blocks registrieren
        ModBlocks.register(modEventBus);

        //Tile entities registrieren
        ModTileEntities.register(modEventBus);

        ModContainers.register(modEventBus);

        ModSoundEvents.register(modEventBus);


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
            ScreenManager.registerFactory(ModContainers.CREDIT_CARD_CONTAINER.get(), CreditCardScreen::new);
            ScreenManager.registerFactory(ModContainers.CREDIT_CARD_PRINTER_CONTAINER.get(), CreditCartPrinterScreen::new);

            RenderTypeLookup.setRenderLayer(ModBlocks.CREDIT_CART_PRINTER.get(), RenderType.getCutout());
        });

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
