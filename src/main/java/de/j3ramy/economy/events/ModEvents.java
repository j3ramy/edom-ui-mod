package de.j3ramy.economy.events;

import de.j3ramy.economy.EconomyMod;
import de.j3ramy.economy.gui.screen.ComputerScreen;
import de.j3ramy.economy.gui.screen.ModScreen;
import de.j3ramy.economy.gui.screen.ServerScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = EconomyMod.MOD_ID)
public class ModEvents {
    public static List<ModScreen> screens = new ArrayList<>();

    @SubscribeEvent
    public static void onMouseClickEvent(GuiScreenEvent.MouseClickedEvent event){
        if(event.getButton() != 0)
            return;

        for(ModScreen screen : ModEvents.screens){
            if(Minecraft.getInstance().currentScreen instanceof ServerScreen){ // 0 = Left mouse button
                screen.onClick();
            }
            else if(Minecraft.getInstance().currentScreen instanceof ComputerScreen){ // 0 = Left mouse button
                screen.onClick();
            }
        }
    }

    @SubscribeEvent
    public static void onMouseScrollEvent(GuiScreenEvent.MouseScrollEvent event){
        for(ModScreen screen : ModEvents.screens){
            screen.onScroll((int) event.getScrollDelta());
        }
    }

    /*
    @SubscribeEvent
    public static void onMouseScrollEvent(GuiScreenEvent.MouseDragEvent event){
        System.out.println(event.getMouseX() + " | " + event.getMouseY());
    }

     */

}
