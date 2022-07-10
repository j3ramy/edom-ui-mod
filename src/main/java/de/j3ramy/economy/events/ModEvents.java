package de.j3ramy.economy.events;

import de.j3ramy.economy.EconomyMod;
import de.j3ramy.economy.gui.ModScreen;
import de.j3ramy.economy.screen.ServerScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EconomyMod.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void onMouseClickEvent(GuiScreenEvent.MouseClickedEvent event){
        if(event.getButton() == 0  && Minecraft.getInstance().currentScreen instanceof ServerScreen){ // 0 = Left mouse button
            ((ServerScreen)Minecraft.getInstance().currentScreen).getScreen().onClick();
        }
    }

    @SubscribeEvent
    public static void onMouseScrollEvent(GuiScreenEvent.MouseScrollEvent event){
        if(Minecraft.getInstance().currentScreen instanceof ServerScreen){
            ((ServerScreen)Minecraft.getInstance().currentScreen).getScreen().onScroll((int) event.getScrollDelta());
        }
    }
}
