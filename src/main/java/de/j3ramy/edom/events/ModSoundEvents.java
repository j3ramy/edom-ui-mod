package de.j3ramy.edom.events;

import de.j3ramy.edom.EdomMod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModSoundEvents {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, EdomMod.MOD_ID);

    //FOR DISTANCE AUDIO CONVERT OGG-FILE TO MONO
    public static final RegistryObject<SoundEvent> FIREALARM = registerSoundEvent("firealarm");
    public static final RegistryObject<SoundEvent> ATM_WITHDRAW = registerSoundEvent("atm_withdraw");
    public static final RegistryObject<SoundEvent> ATM_DEPOSIT = registerSoundEvent("atm_deposit");
    public static final RegistryObject<SoundEvent> TIME_TRACKING_TERMINAL = registerSoundEvent("time_tracking_terminal");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name){
        return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(EdomMod.MOD_ID, name)));
    }

    public static void register(IEventBus eventBus){
        SOUND_EVENTS.register(eventBus);
    }
}
