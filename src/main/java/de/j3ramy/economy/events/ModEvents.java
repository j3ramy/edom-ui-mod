package de.j3ramy.economy.events;

import de.j3ramy.economy.EconomyMod;
import de.j3ramy.economy.gui.screen.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = EconomyMod.MOD_ID)
public class ModEvents {
    public static List<ModScreen> screens = new ArrayList<>();

    @SubscribeEvent
    public static void onMouseClickEvent(GuiScreenEvent.MouseClickedEvent event){
        for(ModScreen screen : ModEvents.screens){
            if(Minecraft.getInstance().currentScreen instanceof ServerScreen){
                screen.onClick(event.getButton());
            }
            else if(Minecraft.getInstance().currentScreen instanceof ComputerScreen){
                screen.onClick(event.getButton());
            }
            else if(Minecraft.getInstance().currentScreen instanceof RouterScreen){
                screen.onClick(event.getButton());
            }
            else if (Minecraft.getInstance().currentScreen instanceof SwitchScreen) {
                screen.onClick(event.getButton());
            }
        }
    }

    @SubscribeEvent
    public static void onKeyPressedEvent(GuiScreenEvent.KeyboardKeyPressedEvent event){
        for(ModScreen screen : ModEvents.screens){
            if(Minecraft.getInstance().currentScreen instanceof ServerScreen){
                screen.onKeyPressed(event.getKeyCode());
            }
            else if(Minecraft.getInstance().currentScreen instanceof ComputerScreen){
                screen.onKeyPressed(event.getKeyCode());
            }
            else if(Minecraft.getInstance().currentScreen instanceof RouterScreen){
                screen.onKeyPressed(event.getKeyCode());
            }
            else if (Minecraft.getInstance().currentScreen instanceof SwitchScreen) {
                screen.onKeyPressed(event.getKeyCode());
            }
        }
    }

    @SubscribeEvent
    public static void onCharTypedEvent(GuiScreenEvent.KeyboardCharTypedEvent.Post event){
        for(ModScreen s : ModEvents.screens){
            if(event.getGui() instanceof ServerScreen){
                s.onCharTyped(event.getCodePoint());
            }
            else if(event.getGui() instanceof ComputerScreen){
                s.onCharTyped(event.getCodePoint());
            }
            else if(event.getGui() instanceof RouterScreen){
                s.onCharTyped(event.getCodePoint());
            }
            else if (event.getGui() instanceof SwitchScreen) {
                s.onCharTyped(event.getCodePoint());
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
